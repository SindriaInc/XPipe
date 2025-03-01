/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.serializeEmailStatus;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import static org.cmdbuild.email.utils.EmailMtaUtils.embedEmailInlineAttachmentsAsBase64;
import org.cmdbuild.service.rest.v3.model.WsEmailData;
import static org.cmdbuild.service.rest.v3.serializationhelpers.EmailTemplateSerializationType.ETS_APPLYTEMPLATE;
import static org.cmdbuild.service.rest.v3.serializationhelpers.EmailTemplateSerializationType.ETS_TEMPLATEONLY;
import static org.cmdbuild.service.rest.v3.serializationhelpers.EmailTemplateSerializationType.ETS_UPLOADTEMPLATEATTACHMENTS;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentSet;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailWsHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final static String SYNTHESIZED_RESOURCE_ID = "_ANY";

    private final DaoService dao;
    private final EmailService emailService;
    private final EmailTemplateService templateService;
    private final EmailAccountService accountService;
    private final DmsService dmsService;
    private final OperationUserSupplier operationUser;

    public EmailWsHelper(DaoService dao, EmailService emailService, EmailTemplateService templateService, EmailAccountService accountService, DmsService dmsService, OperationUserSupplier operationUser) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.templateService = checkNotNull(templateService);
        this.accountService = checkNotNull(accountService);
        this.dmsService = checkNotNull(dmsService);
        this.operationUser = checkNotNull(operationUser);
    }

    @Deprecated
    public Object createEmail(String classId, String cardId, WsEmailData emailData, boolean applyTemplate, boolean templateOnly, List<EmailAttachment> emailAttachments) {
        return createEmail(classId, cardId, emailData, templateSerializationType(applyTemplate, templateOnly, false), emailAttachments);
    }

    public Object createEmail(String classId, String cardId, WsEmailData emailData, Set<EmailTemplateSerializationType> templateSerializationType, List<EmailAttachment> emailAttachments) {
        Card card;
        if (isSinthesizedResource(cardId)) {
            card = emailData.hasCardData() ? CardImpl.buildCard(dao.getClasse(isSinthesizedResource(classId) ? toStringNotBlank(emailData.getCardData().getValues().get("_type")) : checkNotBlank(classId)), emailData.getCardData().getValues()) : null;
        } else {
            card = dao.getCard(classId, toLong(cardId)); //TODO check user permissions
        }
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();

        Boolean expr = null;
        if (templateSerializationType.contains(ETS_APPLYTEMPLATE)) {
            if (emailData.hasExpr()) {
                expr = handleTemplateExpr(email.getTemplate(), emailData, card);
            }
        }
        email = handleTemplate(email, emailData, card);
        email = toBooleanOrDefault(expr, templateSerializationType.contains(ETS_APPLYTEMPLATE)) ? applyTemplate(email, emailData, card) : email;

        email = EmailImpl.copyOf(email).withAbortableByUser(operationUser.getUsername()).build();

        if (!templateSerializationType.contains(ETS_TEMPLATEONLY)) {
            logger.debug("to email=< {} > adding {} input attachments =< {} >", email.getId(), emailAttachments.size(), emailAttachments);
            email = EmailImpl.copyOf(email).addAttachments(emailAttachments).build();
            email = emailService.create(email);
        }

        if (templateSerializationType.contains(ETS_UPLOADTEMPLATEATTACHMENTS)) {
            handleTemplateAttachments(email, emailData, card);
        }

        return serializeDetailedEmail(email, expr);
    }

    public Object updateEmail(Email email, Card card, WsEmailData emailData, Set<EmailTemplateSerializationType> templateSerializationType) {
        Boolean expr = null;
        if (templateSerializationType.contains(ETS_APPLYTEMPLATE)) {
            if (emailData.hasExpr()) {
                expr = handleTemplateExpr(email.getTemplate(), emailData, card);
            }
        }
        email = handleTemplate(email, emailData, card);
        email = toBooleanOrDefault(expr, templateSerializationType.contains(ETS_APPLYTEMPLATE)) ? applyTemplate(email, emailData, card) : email;

        if (!templateSerializationType.contains(ETS_TEMPLATEONLY)) {
            email = emailService.update(email);
        }
        if (templateSerializationType.contains(ETS_UPLOADTEMPLATEATTACHMENTS)) {
            handleTemplateAttachments(email, emailData, card);
        }
        return serializeDetailedEmail(email, expr);
    }

    public Email handleTemplate(Email email, WsEmailData emailData, @Nullable Card serverCard) {
        if (!email.hasTemplate() && isNotBlank(emailData.getTemplate()) && !templateService.isSysTemplate(emailData.getTemplate())) {
            email = EmailImpl.copyOf(email).withTemplate(templateService.getByNameOrId(emailData.getTemplate()).getId()).build();
        }
        if (!email.hasAccount() && isNotBlank(emailData.getAccount())) {
            email = EmailImpl.copyOf(email).withAccount(accountService.getAccountByIdOrCode(emailData.getAccount()).getId()).build();
        }
        return email;
    }

    public Email applyTemplate(Email email, @Nullable WsEmailData emailData, @Nullable Card serverCard) {
        if (!email.hasTemplate()) {
            return emailService.applySysTemplate(email, emailData.getTemplate());
        } else {
            if (emailData != null && emailData.hasCardData()) {
                Card clientCard = CardImpl.copyOf(serverCard).withAttributes(emailData.getCardData().getValues()).build();
                return emailService.applyTemplate(email, clientCard, serverCard);
            } else if (serverCard != null) {
                return emailService.applyTemplate(email, serverCard);
            } else {
                return emailService.applyTemplate(email);
            }
        }
    }

    public boolean handleTemplateExpr(long template, WsEmailData emailData, Card serverCard) {
        Card clientCard;
        if (emailData.hasCardData()) {
            clientCard = CardImpl.copyOf(serverCard).withAttributes(emailData.getCardData().getValues()).build();
        } else {
            clientCard = serverCard;
        }
        return toBooleanOrDefault(emailService.applyTemplateExpr(template, emailData.getExpr(), clientCard, serverCard), false);
    }

    public void handleTemplateAttachments(Email email, WsEmailData emailData, @Nullable Card serverCard) {
        checkArgument(email.getId() != null, "cannot upload attachments to email with id null");
        if (email.getTemplate() != null && templateService.getById(email.getTemplate()).hasTemplateAttachments()) {
            List<EmailAttachment> emailAttachments = applyTemplate(email, emailData, serverCard).getAttachments();
            logger.debug("email =< {} > adding {} template generated attachments =< {} >", email, emailAttachments.size(), emailAttachments);
            emailService.saveEmailAttachments(EmailImpl.copyOf(email).addAttachments(emailAttachments).build(), true);
        }
    }

    public static CmMapUtils.FluentMap<String, Object> serializeSimpleEmail(Email email) {
        return map(
                "_id", email.getId(),
                "from", email.getFrom(),
                "replyTo", email.getReplyTo(),
                "to", email.getTo(),
                "cc", email.getCc(),
                "bcc", email.getBcc(),
                "subject", email.getSubject(),
                "contentType", email.getContentType(),
                "date", toIsoDateTime(email.getDate()),
                "status", serializeEmailStatus(email.getStatus()),
                "delay", email.getDelay(),
                "keepSynchronization", email.getKeepSynchronization(),
                "promptSynchronization", email.getPromptSynchronization(),
                "isReadByUser", email.isReadByUser(),
                "template", email.getTemplate()
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicEmail(Email email) {
        return serializeSimpleEmail(email).accept(m -> {
            EmailTemplate emailTemplate = email.getTemplate() != null ? templateService.getByIdOrNull(email.getTemplate()) : null;
            m.put("_hasTemplateAttachments", emailTemplate != null ? emailTemplate.hasTemplateAttachments() : false);
        });
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email) {
        return serializeDetailedEmail(email, null);
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email, @Nullable Boolean expr) {
        String html = email.getContentHtmlOrWrappedPlaintext();
        if (email.hasId() && dmsService.isEnabled()) {
            html = embedEmailInlineAttachmentsAsBase64(html, list(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email.getId())).map(d -> EmailAttachmentImpl.build(toDataSource(dmsService.download(d.getDocumentId(), null)))));
        }

        logger.debug("serializing email =< {} > with generated [{}] attachments =< {} >", email.getId(), email.getAttachments().size(), email.getAttachments());

        return serializeBasicEmail(email).with(
                "account", email.getAccount(),
                "signature", email.getSignature(),
                "autoReplyTemplate", email.getAutoReplyTemplate(),
                "noSubjectPrefix", email.getNoSubjectPrefix(),
                "body", email.getContent(),
                "_content_plain", email.getContentPlaintext(),
                "_content_html", html,
                "card", email.getReference()
        ).skipNullValues().with("_expr", expr);
    }

    public Set<EmailTemplateSerializationType> templateSerializationType(boolean applyTemplate, boolean templateOnly, boolean applyTemplateAttachments) {
        FluentSet<EmailTemplateSerializationType> setTemplateSerializationType = set();
        if (templateOnly) {
            setTemplateSerializationType.with(ETS_APPLYTEMPLATE, ETS_TEMPLATEONLY);
        }
        if (applyTemplate) {
            setTemplateSerializationType.with(ETS_APPLYTEMPLATE);
        }
        if (applyTemplateAttachments) {
            setTemplateSerializationType.with(ETS_UPLOADTEMPLATEATTACHMENTS);
        }
        return setTemplateSerializationType;
    }

    /**
     * Check if synthesized by UI to handle generation of a report even if a
     * card is not available, after an import/export operation
     *
     * @param resourceId
     * @return
     */
    private static boolean isSinthesizedResource(String resourceId) {
        return equal(resourceId, "_ANY");
    }

}
