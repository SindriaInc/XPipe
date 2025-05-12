/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner.sys;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Lists.transform;
import java.awt.Color;
import java.io.IOException;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.String.format;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.common.Constants.DMS_MODEL_DEFAULT_CLASS;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.config.EtlConfiguration;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.CardImpl.CardImplBuilder;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.DatabaseRecordValues;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_HTML;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_MARKDOWN;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_OTHER;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_FILENAME;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.report.inner.JasperReportContextService;
import org.cmdbuild.report.inner.ReportHelper;
import org.cmdbuild.report.inner.ReportPreferencesHelperService;
import org.cmdbuild.report.inner.utils.ReportUtils;
import static org.cmdbuild.report.inner.utils.ReportUtils.getBands;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReportImageParamsFromResourcesAndFixReport;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadSubreportParamsFromResourcesAndFixReport;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.userconfig.DateAndFormatPreferences;
import org.cmdbuild.userconfig.DateAndFormatPreferencesImpl;
import org.cmdbuild.userconfig.UserPrefHelper;
import org.cmdbuild.userconfig.UserPrefHelperImpl;
import org.cmdbuild.userconfig.UserPreferencesService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.htmlToString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.markdownToString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewAccessService;
import org.cmdbuild.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SysReportServiceImpl implements SysReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ClasseRepository classeRepository;
    private final UserRepository userRepository;
    private final UserClassService classService;
    private final UserCardService cardService;
    private final CardHistoryService historyService;
    private final LookupService lookupService;
    private final ReportPreferencesHelperService preferencesHelper;
    private final UserPreferencesService userPreferencesService;
    private final ReportHelper reportHelper;
    private final ViewAccessService viewAccessService;
    private final UserDomainService domainService;
    private final JasperReportContextService contextService;
    private final WorkflowService workflowService;
    private final ObjectTranslationService translationService;
    private final EtlConfiguration etlConfiguration;

    public SysReportServiceImpl(
            DaoService dao,
            ClasseRepository classeRepository,
            UserRepository userRepository,
            UserClassService classService,
            UserCardService cardService,
            CardHistoryService historyService,
            LookupService lookupService,
            ReportPreferencesHelperService preferencesHelper,
            UserPreferencesService userPreferencesService,
            ReportHelper reportHelper,
            ViewAccessService viewAccessService,
            UserDomainService domainService,
            JasperReportContextService contextService,
            WorkflowService workflowService,
            ObjectTranslationService translationService,
            EtlConfiguration etlConfiguration) {
        this.dao = checkNotNull(dao);
        this.classeRepository = checkNotNull(classeRepository);
        this.userRepository = checkNotNull(userRepository);
        this.classService = checkNotNull(classService);
        this.cardService = checkNotNull(cardService);
        this.historyService = checkNotNull(historyService);
        this.lookupService = checkNotNull(lookupService);
        this.preferencesHelper = checkNotNull(preferencesHelper);
        this.userPreferencesService = checkNotNull(userPreferencesService);
        this.reportHelper = checkNotNull(reportHelper);
        this.viewAccessService = checkNotNull(viewAccessService);
        this.domainService = checkNotNull(domainService);
        this.contextService = checkNotNull(contextService);
        this.workflowService = checkNotNull(workflowService);
        this.translationService = checkNotNull(translationService);
        this.etlConfiguration = checkNotNull(etlConfiguration);
    }

    @Override
    public DataHandler executeClassSchemaReport(Classe classe, ReportFormat format) {
        try {
            logger.debug("execute class report for class = {} format = {}", classe, format);
            JasperPrint jasperPrint = new ClassSchemaReportHelper(classe).runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, classe.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for class = %s", classe);
        }
    }

    @Override
    public DataHandler executeSchemaReport(ReportFormat format) {
        try {
            JasperPrint jasperPrint = new SchemaReportHelper().runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, "schema_report", format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for the schema");
        }
    }

    @Override
    public DataHandler executeUserClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions) {
        return doExecuteUserClassReport(classe, format, queryOptions, (attrs) -> {
            if (classe.isProcess()) {
                return workflowService.getUserFlowCardsByClasseIdAndQueryOptions(classe.getName(), queryOptions).elements().stream();
            } else {
                return cardService.getUserCards(classe.getName(), queryOptions).elements().stream();
            }
        });
    }

    @Override
    public DataHandler executeUserClassHistoryReport(Classe classe, Long cardId, ReportFormat format, DaoQueryOptions queryOptions, List<HistoryElement> historyTypes) {
        return doExecuteUserHistoryClassReport(classe, cardId, format, queryOptions, getHistoryWithType(classe, cardId, queryOptions, historyTypes));
    }

    @Override
    public DataHandler executeUserClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions, Supplier<Stream<? extends DatabaseRecordValues>> records) {
        return doExecuteUserClassReport(classe, format, queryOptions, (attrs) -> records.get());
    }

    @Override
    public DataHandler executeCardReport(Card card, ReportFormat format) {
        try {
            logger.debug("execute card report for card = {} format = {}", card, format);
            JasperPrint jasperPrint = new CardReportHelper(card).runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, format("%s_%s", card.getClassName(), card.getId()), format);
        } catch (Exception ex) {
            throw new ReportException(ex, "error building report structure for card = %s", card);
        }
    }

    @Override
    public DataHandler executeViewReport(View view, ReportFormat format, DaoQueryOptions queryOptions) {
        try {
            logger.debug("execute view report for view = {} format = {}", view, format);
            JasperPrint jasperPrint = new ClassOrViewReportHelper(
                    view,
                    view.getName(),
                    viewAccessService.getAttributesForView(view),
                    queryOptions,
                    (attrs) -> {
                        logger.debug("execute query for view = {}", view);
                        return viewAccessService.getCards(view, queryOptions).elements().stream();//TODO query only required attrs (?)
                    })
                    .runReport(format);
            logger.debug("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, view.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for view = %s", view);
        }
    }

    private DataHandler doExecuteUserClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions, Function<List<Attribute>, Stream<? extends DatabaseRecordValues>> records) {
        try {
            logger.debug("execute class report for class = {} format = {} with query options = {}", classe, format, queryOptions);
            JasperPrint jasperPrint = new ClassOrViewReportHelper(classe, translationService.translateClassDescription(classe), classe.getAllAttributes(), queryOptions, records)
                    .runReport(format);
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, classe.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for class = %s", classe);
        }
    }

    private DataHandler doExecuteUserHistoryClassReport(Classe classe, Long cardId, ReportFormat format, DaoQueryOptions queryOptions, List<DatabaseRecord> records) {
        try {
            Classe klasse = classeRepository.getClasse(classe);
            logger.debug("execute class report for class = {} format = {} with query options = {}", classe, format, queryOptions);
            JasperPrint jasperPrint = new HistoryClassReportHelper(klasse, translationService.translateClassDescription(classe), classe.getAllAttributes(), queryOptions, records)
                    .runReport(format);
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, format("%s_%s_history", classe.getName(), cardId), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for class = %s", classe);
        }
    }

    private List<DatabaseRecord> getHistoryWithType(Classe classe, Long cardId, DaoQueryOptions queryOptions, List<HistoryElement> historyTypes) {
        List<DatabaseRecord> history = list();
        if (queryOptions.hasAttrs()) {
            history.addAll(queryOptions.getAttrs().stream().flatMap(a -> {
                logger.debug("getting history for {} with type {}", a, historyTypes);
                historyService.getHistoryElementsOnlyChanges(classe.getName(), cardId, DaoQueryOptionsImpl.copyOf(queryOptions).withAttrs(a).build(), historyTypes).forEach(h -> {
                    logger.debug("history is {}", mapToLoggableStringLazy(h.getAllValuesAsMap()));
                });
                return historyService.getHistoryElementsOnlyChanges(classe.getName(), cardId, DaoQueryOptionsImpl.copyOf(queryOptions).withAttrs(a).build(), historyTypes).stream().map(c -> {
                    return (DatabaseRecord) CardImpl.builder().withType(c.getType()).withId(c.getId())
                            .withAttribute("_beginDate", c.getBeginDate())
                            .withAttribute("_endDate", c.getEndDate())
                            .withAttribute("_user", c.getUser())
                            .withAttribute("_userDescription", userRepository.getUserDataByUsernameOrNull(c.getStrippedUser()) == null ? c.getUser() : userRepository.getUserDataByUsernameOrNull(c.getStrippedUser()).getDescription())
                            .withAttribute("_attributeName", a)
                            .withAttribute("_attributeValue", c.get(a))
                            .withAttribute("_historyType", historyService.getHistoryTypeFromRecord(c))
                            .withAttribute(a, c.get(a))
                            .build();
                });
            }).toList());
        } else {
            history.addAll(historyService.getHistoryElements(classe.getName(), cardId, queryOptions, historyTypes).map(c -> {
                logger.debug("history is {}", mapToLoggableStringLazy(c.getAllValuesAsMap()));
                if (c instanceof CMRelation relation) {
                    return (DatabaseRecord) RelationImpl.copyOf(relation)
                            .withAttribute("_beginDate", relation.getBeginDate())
                            .withAttribute("_endDate", relation.getEndDate())
                            .withAttribute("_user", relation.getUser())
                            .withAttribute("_userDescription", userRepository.getUserDataByUsernameOrNull(relation.getStrippedUser()) == null ? relation.getUser() : userRepository.getUserDataByUsernameOrNull(relation.getStrippedUser()).getDescription())
                            .withAttribute("_relation", relation.getDomainWithThisRelationDirection().getDirectDescription())
                            .withAttribute("_historyType", historyService.getHistoryTypeFromRecord(c))
                            .build();
                } else {
                    CardImplBuilder card = CardImpl.copyOf(c)
                            .withAttribute("_beginDate", c.getBeginDate())
                            .withAttribute("_endDate", c.getEndDate())
                            .withAttribute("_user", c.getUser())
                            .withAttribute("_userDescription", userRepository.getUserDataByUsernameOrNull(c.getStrippedUser()) == null ? c.getUser() : userRepository.getUserDataByUsernameOrNull(c.getStrippedUser()).getDescription())
                            .withAttribute("_historyType", historyService.getHistoryTypeFromRecord(c));
                    if (classe.isProcess()) {
                        card.withAttribute("_activityName", c.get("ActivityDefinitionId"))
                                .withAttribute("_activityExecutor", c.get("NextExecutor"))
                                .withAttribute("_activityState", "FlowStatus");
                    }
                    return (DatabaseRecord) card.build();
                }
            }).elements());
        }
        historyService.sortHistoryRecords(history);
        return history;
    }

    private static int getSizeFromAttribute(Attribute attr) {
        return switch (attr.getType().getName()) {
            case BOOLEAN ->
                5;
            case REGCLASS, REFERENCE, INET, LOOKUP, LOOKUPARRAY, FOREIGNKEY, TIME, STRINGARRAY ->
                20;
            case DECIMAL, DOUBLE, FLOAT, INTEGER, LONG ->
                15;
            case TEXT ->
                50;
            case TIMESTAMP ->
                16;
            case DATE ->
                10;
            case FILE ->
                30;
            case STRING, LINK ->
                attr.hasMaxLenght() ? min(max(4, attr.getMaxLength()), 40) : 40;
            default ->
                0;
        };

    }

    private class ClassOrViewReportHelper extends GenericReportHelper {

        private final Object subject;
        private final String subjectName;
        private final List<Attribute> attrs;
        private final Function<List<Attribute>, Stream<? extends DatabaseRecordValues>> dataSupplier;
        private int x;

        public ClassOrViewReportHelper(Object subject, String subjectName, Collection<Attribute> attributes, DaoQueryOptions queryOptions, Function<List<Attribute>, Stream<? extends DatabaseRecordValues>> dataSupplier) {
            this.subject = checkNotNull(subject);
            this.subjectName = checkNotBlank(subjectName);
            this.dataSupplier = checkNotNull(dataSupplier);

            if (queryOptions.hasAttrs()) {
                Map<String, Attribute> attrsByName = map(attributes, Attribute::getName);
                attrs = queryOptions.getAttrs().stream().map(n -> checkNotNull(attrsByName.get(n), "attribute not found for name =< %s >", n)).collect(toImmutableList());
                attrs.forEach(a -> checkArgument(a.hasServiceReadPermission(), "permission denied for attribute = %s", a));
            } else {
                attrs = attributes.stream().filter(Attribute::showInGrid).filter(Attribute::hasUiReadPermission).collect(toImmutableList());
            }
        }

        public JasperPrint runReport(ReportFormat reportFormat) throws JRException {
            logger.debug("run report for subject = {}, selected attributes = {}", subject, transform(attrs, Attribute::getName));
            JasperDesign jasperDesign = loadReportFromResources(equal(reportFormat, ReportFormat.CSV) ? "CMDBuild_list_csv.jrxml" : "CMDBuild_list.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            attrs.forEach(rethrowConsumer(a -> jasperDesign.addField(buildFieldForAttr(a))));

            JRBand detailBand = jasperDesign.getDetailSection().getBands()[0];
            JRBand columnHeader = jasperDesign.getColumnHeader();

            int pageWidth = jasperDesign.getPageWidth() - 30;
            int lineHeight = 17;

            if (equal(reportFormat, ReportFormat.CSV)) {
                int width = pageWidth / attrs.size();
                x = 0;
                attrs.forEach(a -> {
                    JRDesignStaticText staticText = new JRDesignStaticText();
                    staticText.setText(translationService.translateAttributeDescription(a));
                    staticText.setX(x);
                    staticText.setHeight(lineHeight);
                    staticText.setWidth(width);
                    columnHeader.getChildren().add(staticText);

                    JRDesignTextField textField = createTextFieldForAttribute(a, false, reportFormat);
                    textField.setX(x);
                    textField.setHeight(lineHeight);
                    textField.setWidth(width);
                    detailBand.getChildren().add(textField);
                    x += width;
                });
            } else {
                int sumOfWeights = attrs.stream().mapToInt(a -> getSizeFromAttribute(a)).sum();
                x = 0;
                attrs.forEach(a -> {
                    int width = getSizeFromAttribute(a) * pageWidth * 95 / sumOfWeights / 100;
                    logger.trace("add col header for attr = {} with width = {}", a.getName(), width);
                    JRDesignStaticText staticText = new JRDesignStaticText();
                    staticText.setText(translationService.translateAttributeDescription(a));
                    staticText.setForecolor(Color.WHITE);
                    staticText.setY(0);
                    staticText.setX(x);
                    staticText.setHeight(lineHeight);
                    staticText.setWidth(width);
                    columnHeader.getChildren().add(staticText);

                    logger.trace("add col for attr = {} with width = {}", a.getName(), width);
                    JRDesignTextField textField = createTextFieldForAttribute(a, false);
                    textField.setX(x);
                    textField.setY(2);
                    textField.setWidth(width);
                    textField.setHeight(lineHeight);
                    detailBand.getChildren().add(textField);
                    x += width;
                });
            }

            Stopwatch stopwatch = Stopwatch.createStarted();
            List<Map<String, ?>> data = dataSupplier.apply(attrs).map(c -> mapOf(String.class, Object.class).accept(m -> {
                attrs.forEach(a -> {
                    try {
                        m.put(getFieldNameForAttr(a), getReportValueFromCard(c, a));
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SysReportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                logger.trace("loaded record = {} with data = \n\n{}\n", c, mapToLoggableStringLazy((Map) m));
            })).collect(toList()); //TODO filter param, sorter param (??)

            logger.debug("retrieved {} records in {}", data.size(), toUserDuration(stopwatch.elapsed()));

            Map<String, Object> params = map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams).with(
                    "Card_List_Title", subjectName
            );

            return compileAndFillReport(jasperDesign, params, new JRMapCollectionDataSource(data));
        }
    }

    private class HistoryClassReportHelper extends GenericReportHelper {

        private final Classe subject;
        private final String subjectName;
        private final List<Attribute> attrs;
        private final List<DatabaseRecord> records;
        private int x;

        public HistoryClassReportHelper(Classe subject, String subjectName, Collection<Attribute> attributes, DaoQueryOptions queryOptions, List<DatabaseRecord> records) {
            this.subject = checkNotNull(subject);
            this.subjectName = checkNotBlank(subjectName);
            this.records = checkNotNull(records);

            attrs = list(
                    AttributeImpl.builder().withName("_historyType").withDescription("Type").withType(new StringAttributeType()).withOwner(subject).build(),
                    AttributeImpl.builder().withName("_beginDate").withDescription("BeginDate").withType(new DateTimeAttributeType()).withOwner(subject).build(),
                    AttributeImpl.builder().withName("_endDate").withDescription("EndDate").withType(new DateTimeAttributeType()).withOwner(subject).build(),
                    AttributeImpl.builder().withName("_user").withDescription("User").withType(new StringAttributeType()).withOwner(subject).build(),
                    AttributeImpl.builder().withName("_userDescription").withDescription("UserDescription").withType(new StringAttributeType()).withOwner(subject).build()
            );
            if (queryOptions.hasAttrs()) {
                Map<String, Attribute> attrsByName = map(attributes, Attribute::getName);
                queryOptions.getAttrs().stream().forEach(n -> {
                    checkNotNull(attrsByName.get(n), "attribute not found for name =< %s >", n);
                    checkArgument(attrsByName.get(n).hasServiceReadPermission(), "permission denied for attribute = %s", n);
                });
                attrs.addAll(list(
                        AttributeImpl.builder().withName("_attributeName").withDescription("AttributeName").withType(new StringAttributeType()).withOwner(subject).build(),
                        AttributeImpl.builder().withName("_attributeValue").withDescription("AttributeValue").withType(new StringAttributeType()).withOwner(subject).build()
                ));
            } else {
                attrs.addAll(listOf(Attribute.class).accept(l -> {
                    if (subject.isProcess()) {
                        l.add(AttributeImpl.builder().withName("_activityName").withDescription("ActivityName").withType(new StringAttributeType()).withOwner(subject).build());
                        l.add(AttributeImpl.builder().withName("_activityExecutor").withDescription("ActivityExecutor").withType(new StringAttributeType()).withOwner(subject).build());
                        l.add(AttributeImpl.builder().withName("_activityState").withDescription("ActivityState").withType(new StringAttributeType()).withOwner(subject).build());
                    }
                    if (records.stream().anyMatch(r -> set("reference", "relation").contains(r.get("_historyType", String.class)))) {
                        l.add(AttributeImpl.builder().withName("_relation").withDescription("Relation").withType(new StringAttributeType()).withOwner(subject).build());
                    }
                }));
            }
        }

        public JasperPrint runReport(ReportFormat reportFormat) throws JRException {
            logger.debug("run report for subject = {}, selected attributes = {}", subject, transform(attrs, Attribute::getName));
            JasperDesign jasperDesign = loadReportFromResources(equal(reportFormat, ReportFormat.CSV) ? "CMDBuild_list_csv.jrxml" : "CMDBuild_list.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            attrs.forEach(rethrowConsumer(a -> jasperDesign.addField(buildFieldForAttr(a))));

            JRBand detailBand = jasperDesign.getDetailSection().getBands()[0];
            JRBand columnHeader = jasperDesign.getColumnHeader();

            int pageWidth = jasperDesign.getPageWidth() - 30;
            int lineHeight = 17;

            if (equal(reportFormat, ReportFormat.CSV)) {
                int width = pageWidth / attrs.size();
                x = 0;
                attrs.forEach(a -> {
                    JRDesignStaticText staticText = new JRDesignStaticText();
                    staticText.setText(a.getDescription());
                    staticText.setX(x);
                    staticText.setHeight(lineHeight);
                    staticText.setWidth(width);
                    columnHeader.getChildren().add(staticText);

                    JRDesignTextField textField = createTextFieldForAttribute(a, false, reportFormat);
                    textField.setX(x);
                    textField.setHeight(lineHeight);
                    textField.setWidth(width);
                    detailBand.getChildren().add(textField);
                    x += width;
                });
            } else {
                int sumOfWeights = attrs.stream().mapToInt(a -> getSizeFromAttribute(a)).sum();
                x = 0;
                attrs.forEach(a -> {
                    int width = getSizeFromAttribute(a) * pageWidth * 95 / sumOfWeights / 100;
                    logger.trace("add col header for attr = {} with width = {}", a.getName(), width);
                    JRDesignStaticText staticText = new JRDesignStaticText();
                    staticText.setText(a.getDescription());
                    staticText.setForecolor(Color.WHITE);
                    staticText.setY(0);
                    staticText.setX(x);
                    staticText.setHeight(lineHeight);
                    staticText.setWidth(width);
                    columnHeader.getChildren().add(staticText);

                    logger.trace("add col for attr = {} with width = {}", a.getName(), width);
                    JRDesignTextField textField = createTextFieldForAttribute(a, false);
                    textField.setX(x);
                    textField.setY(2);
                    textField.setWidth(width);
                    textField.setHeight(lineHeight);
                    detailBand.getChildren().add(textField);
                    x += width;
                });
            }

            Stopwatch stopwatch = Stopwatch.createStarted();
            List<Map<String, ?>> data = records.stream().map(c -> mapOf(String.class, Object.class).accept(m -> {
                attrs.forEach(a -> {
                    try {
                        m.put(getFieldNameForAttr(a), getReportValueFromHistoryCard(subject, c, a));
                    } catch (IOException ex) {
                        throw new ReportException(ex);
                    }
                });
                logger.trace("loaded record = {} with data = \n\n{}\n", c, mapToLoggableStringLazy((Map) m));
            })).collect(toList());

            logger.debug("retrieved {} records in {}", data.size(), toUserDuration(stopwatch.elapsed()));
            Map<String, Object> params = map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams).with(
                    "Card_List_Title", subjectName
            );

            return compileAndFillReport(jasperDesign, params, new JRMapCollectionDataSource(data));
        }

    }

    private class ClassSchemaReportHelper extends GenericReportHelper {

        private final Set<String> SPECIAL_ATTRS = ImmutableSet.of(ATTR_IDTENANT, ATTR_IDCLASS, ATTR_ID);
        private final Classe classe;
        private final List<Attribute> attrs;

        public ClassSchemaReportHelper(Classe classe) {
            this.classe = checkNotNull(classe);
            attrs = classe.getAllAttributes().stream()
                    .filter(a -> !SPECIAL_ATTRS.contains(a.getName()))
                    .filter(Attribute::hasServiceReadPermission).collect(toImmutableList());
        }

        public JasperPrint runReport() throws JRException {
            List<Domain> domains = domainService.getUserDomains().stream().filter((d) -> d.isDomainForClasse(classe)).collect(toList());
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_class_schema.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign),
                    subreportParams = loadSubreportParamsFromResourcesAndFixReport(jasperDesign);

            List domainList = domains.stream().map(a -> map(
                    "domainname", a.getName(),
                    "domainclass1", a.getSourceClass().getName(),
                    "domainclass2", a.getTargetClass().getName(),
                    "domaincardinality", serializeDomainCardinality(a.getCardinality())
            )).collect(toList());

            List attributeList = attrs.stream().map(a -> map(
                    "attributename", a.getName(),
                    "attributetype", a.getType().getName().toString(),
                    "attributelength", a.isOfType(STRING) ? firstNotNull(a.getMaxLength(), Integer.MAX_VALUE) : 0,
                    "attributenotnull", a.isMandatory(),
                    "attributeunique", a.isUnique(),
                    "attributelookup", a.isOfType(LOOKUP) ? a.getType().as(LookupAttributeType.class).getLookupTypeName() : "",
                    "attributereferencedomain", a.isOfType(REFERENCE) ? a.getType().as(ReferenceAttributeType.class).getDomainName() : ""
            )).collect(toList());
            Map<String, Object> d = map(
                    "classData", map(
                            "classname", classe.getName(),
                            "classdescription", classe.getDescription(),
                            "classisprocess", classe.isProcess(),
                            "classsuperclass", classe.getParentOrNull(),
                            "classissuperclass", classe.isSuperclass()
                    ),
                    "attributeList", new JRBeanCollectionDataSource(attributeList, false),
                    "domainList", new JRBeanCollectionDataSource(domainList, false)
            );
            return compileAndFillReport(jasperDesign, map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams), new JRMapCollectionDataSource(list(d)));
//            return compileAndFillReport(jasperDesign, map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams).with(subreportParams).with("class", classe.getName()), null);
        }
    }

    private class SchemaReportHelper extends GenericReportHelper {

        public JasperPrint runReport() throws JRException {
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_database_schema.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            List<Classe> classes = classService.getAllUserClasses();

            List classList = classes.stream().map(a -> map(
                    "classname", a.getName(),
                    "classdescription", a.getDescription(),
                    "isprocess", a.isProcess(),
                    "issuperclass", a.isSuperclass(),
                    "superclass", a.getParentOrNull(),
                    "attributes", new JRBeanCollectionDataSource(a.getAllAttributes().stream().map(
                            f -> map(
                                    "name", f.getName(),
                                    "type", f.getType().getName().toString(),
                                    "length", f.isOfType(STRING) ? firstNotNull(f.getMaxLength(), Integer.MAX_VALUE) : 0,
                                    "notnull", f.isMandatory(),
                                    "unique", f.isUnique(),
                                    "lookup", f.isOfType(LOOKUP) ? f.getType().as(LookupAttributeType.class).getLookupTypeName() : "",
                                    "reference", f.isOfType(REFERENCE) ? f.getType().as(ReferenceAttributeType.class).getDomainName() : ""
                            )).collect(toList()), false),
                    "domains", new JRBeanCollectionDataSource(domainService.getUserDomainsForClasse(a.getName()).stream().map(
                            f -> map(
                                    "domname", f.getName(),
                                    "class1", f.getSourceClassName(),
                                    "class2", f.getTargetClassName(),
                                    "cardinality", serializeDomainCardinality(f.getCardinality())
                            )).collect(toList()), false))).collect(toList());

            List lookupklist = lookupService.getAllTypes().stream().map(a -> map(
                    "lookuptype", a.getName(),
                    "values", new JRBeanCollectionDataSource(lookupService.getAllLookup(a.getName()).stream().map(
                            f -> map(
                                    "lookupname", f.getCode(),
                                    "lookupdesc", f.getDescription()
                            )).collect(toList()), false)
            )).collect(toList());

            Map<String, Object> data = map(
                    "classList", new JRBeanCollectionDataSource(classList, false),
                    "classList2", new JRBeanCollectionDataSource(classList, false),
                    "lookuplist", new JRBeanCollectionDataSource(lookupklist, false)
            );

            return compileAndFillReport(jasperDesign, map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams), new JRMapCollectionDataSource(list(data)));
        }

    }

    private class CardReportHelper extends GenericReportHelper {

        private final Card card;

        private int width, height, x, y;

        public CardReportHelper(Card card) {
            this.card = checkNotNull(card);
        }

        public JasperPrint runReport() throws JRException, Exception {
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_card_detail.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);
            Classe classe = card.getType();
            Map<String, Object> data = map();

            JRSection detailSection = jasperDesign.getDetailSection();
            JRDesignBand detailBand = (JRDesignBand) detailSection.getBands()[0];
            x = 0;
            y = 0;
            width = jasperDesign.getPageWidth() - 30 * 2;
            height = 20;
            List<AttributeGroupData> attributeGroups = classe.getAttributeGroups();
            attributeGroups.forEach(rethrowConsumer(g -> {
                data.put(g.getName(), translationService.translateAttributeGroupDescription(classe, g));
                jasperDesign.addField(buildFieldForAttributeGroup(g, classe));
                JRDesignTextField textFieldAttrGrouping = createTextFieldForAttributeGrouping(g.getName());
                textFieldAttrGrouping.setHeight(height);
                textFieldAttrGrouping.setWidth(width);
                textFieldAttrGrouping.setX(x);
                textFieldAttrGrouping.setY(y);
                textFieldAttrGrouping.setBackcolor(Color.BLUE);
                textFieldAttrGrouping.setBold(true);
                detailBand.getChildren().add(textFieldAttrGrouping);
                y += 20;
                classe.getActiveServiceAttributes().stream().filter(att -> att.hasGroup() && att.hasUiReadPermission()).forEach(rethrowConsumer(a -> {
                    if (a.getGroupName().equals(g.getName())) {
                        Object value = getReportValueFromCard(card, a);
                        data.put(getFieldNameForAttr(a), value);
                        jasperDesign.addField(buildFieldForAttr(a));
                        JRDesignTextField textField = createTextFieldForAttribute(a, true);
                        textField.setHeight(height);
                        textField.setWidth(width);
                        textField.setX(x + 10);
                        textField.setY(y);
                        detailBand.getChildren().add(textField);

                        y += 20;
                    }
                }));
            }));
            y += 20;
            classe.getActiveServiceAttributes().stream().filter(att -> !att.hasGroup() && att.hasUiReadPermission()).forEach(rethrowConsumer(a -> {
                Object value = getReportValueFromCard(card, a);
                data.put(getFieldNameForAttr(a), value);
                jasperDesign.addField(buildFieldForAttr(a));
                JRDesignTextField textField = createTextFieldForAttribute(a, true);
                textField.setHeight(height);
                textField.setWidth(width);
                textField.setX(x);
                textField.setY(y);
                detailBand.getChildren().add(textField);

                y += 20;
            }));

            int detailHeight = y + 5;
            detailBand.setHeight(detailHeight);

            int totalBandsHeight = getBands(jasperDesign).stream().mapToInt(JRBand::getHeight).sum();
            if (totalBandsHeight > jasperDesign.getPageHeight()) {
                jasperDesign.setPageHeight(totalBandsHeight);
            }
            logger.trace("report data = \n\n{}\n", mapToLoggableStringLazy(data));

            Map<String, Object> params = map(preferencesHelper.getUserPreferencesReportParams()).with(imageParams).with(
                    "Card_Detail_Title", format("%s - %s", translationService.translateClassDescription(classe), card.getDescription())
            );

            data.put("domains", new JRBeanCollectionDataSource(domainService.getUserRelationsForCard(classe.getName(), card.getId(), DaoQueryOptionsImpl.builder().build())
                    .stream().map(
                            f -> {
                                String domDescription;
                                if (f.isDirect()) {
                                    domDescription = translationService.translateDomainDirectDescription(f.getDomainWithThisRelationDirection().getName(), f.getDomainWithThisRelationDirection().getDirectDescription());
                                } else {
                                    domDescription = translationService.translateDomainInverseDescription(f.getDomainWithThisRelationDirection().getName(), f.getDomainWithThisRelationDirection().getDirectDescription());
                                }
                                return map(
                                        "domname", domDescription,
                                        "class", f.getTargetClassName(),
                                        "startdate", f.getBeginDate(),
                                        "code", f.getTargetCode(),
                                        "description", f.getTargetDescription()
                                );
                            }).collect(toList()), false));
            return compileAndFillReport(jasperDesign, params, new JRMapCollectionDataSource(list(data)));
        }

    }

    private JRDesignTextField createTextFieldForAttribute(Attribute attribute, boolean addLabel) {
        return createTextFieldForAttribute(attribute, addLabel, null);
    }

    private JRDesignTextField createTextFieldForAttribute(Attribute attribute, boolean addLabel, @Nullable ReportFormat format) {
        String expr = format("$F{%s#%s}", attribute.getOwner().getName(), attribute.getName());
        if (addLabel) {
            expr = format("\"%s : \" + ( %s == null ? \"\" : %s )", escapeJava(translationService.translateAttributeDescription(attribute)), expr, expr);
        }
        JRDesignExpression varExpr = new JRDesignExpression();
        varExpr.setText(expr);
        JRDesignTextField field = new JRDesignTextField();
        field.setExpression(varExpr);
        field.setBlankWhenNull(true);
        field.setX(0);
        field.setY(0);
        field.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
        if (!equal(format, ReportFormat.CSV)) {
            field.setForecolor(Color.BLACK);
            field.setBackcolor(Color.GRAY);
            field.setPositionType(PositionTypeEnum.FLOAT);
            field.setMarkup(JRCommonText.MARKUP_HTML);
        }
        return field;
    }

    private JRDesignTextField createTextFieldForAttributeGrouping(String groupingName) {
        String expr = format("$F{%s}", groupingName);
        JRDesignExpression varExpr = new JRDesignExpression();
        varExpr.setText(expr);
        JRDesignTextField field = new JRDesignTextField();
        field.setExpression(varExpr);
        field.setBlankWhenNull(true);
        field.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
        field.setForecolor(Color.BLACK);
        field.setBackcolor(Color.CYAN);
        field.setPositionType(PositionTypeEnum.FLOAT);
        field.setMarkup(JRCommonText.MARKUP_HTML);
        field.setX(20);
        field.setY(20);
        return field;
    }

    private static Class getJavaClassForReportFromAttribute(Attribute attr) {
        return switch (attr.getType().getName()) {
            case BOOLEAN ->
                Boolean.class;
            case CHAR ->
                Character.class;
            case REFERENCE, FOREIGNKEY, LOOKUP, LOOKUPARRAY, INET, STRING, GEOMETRY, TEXT, TIME, TIMESTAMP, DATE, DECIMAL, DOUBLE, FLOAT, LINK, FILE, INTEGER, LONG ->
                String.class;
            case STRINGARRAY ->
                String[].class;
            default ->
                Object.class; //TODO check this
        };
    }

    public final String passwordDotSymbol = "&#8226;";

    private abstract class GenericReportHelper {

        private final UserPrefHelper userPrefHelper = new UserPrefHelperImpl(buildUserPrefs(userPreferencesService.getUserPreferences()));

        @Nullable
        protected final Object getReportValueFromCard(DatabaseRecordValues card, Attribute attr) throws IOException {
            Class type = getJavaClassForReportFromAttribute(attr);
            return switch (attr.getType().getName()) {
                case LOOKUP -> {
                    if (card.getCodeOf(attr.getName()) != null) {
                        yield translationService.translateLookupDescription(((LookupAttributeType) attr.getType()).getLookupTypeName(), card.getCodeOf(attr.getName()), card.getDescriptionOf(attr.getName()));
                    } else {
                        yield "";
                    }
                }
                case LOOKUPARRAY -> {
                    List<String> lookupTranslatedDescriptions = list();
                    card.get(attr.getName(), List.class).forEach(l -> lookupTranslatedDescriptions.add(translationService.translateLookupDescription(((LookupAttributeType) attr.getType()).getLookupTypeName(), ((LookupValueImpl) l).getCode(), ((LookupValueImpl) l).getDescription())));
                    yield String.join(", ", lookupTranslatedDescriptions);
                }
                case REFERENCE, FOREIGNKEY ->
                    card.getDescriptionOf(attr.getName());
                case DATE ->
                    userPrefHelper.serializeDate(card.get(attr.getName(), LocalDate.class));
                case TIMESTAMP ->
                    userPrefHelper.serializeDateTime(card.get(attr.getName(), ZonedDateTime.class));
                case TIME ->
                    userPrefHelper.serializeTime(card.get(attr.getName(), LocalTime.class));
                case DECIMAL, DOUBLE, FLOAT -> {
                    if (card.get(attr.getName(), Number.class) != null) {
                        DecimalFormat decimalFormat = userPreferencesService.getUserPreferences().getDecimalFormat();
                        if (attr.getType() instanceof DecimalAttributeType && ((DecimalAttributeType) attr.getType()).getScale() != null) {
                            String formatAsString = "0.0";
                            for (int i = 0; i < ((DecimalAttributeType) attr.getType()).getScale() - 1; i++) {
                                formatAsString += "0";
                            }
                            decimalFormat.applyPattern(formatAsString);
                        } else if (attr.getType() instanceof DoubleAttributeType && attr.getMetadata().getVisibleDecimals() != null) {
                            String formatAsString = "0.0";
                            for (int i = 0; i < attr.getMetadata().getVisibleDecimals() - 1; i++) {
                                formatAsString += "0";
                            }
                            decimalFormat.applyPattern(formatAsString);
                        }
                        String value = decimalFormat.format(card.get(attr.getName(), Number.class));
                        yield format("%s %s", value, attr.getMetadata().getUnitOfMeasure() == null ? "" : attr.getMetadata().getUnitOfMeasure());
                    } else {
                        yield card.get(attr.getName(), type);
                    }
                }
                case STRING -> {
                    if (attr.getMetadata().isPassword()) {
                        if (isBlank(card.get(attr.getName(), type))) {
                            yield "";
                        } else {
                            yield format("%s%s%s%s%s", passwordDotSymbol, passwordDotSymbol, passwordDotSymbol, passwordDotSymbol, passwordDotSymbol);
                        }
                    } else {
                        yield card.get(attr.getName(), type);
                    }
                }
                case STRINGARRAY -> {
                    yield card.get(attr.getName(), List.class).stream().collect(joining(", "));
                }
                case TEXT ->
                    switch (Optional.ofNullable(attr.getMetadata().getTextAttributeLanguage()).orElse(TAL_OTHER)) {
                        case TAL_HTML ->
                            htmlToString(toStringOrEmpty(card.get(attr.getName(), type)));
                        case TAL_MARKDOWN ->
                            markdownToString(toStringOrEmpty(card.get(attr.getName(), type)));
                        default ->
                            card.get(attr.getName(), type);
                    };
                case LINK ->
                    htmlToString(toStringOrEmpty(card.get(attr.getName(), type)));
                case FILE -> {
                    if (card.get(attr.getName()) != null) {
                        yield dao.getCard(DMS_MODEL_DEFAULT_CLASS, ((IdAndDescription) card.get(attr.getName())).getId()).get(DOCUMENT_ATTR_FILENAME).toString();
                    } else {
                        yield "";
                    }
                }
                case INTEGER, LONG -> {
                    if (card.get(attr.getName(), Number.class) != null) {
                        yield format("%d %s", card.get(attr.getName(), Number.class), attr.getMetadata().getUnitOfMeasure() == null ? "" : attr.getMetadata().getUnitOfMeasure());
                    } else {
                        yield card.get(attr.getName(), type);
                    }
                }
                default ->
                    card.get(attr.getName(), type);
            };
        }

        @Nullable
        protected final Object getReportValueFromHistoryCard(Classe classe, DatabaseRecordValues card, Attribute historyAttr) throws IOException {
            Attribute attr = switch (historyAttr.getName()) {
                case "_beginDate" ->
                    card instanceof CMRelation ? historyAttr : classe.getAttribute(ATTR_BEGINDATE);
                case "_endDate" ->
                    card instanceof CMRelation ? historyAttr : classe.getAttribute(ATTR_ENDDATE);
                case "_user" ->
                    card instanceof CMRelation ? historyAttr : classe.getAttribute(ATTR_USER);
                case "_activityName" ->
                    classe.getAttribute("ActivityDefinitionId");
                case "_activityExecutor" ->
                    classe.getAttribute("NextExecutor");
                case "_activityState" ->
                    classe.getAttribute("FlowStatus");
                case "_attributeValue" ->
                    classe.getAttribute(card.get("_attributeName", String.class));
                default ->
                    historyAttr;
            };
            return getReportValueFromCard(card, attr);
        }

    }

    private DateAndFormatPreferences buildUserPrefs(DateAndFormatPreferences currentConfig) {
        return DateAndFormatPreferencesImpl.copyOf(currentConfig).accept(c -> {
            if (etlConfiguration.getThousandsSeparator() != null) {
                c.withNumberGroupingSeparator(etlConfiguration.getThousandsSeparator());
            }
        }).build();
    }

    private static String getFieldNameForAttr(Attribute attr) {
        return format("%s#%s", attr.getOwner().getName(), attr.getName());
    }

    private JRDesignField buildFieldForAttr(Attribute attr) {
        JRDesignField field = new JRDesignField();
        field.setName(getFieldNameForAttr(attr));
        field.setDescription(translationService.translateAttributeDescription(attr));
        field.setValueClass(getJavaClassForReportFromAttribute(attr));
        logger.trace("create field = {} for attr = {}", field, attr);
        return field;
    }

    private JRDesignField buildFieldForAttributeGroup(AttributeGroupInfo attrGroup, Classe myClass) {
        JRDesignField field = new JRDesignField();
        field.setName(attrGroup.getName());
        field.setDescription(translationService.translateAttributeGroupDescription(myClass, attrGroup));
        return field;
    }

    public JasperPrint compileAndFillReport(JasperDesign jasperDesign, Map<String, Object> params, JRDataSource dataSource) {
        try {
            logger.debug("compile report");
            JasperReport compiledReport = JasperCompileManager.getInstance(contextService.getContext()).compile(jasperDesign);
            Stopwatch stopwatch = Stopwatch.createStarted();
            logger.debug("execute report");
            logger.trace("report params = \n\n{}\n", mapToLoggableStringLazy(params));
            JasperPrint filledReport = JasperFillManager.getInstance(contextService.getContext()).fill(compiledReport, params, dataSource);
            logger.debug("processed report in {}", CmDateUtils.toUserDuration(stopwatch.elapsed()));
            return filledReport;
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    private JasperDesign loadReportFromResources(String reportFileName) throws JRException {
        return JRXmlLoader.load(contextService.getContext(), checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + reportFileName), "report not found for file =< %s >", reportFileName));
    }
}
