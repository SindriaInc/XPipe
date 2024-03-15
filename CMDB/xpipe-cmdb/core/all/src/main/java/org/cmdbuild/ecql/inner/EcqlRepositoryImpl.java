/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import javax.inject.Provider;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardRepository;
import static org.cmdbuild.dashboard.utils.DashboardUtils.getEcqlExprsInOrder;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlRepository;
import static org.cmdbuild.ecql.EcqlSource.CLASS_ATTRIBUTE;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.ecql.utils.EcqlUtils.ECQL_WIDGET_FROM_CLASS;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromClassAttributeFilter;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromDomainClassFilter;
import static org.cmdbuild.ecql.utils.EcqlUtils.parseEcqlId;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.sysparam.SysparamRepository;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EcqlRepositoryImpl implements EcqlRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseRepository classeRepository;
    private final DomainRepository domainRepository;
    private final Provider<ReportService> reportService; //TODO not great, remove dependency loop
    private final Provider<EasytemplateRepository> easytemplateRepository; //TODO not great, remove dependency loop
    private final Provider<SysparamRepository> sysparamRepository; //TODO not great, remove dependency loop
    private final Provider<NavTreeService> navTreeService; //TODO not great, remove dependency loop
    private final Provider<DashboardRepository> dashboardRepository; //TODO not great, remove dependency loop
    private final Provider<WorkflowService> workflowService; //TODO not great, remove dependency loop
    private final Provider<WidgetService> widgetService; //TODO not great, remove dependency loop

    public EcqlRepositoryImpl(ClasseRepository classeRepository, DomainRepository domainRepository, 
            Provider<ReportService> reportService, Provider<EasytemplateRepository> easytemplateRepository, Provider<SysparamRepository> sysparamRepository, Provider<NavTreeService> navTreeService, Provider<DashboardRepository> dashboardRepository, Provider<WorkflowService> workflowService, Provider<WidgetService> widgetService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.reportService = checkNotNull(reportService);
        this.easytemplateRepository = checkNotNull(easytemplateRepository);
        this.sysparamRepository = checkNotNull(sysparamRepository);
        this.navTreeService = checkNotNull(navTreeService);
        this.dashboardRepository = checkNotNull(dashboardRepository);
        this.workflowService = checkNotNull(workflowService);
        this.widgetService = checkNotNull(widgetService);
    }

    @Override
    public EcqlExpression getById(String encodedId) {
        logger.debug("get expression for ecql id = {}", encodedId);
        try {
            EcqlId ecqlId = parseEcqlId(encodedId);
            logger.debug("decoded ecql id = {}", ecqlId);
            return switch (ecqlId.getSource()) {
                case EASYTEMPLATE ->
                    new EcqlExpressionImpl(easytemplateRepository.get().getTemplate(ecqlId.getOnlyId()));
                case SYSPARAM ->
                    new EcqlExpressionImpl(sysparamRepository.get().getParam(ecqlId.getOnlyId()));
                case CLASS_ATTRIBUTE -> {
                    checkArgument(ecqlId.getId().size() == 2);
                    String classId = checkNotBlank(getPositionalId(ecqlId, 0));
                    String attributeId = checkNotBlank(getPositionalId(ecqlId, 1));
                    yield getFromClassAttribute(classId, attributeId);
                }
                case DOMAIN -> {
                    checkArgument(ecqlId.getId().size() == 2);
                    String domainId = checkNotBlank(getPositionalId(ecqlId, 0));
                    String classId = checkNotBlank(getPositionalId(ecqlId, 1));
                    yield getFromDomainClass(domainId, EcqlUtils.fetchClassToken(classId));
                }
                case REPORT_ATTRIBUTE -> {
                    checkArgument(ecqlId.getId().size() == 2);
                    String reportId = checkNotBlank(getPositionalId(ecqlId, 0));
                    String attributeCode = checkNotBlank(getPositionalId(ecqlId, 1));
                    yield new EcqlExpressionImpl(reportService.get().getParamsById(toLong(reportId)).stream().filter(a -> a.getName().equals(attributeCode)).findFirst().orElse(null).getFilter());
                }
                case NAVTREE_NODE -> {
                    checkArgument(ecqlId.getId().size() == 2);
                    String navTreeCode = checkNotBlank(getPositionalId(ecqlId, 0));
                    String nodeId = checkNotBlank(getPositionalId(ecqlId, 1));
                    yield new EcqlExpressionImpl(navTreeService.get().getTree(navTreeCode).getNodeById(nodeId).getTargetFilter());
                }
                case DASHBOARD_ITEM -> {
                    checkArgument(ecqlId.getId().size() == 2);
                    String dashboardCode = checkNotBlank(getPositionalId(ecqlId,0));
                    int itemIndex = toInt(getPositionalId(ecqlId, 1));
                    DashboardData dashboard = dashboardRepository.get().getDashboardByCode(dashboardCode);
                    yield new EcqlExpressionImpl(getEcqlExprsInOrder(dashboard).get(itemIndex));
                }
                case WIDGET -> {
                    checkArgument(ecqlId.getId().size() == 5);
                    String processIdOrClassId = checkNotBlank(getPositionalId(ecqlId, 0));
                    String taskId = checkNotBlank(getPositionalId(ecqlId, 1));
                    String widgetId = checkNotBlank(getPositionalId(ecqlId, 2));
                    String widgetAttr = checkNotBlank(getPositionalId(ecqlId, 3));
                    String xaContext = checkNotBlank(getPositionalId(ecqlId, 4));
                    WidgetData widget = switch (taskId) {
                        case ECQL_WIDGET_FROM_CLASS ->
                            widgetService.get().getWidgetForClass(processIdOrClassId, widgetId);
                        default ->
                            workflowService.get().getProcess(processIdOrClassId).getTaskById(taskId).getWidgetById(widgetId);
                    };
                    String cqlExpr = widgetService.get().widgetDataToWidget(widget, emptyMap()).getNotBlank(widgetAttr);
                    cqlExpr = EcqlUtils.resolveEcqlXa(cqlExpr, fromJson(xaContext, MAP_OF_OBJECTS));
                    yield new EcqlExpressionImpl(cqlExpr);
                }
                case EMBEDDED ->
                    new EcqlExpressionImpl(ecqlId.getOnlyId());
                default ->
                    throw new UnsupportedOperationException(format("unsupported ecql source = %s", ecqlId.getSource()));
            };
        } catch (Exception ex) {
            throw new EcqlException(ex, "ecql expression not found for id = %s", encodedId);
        }
    }

    private static String getPositionalId(EcqlId ecqlId, int pos) {
        return ecqlId.getId().get(pos);
    }

    private EcqlExpression getFromClassAttribute(String classId, String attributeId) {
        Classe classe = classeRepository.getClasse(classId);
        Attribute attribute = classe.getAttribute(attributeId);
        return getEcqlExpressionFromClassAttributeFilter(attribute);
    }
    
    private EcqlExpression getFromDomainClass(String domainId, String classId) {
        Domain domain = domainRepository.getDomain(domainId);

        return getEcqlExpressionFromDomainClassFilter(domain, classId);
    }    

}
