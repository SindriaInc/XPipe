/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.api.UtilsApi;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.springframework.stereotype.Component;

@Component
public class UtilsApiImpl implements UtilsApi {

    private final EmailTemplateProcessorService service;
    private final EmailTemplateService emailTemplateService;
    private final ApiConverterService cardHelper;

    public UtilsApiImpl(EmailTemplateProcessorService service, EmailTemplateService emailTemplateService, ApiConverterService cardHelper) {
        this.service = checkNotNull(service);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.cardHelper = checkNotNull(cardHelper);
    }

    @Override
    public String applyTemplate(String template, Card card) {
        return service.applyEmailTemplateExpr(nullToEmpty(template), cardHelper.apiCardToDaoCard(card));
    }

    @Override
    public String applyTemplate(String template, Map<String, ?> data) {
        return service.applyEmailTemplateExpr(nullToEmpty(template), data);
    }

    @Override
    public EmailTemplate getTemplateOrNull(String templateCode) {
        return emailTemplateService.getByNameOrNull(templateCode);
    }

//    private final FtlTemplateService templateService;
//    private final CmApiService apiService;
//
//    public UtilsApiImpl(FtlTemplateService templateService, CmApiService apiService) {
//        this.templateService = checkNotNull(templateService);
//        this.apiService = checkNotNull(apiService);
//    }
//
//    @Override
//    public String applyTemplate(String template, Map<String, ?> data) {//TODO improve this
//        return templateService.executeFtlTemplate(nullToEmpty(template), map(data).with("cmdb", apiService.getCmApi(), "logger", LoggerFactory.getLogger(format("%s.TEMPLATE", getClass().getName()))));
//    }
}
