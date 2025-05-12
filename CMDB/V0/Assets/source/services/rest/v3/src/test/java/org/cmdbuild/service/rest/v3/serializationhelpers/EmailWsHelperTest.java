/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import com.google.common.base.Predicate;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.DMS_CATEGORY;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.service.rest.v3.model.WsCardData;
import org.cmdbuild.service.rest.v3.model.WsEmailData;
import static org.cmdbuild.service.rest.v3.serializationhelpers.EmailTemplateSerializationType.ETS_APPLYTEMPLATE;
import static org.cmdbuild.service.rest.v3.serializationhelpers.EmailTemplateSerializationType.ETS_TEMPLATEONLY;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EmailWsHelperTest {

    private static final String A_KNOWN_DMS_CATEGORY = "aDmsCategory";
    private static final String A_KNOWN_FILENAME = "file.txt";
    private static final String A_KNOWN_AUTHOR = "Paolino Paperino";

    private static final String SYNTHESIZED_RESOURCE = "_ANY";

    private static final String A_KNOWN_TEMPLATE_TEXT = """
                                    TestFileAttr: {server:%1$s}
                                    TestFileAttr: description {server:%1$s.Description}, code {server:%1$s.Code}, category {server:%1$s.Category}, name {server:%1$s.Filename}, author {server:%1$s.Author}
                                    """;

    private static final String A_KNOWN_TEMPLATE_BINDED_TEXT = """
                                    TestFileAttr: %s
                                    TestFileAttr: description %s, code %s, category %s, name %s, author %s
                                    """;

    private final Classe classe = mock(Classe.class);

    private final DaoService dao = mock(DaoService.class);
    private final EmailService emailService = mock(EmailService.class);
    private final EmailTemplateService templateService = mock(EmailTemplateService.class);
    private final EmailAccountService accountService = mock(EmailAccountService.class);
    private final DmsService dmsService = mock(DmsService.class);
    private final OperationUserSupplier operatorUser = mock(OperationUserSupplier.class);

    private EmailWsHelper instance;

    @Before
    public void setUp() {
        instance = new EmailWsHelper(dao, emailService, templateService, accountService, dmsService, operatorUser);
    }

    /**
     * Test of createEmail method, of class EmailWsHelper.
     */
    @Test
    public void testCreateEmail() {
        System.out.println("createEmail");

        //arrange:
        final String aFileAttrName = "FileAttribute";
        // Card sintetizzata dalla UI per inviare un report dopo un import/export

        when(classe.getName()).thenReturn("TestClass");
        when(classe.getClassType()).thenReturn(ClassType.CT_STANDARD);
        when(classe.asClasse()).thenReturn(classe);

        // Attribute
        Attribute aFileAttribute = mockBuildAttrFile(aFileAttrName, classe, A_KNOWN_DMS_CATEGORY);
        // ...and related card
        Card aRelatedSynthesizedCard = CardImpl.builder()
                .withType(classe)
                .withAttribute(aFileAttrName, aFileAttribute)
                .build();

        // WsEmailData
        long aEmailSignatureId = 5L;
        String aFrom = "aFrom";
        String aReplyTo = "aReplyTo";
        String aTo = "aTo";
        String aSubject = "aSubject";
        String aAccountCode = "myAccount";
        Long aAccountId = 53L;
        when(accountService.getAccountByIdOrCode(eq(aAccountCode))).thenReturn(EmailAccountImpl.builder().withId(aAccountId).build());
        // Template
        String aTemplateCode = "aTemplateCode";
        String aTemplateDescription = "aTemplateDescription";
        String expBody = format(A_KNOWN_TEMPLATE_BINDED_TEXT, aFileAttrName, aTemplateDescription, aTemplateCode, A_KNOWN_DMS_CATEGORY, A_KNOWN_FILENAME, A_KNOWN_AUTHOR);
        when(templateService.isSysTemplate(aTemplateCode)).thenReturn(false);
        final Long aTemplateId = 101L;
        EmailTemplate aEmailTemplate = mockBuildFileAttrEmailTemplate(aTemplateId, aTemplateCode, aTemplateDescription, aFileAttrName);
        when(templateService.getByNameOrId(aTemplateCode)).thenReturn(aEmailTemplate);
        // See EmailWsHelper:79: represents a UI synthesized card
        FluentMap< String, Object> synthesizedAttributes = map();
        synthesizedAttributes.with("_type", classe.getName()) // the real class
                .with(aRelatedSynthesizedCard.getAllValuesAsMap());
        final WsCardData wsCardData = new WsCardData(synthesizedAttributes);
        WsEmailData wsEmailData = new WsEmailData(null, aFrom, aReplyTo, aTo, null,
                null, null,
                null, "text/plain", aAccountCode, aEmailSignatureId,
                aTemplateCode,
                null, null, null, null, null, null, null, wsCardData);
        when(dao.getClasse(classe.getName())).thenReturn(classe);
        final ZonedDateTime aBeginDateTime = toDateTime("2022-08-22T14:40:00Z");
        Email expEmail = wsEmailData.toEmail() // as in EmailWsHelper.createEmail() on emailData.toEmail()
                .withTemplate(aTemplateId)
                .withSubject(aSubject)
                .withContent(expBody)
                .withAccount(aAccountId)
                .withBeginDate(aBeginDateTime)
                .build();                 // as in handleTemplate()
        when(emailService.applyTemplate(any(Email.class), any(Card.class), any(Card.class))).thenReturn(expEmail);

        List<EmailAttachment> emailAttachments = list(mockBuildEmailAttachment("templateFile.txt", aEmailTemplate));

        // Real effect fo serializeDetailedEmail(email, expr) in EmailWsHelper.createEmail()
        FluentMap< String, Object> expResult = map("from", aFrom,
                "replyTo", aReplyTo,
                "to", aTo,
                "subject", aSubject,
                "contentType", "text/plain",
                "date", toIsoDateTime(aBeginDateTime),
                "status", "draft",
                "keepSynchronization", false,
                "promptSynchronization", false,
                "isReadByUser", false,
                "template", aTemplateId,
                "_hasTemplateAttachments", false,
                "account", aAccountId,
                "signature", aEmailSignatureId,
                "noSubjectPrefix", false,
                "body", expBody,
                "_content_plain", expBody,
                "_content_html", expEmail.getContentHtml());

        //act:
        // Returns a serialization map()
        Object result = instance.createEmail(SYNTHESIZED_RESOURCE, SYNTHESIZED_RESOURCE,
                wsEmailData,
                set(ETS_APPLYTEMPLATE, ETS_TEMPLATEONLY),
                emailAttachments);
        System.out.println("result: " + result);

        //assert:
        assertEquals(expResult, removeEmptyEntries((FluentMap< String, Object>) result));
    }

    private static Attribute mockBuildAttrFile(String name, Classe classe, String fileDmsCategory) {
        return mockBuildAttr(name, FileAttributeType.INSTANCE, classe,
                new String[]{DMS_CATEGORY, fileDmsCategory});
    }

    private static Attribute mockBuildAttr(String name, CardAttributeType type, Classe classe,
            String[] metaValues) {
        return AttributeImpl.builder().withName(name).withDescription(name).withType(type)
                .withMeta(metaValues).withOwner(classe).build();
    }

    private static EmailTemplate mockBuildFileAttrEmailTemplate(final Long templateId, final String templateCode,
            String templateDescription,
            String fileAttrName) {
        return EmailTemplateImpl.builder()
                .withId(templateId)
                .withCode(templateCode)
                .withDescription(templateDescription)
                .withContentType("text/plain")
                .withContent(format(A_KNOWN_TEMPLATE_TEXT, fileAttrName))
                .withSubject("Test email template for file attribute")
                .build();
    }

    private static EmailAttachment mockBuildEmailAttachment(String templateFilename, EmailTemplate emailTemplate) {
        return EmailAttachmentImpl.builder()
                .withFileName(templateFilename)
                .withData(emailTemplate.getContent().getBytes())
                .withContentType(emailTemplate.getContentType())
                .build();
    }

    private FluentMap<String, Object> removeEmptyEntries(FluentMap<String, Object> inputMap) {
        return inputMap.withoutValues(PredicateHelper.NULL_OR_EMPTY_PREDICATE);
    }

}

class PredicateHelper {

    final static Predicate NULL_OR_EMPTY_PREDICATE = (Predicate) (final Object input) -> {
        if (input == null) {
            return true;
        }

        if (input instanceof String str) {
            return StringUtils.isBlank(str);
        }

        return false;
    };

    } // end PredicateHelper class
