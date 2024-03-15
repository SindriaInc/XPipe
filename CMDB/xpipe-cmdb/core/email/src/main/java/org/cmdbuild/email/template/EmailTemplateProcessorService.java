/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.job.MapperConfig;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface EmailTemplateProcessorService {

    Email applyEmailTemplate(Email email, EmailTemplate template, Card clientCard, Card serverCard);

    Email applyEmailTemplate(Email email, EmailTemplate template);

    String applyEmailTemplateExpr(String expr, EmailTemplate template, Card clientCard, Card serverCard);

    Map<String, Object> applyEmailTemplateExprs(@Nullable Card card, Map<String, Object> exprs);

    Map<String, Object> applyEmailTemplateExprs(@Nullable Card card, Map<String, Object> exprs, String language);

    Email applyEmailTemplate(Email email, EmailTemplate template, Card data, Email receivedEmail);

    Map<String, Object> applyEmailTemplate(Map<String, String> expressions, Email receivedEmail, @Nullable MapperConfig mapperConfig);

    EmailTemplateBindings getEmailTemplateBindings(EmailTemplate template);

    Email createEmailFromTemplate(EmailTemplate template, Map<String, Object> map);

    Email createEmailFromTemplate(EmailTemplate template, Card card);

    Email createEmailFromTemplate(EmailTemplate template, Card card, Map<String, Object> data);

    Email applyEmailTemplate(Email email, EmailTemplate template, Map<String, Object> data);

    default String applyEmailTemplateExpr(String expr, EmailTemplate template, Card card) {
        return applyEmailTemplateExpr(expr, template, card, card);
    }

    default Map<String, Object> applyEmailTemplateExprs(Map<String, Object> exprs) {
        return applyEmailTemplateExprs(null, exprs);
    }

    default String applyEmailTemplateExpr(String expr, Card card) {
        return (String) applyEmailTemplateExprs(card, map("_expr", expr)).get("_expr");
    }

    default String applyEmailTemplateExpr(String expr, Card card, String language) {
        return (String) applyEmailTemplateExprs(card, map("_expr", expr), language).get("_expr");
    }

    default String applyEmailTemplateExpr(String expr, Map<String, ?> data) {
        return applyEmailTemplateExpr(expr, CardImpl.buildCard(ClasseImpl.builder().withName("DUMMY").build(), (Map) data));
    }

    default Email applyEmailTemplate(Email email, EmailTemplate template, Card card) {
        return applyEmailTemplate(email, template, card, card);
    }

}
