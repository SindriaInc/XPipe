/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.cql.CqlUtils.getCqlSelectElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperServiceExt;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_DEFAULT;
import static org.cmdbuild.easytemplate.EasytemplateProcessor.ExprProcessingMode.EPM_JAVASCRIPT;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService;
import org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_AUTO;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_HTML;
import org.cmdbuild.easytemplate.TemplateResolver;
import org.cmdbuild.easytemplate.TemplateResolverImpl;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.EmailTemplateBindingsImpl;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.job.MapperConfig;
import static org.cmdbuild.email.utils.EmailMtaUtils.renameDuplicates;
import org.cmdbuild.email.utils.EmailUtils;
import static org.cmdbuild.email.utils.EmailUtils.handleEmailSignatureForTemplate;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.translation.TranslationUtils.lookupDescriptionTranslationCode;
import static org.cmdbuild.translation.TranslationUtils.notificationTemplateBodyTranslationCode;
import static org.cmdbuild.translation.TranslationUtils.notificationTemplateSubjectTranslationCode;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJsonLazy;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.emptyToNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateProcessorServiceImpl implements EmailTemplateProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasytemplateRepository easytemplateRepository;
    private final EasytemplateService easytemplateService;
    private final UserRepository userRepository;
    private final OperationUserSupplier userSupplier;
    private final RoleRepository roleRepository;
    private final DaoService dao;
    private final FtlTemplateService ftlTemplateService;
    private final CmApiService apiService;
    private final RefAttrHelperServiceExt refAttrHelperService;
    private final LookupService lookupService;
    private final ReportService reportService;
    private final ObjectTranslationService translationService;
    private final EmailSignatureService signatureService;
    private final LanguageService languageService;
    private final DmsAttachmentDownloader dmsAttachmentDownloader;

    public EmailTemplateProcessorServiceImpl(EasytemplateRepository easytemplateRepository, EasytemplateService easytemplateService, UserRepository userRepository, OperationUserSupplier userSupplier, RoleRepository roleRepository, DaoService dao, FtlTemplateService ftlTemplateService, CmApiService apiService, RefAttrHelperServiceExt refAttrHelperService, LookupService lookupService, ReportService reportService, ObjectTranslationService translationService, EmailSignatureService signatureService, LanguageService languageService, DmsAttachmentDownloader dmsAttachmentDownloader) {
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.easytemplateService = checkNotNull(easytemplateService);
        this.userRepository = checkNotNull(userRepository);
        this.userSupplier = checkNotNull(userSupplier);
        this.roleRepository = checkNotNull(roleRepository);
        this.dao = checkNotNull(dao);
        this.ftlTemplateService = checkNotNull(ftlTemplateService);
        this.apiService = checkNotNull(apiService);
        this.refAttrHelperService = checkNotNull(refAttrHelperService);
        this.lookupService = checkNotNull(lookupService);
        this.reportService = checkNotNull(reportService);
        this.translationService = checkNotNull(translationService);
        this.signatureService = checkNotNull(signatureService);
        this.languageService = checkNotNull(languageService);
        this.dmsAttachmentDownloader = checkNotNull(dmsAttachmentDownloader);
    }

    @Override
    public Email createEmailFromTemplate(EmailTemplate template, Map<String, Object> map) {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT).build();
        return new EmailTemplateProcessor(null, null, template, null, null, checkNotNull(map)).processEmail(email);
    }

    @Override
    public Email createEmailFromTemplate(EmailTemplate template, Card card) {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT).build();
        return applyEmailTemplate(email, template, checkNotNull(card));
    }

    @Override
    public Email createEmailFromTemplate(EmailTemplate template, @Nullable Card card, @Nullable Map<String, Object> data) {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT).build();
        return new EmailTemplateProcessor(card, card, checkNotNull(template), null, null, data).processEmail(email);
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template, Card clientCard, Card serverCard) {
        return new EmailTemplateProcessor(checkNotNull(clientCard), checkNotNull(serverCard), checkNotNull(template), null, null, null).processEmail(email);
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template) {
        checkNotNull(email);
        checkNotNull(template);
        return new EmailTemplateProcessor(null, null, template, null, null, null).processEmail(email);
    }

    @Override
    public String applyEmailTemplateExpr(String expr, EmailTemplate template, Card clientCard, Card serverCard) {
        checkNotNull(template);
        return new EmailTemplateProcessor(checkNotNull(clientCard), checkNotNull(serverCard), template, null, null, null).processExpression(expr);
    }

    @Override
    public Map<String, Object> applyEmailTemplateExprs(@Nullable Card card, Map<String, Object> exprs) {
        return new EmailTemplateProcessor(card, card, null, null, null, exprs).processExpressions(exprs);
    }

    @Override
    public Map<String, Object> applyEmailTemplateExprs(@Nullable Card card, Map<String, Object> exprs, String language) {
        return new EmailTemplateProcessor(card, card, null, null, null, exprs, language).processExpressions(exprs);
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template, Card card, Email receivedEmail) {
        checkNotNull(card);
        checkNotNull(template);
        return new EmailTemplateProcessor(card, card, template, checkNotNull(receivedEmail), null, null).processEmail(email);//TODO client,server card data
    }

    @Override
    public Email applyEmailTemplate(Email email, EmailTemplate template, Map<String, Object> data) {
        checkNotNull(email);
        checkNotNull(template);
        checkNotNull(data);
        return new EmailTemplateProcessor(null, null, template, null, null, data).processEmail(email);
    }

    @Override
    public EmailTemplateBindings getEmailTemplateBindings(EmailTemplate template) {
        checkNotNull(template);
        return new EmailTemplateProcessor(null, null, template, null, null, null).getBindings();
    }

    @Override
    public Map<String, Object> applyEmailTemplate(Map<String, String> expressions, Email receivedEmail, @Nullable MapperConfig mapperConfig) {
        checkNotNull(expressions);
        checkNotNull(receivedEmail);
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor(null, null, null, receivedEmail, mapperConfig, null);
        return map(transformValues(expressions, v -> emailTemplateProcessor.processExpression(v)));
    }

    @Nullable
    private Card loadClientCardValues(@Nullable Card clientCard, @Nullable Card serverCard) {
        if (clientCard != null && clientCard != serverCard) {
            logger.debug("load client card data (lookup/references id and descriptions)");
            Classe type = clientCard.getType();
            clientCard = CardImpl.copyOf(clientCard).withAttributes(clientCard.getAllValuesAsMap().entrySet().stream().map(e -> { //TODO move this to another service
                Object value = e.getValue();
                if (type.hasAttribute(e.getKey()) && type.getAttribute(e.getKey()).isOfType(REFERENCE, FOREIGNKEY, LOOKUP) && value != null && !((IdAndDescription) value).hasCodeAndDescription() && ((IdAndDescription) value).hasId()) {
                    Attribute attribute = type.getAttribute(e.getKey());
                    value = (IdAndDescription) switch (attribute.getType().getName()) {
                        case REFERENCE, FOREIGNKEY ->
                            refAttrHelperService.getReferencedCard(attribute, value);
                        case LOOKUP ->
                            lookupService.getLookup(((IdAndDescription) value).getId());
                        default ->
                            value;
                    };
                }
                return Pair.of(e.getKey(), value);
            }).collect(toMap(Pair::getKey, Pair::getValue))).build();
        }
        return clientCard;
    }

    private class EmailTemplateProcessor {

        private final EmailTemplate template;
        private final EasytemplateProcessor processor;
        private final Email receivedEmail;
        private final MapperConfig mapperConfig;
        private final Map<String, Object> otherData, ftlTemplateData;
        private final String language;
        private final Card clientCard;

        public EmailTemplateProcessor(@Nullable Card clientCard, @Nullable Card serverCard, @Nullable EmailTemplate template, @Nullable Email receivedEmail, @Nullable MapperConfig mapperConfig, @Nullable Map<String, Object> otherData) {
            this(clientCard, serverCard, template, receivedEmail, mapperConfig, otherData, null);
        }

        public EmailTemplateProcessor(@Nullable Card clientCard, @Nullable Card serverCard, @Nullable EmailTemplate template, @Nullable Email receivedEmail, @Nullable MapperConfig mapperConfig, @Nullable Map<String, Object> otherData, @Nullable String language) {
            this.template = template;
            this.receivedEmail = receivedEmail;
            this.mapperConfig = mapperConfig;
            this.otherData = firstNotNull(otherData, emptyMap());

            this.language = isNotBlank(language) ? language : new EmailTemplateProcessor(clientCard, serverCard, template, receivedEmail, mapperConfig, otherData, languageService.getContextLanguage()).getLanguage();
            logger.debug("template language =< {} >", this.language);

            this.clientCard = loadClientCardValues(clientCard, serverCard);

            Map<String, Object> clientData = Optional.ofNullable(this.clientCard).map(Card::getAllValuesAsMap).orElse(emptyMap()),
                    serverData = Optional.ofNullable(serverCard).map(Card::getAllValuesAsMap).orElse(emptyMap());

            logger.trace("client (new) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(clientData));
            logger.trace("server (current) card data for notification template = \n\n{}\n", mapToLoggableStringLazy(serverData));
            logger.trace("other data for notification template = \n\n{}\n", mapToLoggableStringLazy(this.otherData));

            String jsContext = buildJsContext(this.clientCard, serverCard, this.language);
            logger.trace("js context for notification template = \n\n{}\n", prettifyIfJsonLazy(jsContext));

            logger.trace("template context data = \n\n{}\n", mapToLoggableStringLazy(Optional.ofNullable(template).map(EmailTemplate::getMeta).orElse(emptyMap())));

            Map<String, TemplateResolver> resolvers = processingResolvers(jsContext);

            processor = EasytemplateProcessorImpl.builder().withResolvers(resolvers).build();

            Map<String, Object> cardData = firstNotNull(emptyToNull(serverData), clientData);
            cardData = map(cardData).with("_client", clientData, "_new", clientData, "_server", serverData, "_old", serverData, "_email", serializeEmailForTemplate(receivedEmail));
            Map<String, Object> allData = map(cardData).with(this.otherData);

            ftlTemplateData = map(allData).with("card", cardData, "data", allData, "email", serializeEmailForTemplate(receivedEmail), "lang", this.language, "cmdb", apiService.getCmApi(), "logger", LoggerFactory.getLogger(format("%s.TEMPLATE.%s", getClass().getName(), Optional.ofNullable(template).map(EmailTemplate::getCode).orElse("NOTEMPLATE"))));
        }

        private EmailTemplateProcessor(EasytemplateProcessor processor, @Nullable EmailTemplate template) {
            this.processor = checkNotNull(processor);
            this.template = template;
            this.receivedEmail = null;
            this.mapperConfig = null;//TODO check this 
            this.otherData = ftlTemplateData = emptyMap();
            this.language = null;//TODO check this 
            this.clientCard = null;//TODO check this 
        }

        private Map<String, TemplateResolver> processingResolvers(String jsContext) {
            Map<String, TemplateResolver> resolvers = EasytemplateProcessorImpl.copyOf(easytemplateService.getDefaultProcessorWithJsContext(jsContext))
                    .withResolver("js", this::processJsExpr)
                    .withResolver("user", this::processUserExpr)//TODO check this
                    .withResolver("group", this::processGroupExpr)//TODO check this
                    .withResolver("groupUsers", this::processGroupUsersExpr)
                    .withResolver("email", this::processReceivedEmailExpr, false)
                    .withResolver("card", this::processCardExpr)
                    .withResolver("", this::processCardExpr)
                    .withResolver("cql", this::processCqlExpr)
                    .withResolver("dbtmpl", this::processDbTmplExpr)
                    .withResolver("data", this::processOtherDataExpr)
                    .withResolver("attrLabel", this::processAttrExpr)
                    .accept((b) -> {
                        if (mapperConfig != null) {
                            b.withResolver("mapper", this::processMapperExpr);
                        }
                    }).build().getResolvers();

            resolvers = transformValues(resolvers, f -> {
                return new TemplateResolverImpl((x) -> {
                    try {
                        return f.getFunction().apply(x);
                    } catch (Exception ex) {
                        logger.warn(marker(), "CM: error processing email template expression =< {} > for template = {}", x, template, ex);
                        return "";
                    }
                }, f.isRecursive());
            });

            return resolvers;
        }

        @Nullable
        private Map<String, Object> serializeEmailForTemplate(@Nullable Email email) {
            return email == null ? null : map(
                    "_id", email.getId(),
                    "from", email.getFrom(),
                    "replyTo", email.getReplyTo(),
                    "to", email.getTo(),
                    "cc", email.getCc(),
                    "bcc", email.getBcc(),
                    "subject", email.getSubject(),
                    "body", email.getContent(),
                    "contentType", email.getContentType(),
                    "_content_plain", email.getContentPlaintext(),
                    "_content_html", email.getContentHtml(),
                    "date", toIsoDateTime(email.getDate()),
                    "card", email.getReference()
            );
        }

        @Nullable
        private String getLanguage() {
            if (template != null && template.hasLangExpr()) {
                return trimToNull(processEmailTemplateValue(template.getLangExpr()));
            } else {
                return null;
            }
        }

        private boolean hasHtmlTarget() {
            return template != null && isContentType(template.getContentType(), "text/html");
        }

        private boolean hasPlaintextTarget() {
            return template != null && isContentType(template.getContentType(), "text/plain");
        }

        private String processExpression(String expr) {
            logger.debug("processing email template expr =< {} >", expr);
            return processEmailTemplateValue(expr);
        }

        public Email processEmail(Email email) {
            checkNotNull(template, "email template is null");
            logger.debug("processing email = {} with template = {}", email, template);
            final Email toSendEmail = EmailImpl.copyOf(email).accept((builder) -> {
                processEmailTemplate(builder::withFrom, template.getFrom());
                processEmailTemplate(builder::withBcc, template.getBcc());
                processEmailTemplate(b -> {
                    if (template.hasSignature() && isContentType(template.getContentType(), "text/html")) {
                        b = handleEmailSignatureForTemplate(b, signatureService.getSignatureHtmlForCurrentUser(template.getSignature()));
                    }
                    builder.withContent(b);
                }, translateValue(template.getContent(), notificationTemplateBodyTranslationCode(template.getCode())), template.getContentType());
                processEmailTemplate(builder::withCc, template.getCc());
                processEmailTemplate(builder::withSubject, translateValue(template.getSubject(), notificationTemplateSubjectTranslationCode(template.getCode())));
                processEmailTemplate(builder::withTo, template.getTo());
            })
                    .withAccount(template.getAccount())
                    .withContentType(template.getContentType())
                    .withDelay(template.getDelay())
                    .withKeepSynchronization(template.getKeepSynchronization())
                    .withPromptSynchronization(template.getPromptSynchronization())
                    .withSignature(template.getSignature())
                    .withNotificationProvider(template.getNotificationProvider())
                    .withTemplate(template.getId()).accept(e -> {
                if (template.hasReports()) {
                    e.addAttachments(executeReportsToAttachments());
                }
                if (template.hasUploadAttachments()) {
                    logger.debug("adding upload attachments for filter =< {} > defined in template =< {} >", template.getUploadAttachmentsFilter(), template);
                    final List<EmailAttachment> toUploadAttachments = executeUploadAttachmentsToAttachments();
                    e.addAttachments(toUploadAttachments);
                    logger.debug("added {} to upload attachments", toUploadAttachments.size());
                }
            }).build();

            return toSendEmail;
        }

        @Nullable
        private String translateValue(String value, String code) {
            if (template != null && isNotBlank(language)) {
                value = translationService.translateByLangAndCode(language, code, value);
                logger.trace("translate email {} as =< {} >", code, value);
            }
            return value;
        }

        public Map<String, Object> processExpressions(Map<String, Object> exprs) {
            return map(exprs).mapValues(v -> v instanceof String ? processExpression((String) v) : v);
        }

        public EmailTemplateBindings getBindings() {
            List<String> clientBindings = list(), serverBindings = list();

            new EmailTemplateProcessor(EasytemplateProcessorImpl.copyOf(processor)
                    .withResolver("client", clientBindings::add)
                    .withResolver("server", serverBindings::add)
                    .build(), template).doProcessForBindings();

            return new EmailTemplateBindingsImpl(clientBindings, serverBindings);
        }

        private void doProcessForBindings() {
            checkNotNull(template, "email template is null");
            final Map<String, String> reportCustomParams = template.getBindingParams();
            list(
                    template.getFrom(),
                    template.getBcc(),
                    template.getContent(),
                    template.getCc(),
                    template.getSubject(),
                    template.getTo()
            ).with(
                    reportCustomParams.values()
            //Prev version: template.getMeta().values() //note: process all data values because they _may_ contain bindings... this is quite rough and may/will produce a bungh of warnings            
            ).stream().filter(StringUtils::isNotBlank).forEach(processor::processExpression);
            logger.trace("Report bindings found =< {} >", reportCustomParams);
        }

        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression) {
            processEmailTemplate(setter, expression, null);
        }

        private void processEmailTemplate(Consumer<String> setter, @Nullable String expression, @Nullable String contentType) {
            if (isBlank(expression)) {
                // do nothing
            } else {
                String value = processEmailTemplateValue(expression, contentType);
                setter.accept(value);
            }
        }

        private String processMapperExpr(String expression) {
            checkNotNull(receivedEmail, "missing received email");
            checkNotNull(mapperConfig, "missing mapper config");
            return EmailUtils.processMapperExpr(mapperConfig, receivedEmail.getContentPlaintext(), expression);
        }

        private String processDbTmplExpr(String expression) {
            return processor.processExpression(easytemplateRepository.getTemplateOrNull(checkNotBlank(expression, "db template expr is blank")));
        }

        private String processEmailTemplateValue(String expression) {
            return processEmailTemplateValue(expression, null);
        }

        private String processEmailTemplateValue(String expression, @Nullable String contentType) {
            logger.trace("process email template expr =< {} >", abbreviate(expression));
            String value;
            languageService.setContextLanguage(language);
            try {
                if (ftlTemplateService.isFtlTemplate(expression)) {
                    FtlTemplateMode mode = isContentType(contentType, "text/html") ? FTM_HTML : FTM_AUTO;//TODO improve this ??
                    value = ftlTemplateService.executeFtlTemplate(expression, mode, ftlTemplateData);
                } else {
                    value = processor.processExpression(expression);
                }
            } finally {
                languageService.resetContextLanguage();
            }
            logger.trace("processed email template expr =< {} >, output value =< {} >", abbreviate(expression), abbreviate(value));
            return value;
        }

        private String getTemplateContextValueOrDbtemplateValue(String key, ExprProcessingMode mode) {
            String expr = null;
            if (template != null) {
                expr = template.getMeta().get(key);
                logger.trace("trying to resolve key =< {} > with template context data, resolved to value =< {} >", key, expr);
//                easytemplateRepository.getTemplate
            }
            if (isBlank(expr)) {
                expr = easytemplateRepository.getTemplateOrNull(key);
                logger.trace("trying to resolve key =< {} > with dbtemplate data, resolved to value =< {} >", key, expr);
            }
            checkNotBlank(expr, "unable to resolve expr =< %s > for template context data or dbtemplate data", key);
            return processor.processExpression(expr, mode);
        }

        private String processJsExpr(String jsExpr) {
            checkNotBlank(jsExpr, "js expr is blank");
            if (template != null && isTemplateContextKey(jsExpr)) {
                jsExpr = getTemplateContextValueOrDbtemplateValue(jsExpr, EPM_JAVASCRIPT);
            }
            logger.trace("evaluate js expression =< {} >", jsExpr);
            return toStringOrNull(easytemplateService.evalJavascriptCode(jsExpr));
        }

        private String processUserExpr(String username) {
            checkNotBlank(username, "username expr is blank");
            return switch (username) {
                case "id" ->
                    toStringOrNull(userSupplier.getUser().getId());
                case "name" ->
                    userSupplier.getUsername();
                case "lang" ->
                    languageService.getContextLanguage();
                default ->
                    userRepository.getUserByIdOrUsername(username).getEmail();
            };
        }

        private String processGroupExpr(String group) {
            checkNotBlank(group, "group expr is blank");
            return switch (group) {
                case "id" ->
                    userSupplier.getUser().hasDefaultGroup() ? toStringOrNull(userSupplier.getUser().getDefaultGroup().getId()) : null;
                case "name" ->
                    userSupplier.getCurrentGroup();
                default ->
                    roleRepository.getByNameOrId(group).getEmail();
            };
        }

        private String processGroupUsersExpr(String group) {
            checkNotBlank(group, "groupUsers expr is blank");
            long roleId;
            if (isNumber(group)) {
                roleId = toLong(group);
            } else {
                roleId = roleRepository.getGroupWithName(group).getId();
            }
            return userRepository.getAllWithRole(roleId).stream().map(UserData::getEmail).filter(StringUtils::isNotBlank).distinct().sorted().collect(joining(","));
        }

        private String processOtherDataExpr(String expr) {
            checkNotBlank(expr, "other data expr is blank");
            return toStringOrEmpty(otherData.get(expr));
        }

        private String processReceivedEmailExpr(String expr) {
            checkNotNull(receivedEmail, "invalid email expr, no received email is available");
            checkNotBlank(expr, "email expr is blank");
            return switch (expr) {
                case "from" ->
                    receivedEmail.getFrom();
                case "to" ->
                    receivedEmail.getTo();
                case "cc" ->
                    receivedEmail.getCc();
                case "bcc" ->
                    receivedEmail.getBcc();
                case "date" ->
                    toUserReadableDateTime(receivedEmail.getDate());
                case "subject" ->
                    receivedEmail.getSubject();
                case "content" -> {
                    if (hasHtmlTarget()) {
                        yield receivedEmail.getContentHtmlOrWrappedPlaintext();
                    } else if (hasPlaintextTarget()) {
                        yield receivedEmail.getContentPlaintext();
                    } else {
                        logger.warn(marker(), "evaluating email `context` expr, unable to detect target content type; returning fuzzy default. You should use `content_plain` or `content_html` here instead of `content`");
                        yield receivedEmail.getContentHtmlOrRawPlaintext();
                    }
                }
                case "content_plain" ->
                    receivedEmail.getContentPlaintext();
                case "content_html" ->
                    receivedEmail.getContentHtmlOrWrappedPlaintext();
                default ->
                    throw new IllegalArgumentException(format("unsupported email expr = %s", expr));
            };
        }

        @Nullable
        private String processCqlExpr(@Nullable String expr) {
            logger.trace("process cql expr =< {} >", expr);
            checkNotBlank(expr, "cql expr is blank");
            String field = null;
            Matcher matcher = Pattern.compile("^([^.]+)[.]([^.]+)$").matcher(expr);
            if (matcher.find()) {
                expr = checkNotBlank(matcher.group(1));
                field = checkNotBlank(matcher.group(2));
                expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
            } else if (isTemplateContextKey(expr)) {
                expr = getTemplateContextValueOrDbtemplateValue(expr, EPM_DEFAULT);
            }
            logger.trace("execute cql query =< {} >", expr);
            Card card = dao.selectAll().where(CqlFilterImpl.build(expr).toCmdbFilter()).getCardOrNull();
            logger.trace("cql query output card = {}", card);
            String value;
            if (card == null) {
                value = null;
            } else {
                if (isBlank(field)) {
                    field = getOnlyElement(getCqlSelectElements(expr));
                }
                value = card.getString(field);
            }
            logger.trace("cql query output value =< {} > for field =< {} > expr =< {} >", value, field, expr);
            return value;
        }

        private String processCardExpr(@Nullable String expr) {
            logger.trace("process card expr =< {} >", expr);
            String value = switch (checkNotBlank(expr, "card expr is blank")) {
                case "CurrentRole" ->
                    Optional.ofNullable(userSupplier.getUser().getDefaultGroupOrNull()).map(r -> toStringOrEmpty(r.getId())).orElse("");
                case "lang" ->
                    nullToEmpty(language);
                default ->
                    processor.processExpression(format("{client:%s}", expr));
            };
            logger.trace("processed card expr =< {} >, found value =< {} >", expr, value);
            return value;
        }

        private String processAttrExpr(String expr) {
            logger.trace("process attr expr =< {} >", expr);
            checkNotBlank(expr);
            return translationService.translateAttributeDescription(dao.getClasse(((Map<String, Object>) ftlTemplateData.get("card")).get("IdClass").toString()).getAttribute(expr));
        }

        private boolean isTemplateContextKey(String expr) {
            return template != null && template.getMeta().containsKey(expr);
        }

        private List<EmailAttachment> executeReportsToAttachments() {
            List<EmailAttachment> result = template.getReports().stream().map(this::executeReportToAttachment).collect(toImmutableList());

            // Different attachments have to 
            return renameDuplicates(result);
        }

        private EmailAttachment executeReportToAttachment(ReportConfig config) {
            try {
                logger.debug("execute report = {} template for email attachment", config);
                Map<String, Object> params = processExpressions(config.getParams());
                logger.debug("build report = {} with template params = {}", config, mapToLoggableStringInline(params));

                DataHandler dataHandler = reportService.executeReportAndDownload(config.getCode(), config.getFormat(), params);
                EmailAttachment attachment = buildEmailAttachment(dataHandler);
                logger.debug("build email attachment = {}", attachment);
                return attachment;
            } catch (Exception ex) {
                throw new EmailException(ex, "error processing report = %s for email attachment", config);
            }
        }

        private EmailAttachmentImpl buildEmailAttachment(DataHandler dataHandler) {
            return EmailAttachmentImpl.build(toDataSource(dataHandler));
        }

        private List<EmailAttachment> executeUploadAttachmentsToAttachments() {
            return list(dmsAttachmentDownloader.downloadAttachments(
                    this.clientCard,
                    template.getUploadAttachmentsFilter()))
                    .map(d -> buildEmailAttachment(d));
        }
    } // end EmailTemplateProcessor inner class

    private String buildJsContext(@Nullable Card clientCard, @Nullable Card serverCard, @Nullable String language) {
        return toJson(map("client", cardToJsContext(clientCard, language), "server", cardToJsContext(serverCard, language)));
    }

    private Object cardToJsContext(@Nullable Card card, @Nullable String language) {
        if (card == null) {
            return emptyMap();
        } else {
            return map(card.getAllValuesAsMap()).mapValues((k, v) -> cardAttrToJsContext(card.getType().getAttributeOrNull(k), v, language));
        }
    }

    private Object cardAttrToJsContext(@Nullable Attribute attr, @Nullable Object value, @Nullable String language) {
        if (value == null) {
            return null;
        } else if (attr == null) {
            return value;//TODO
        } else {
            return switch (attr.getType().getName()) {
                case REFERENCE, FOREIGNKEY -> {
                    IdAndDescription reference = rawToSystem(attr, value);
                    yield map("Id", reference.getId(), "_id", reference.getId()).skipNullValues().with(
                    "Code", reference.getCode(),
                    "code", reference.getCode(),
                    "Description", reference.getDescription(),
                    "description", reference.getDescription());
                }
                case LOOKUP -> {
                    LookupValue lookup = rawToSystem(attr, value);
                    yield map("Id", lookup.getId(), "_id", lookup.getId()).skipNullValues().with(
                    "Code", lookup.getCode(),
                    "code", lookup.getCode()
                    ).accept(c -> {
                        if (language != null && lookup.getLookupType() != null && lookup.getCode() != null) {
                            c.put("Description", translationService.translateByLangAndCode(language, lookupDescriptionTranslationCode(lookup.getLookupType(), lookup.getCode()), lookup.getDescription()));
                            c.put("Description", translationService.translateByLangAndCode(language, lookupDescriptionTranslationCode(lookup.getLookupType(), lookup.getCode()), lookup.getDescription()));
                        } else {
                            c.put("Description", lookup.getDescription());
                            c.put("description", lookup.getDescription());
                        }
                    }).accept(c -> {
                        if (lookup.getLookupType() != null && lookup.getCode() != null) {
                            c.put("_description_translation", translationService.translateLookupDescription(lookup.getLookupType(), lookup.getCode(), lookup.getDescription()));
                        } else {
                            c.put("_description_translation", lookup.getDescription());
                        }
                    });
                }
                case DATE ->
                    toIsoDate(value);
                case TIME ->
                    toIsoTime(value);
                case TIMESTAMP ->
                    toIsoDateTime(value);
                default ->
                    value;
            };
        }
    }
}
