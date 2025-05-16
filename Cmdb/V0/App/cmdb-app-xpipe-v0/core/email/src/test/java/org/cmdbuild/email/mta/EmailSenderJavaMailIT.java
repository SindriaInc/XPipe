/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.mta.EmailTestHelper.A_KNOWN_IN_REPLY_TO;
import static org.cmdbuild.email.mta.EmailTestHelper.A_USER_DESTINATION_EMAIL;
import static org.cmdbuild.email.mta.EmailTestHelper.buildTestEmail;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Test;
import static org.cmdbuild.email.mta.EmailAccountJavaMailHelper.buildEmailAccount_Sender;
import static org.cmdbuild.email.mta.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailQueueConfiguration;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailSignatureService;
import static org.cmdbuild.email.mta.TokenEncrypter.setJavaEncryptionKey;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author afelice
 */
public class EmailSenderJavaMailIT {

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of sendEmail method, of class EmailSenderJavaMail.
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testSendMail() throws IOException, MessagingException {
        System.out.println("sendMail");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Sender();
        EmailSenderJavaMail instance = buildEmailSender(emailAccount);

        // (No) Attachments
        List<EmailAttachment> attachments = list();
        Email emailToSend = buildTestEmail(List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);
        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // nothing to assert
    }

    /**
     * Test of testConnection method, of class EmailSenderJavaMail.
     * @throws java.io.IOException
     */
    @Test
    public void testConnection() throws IOException {
        System.out.println("testConnection");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Sender();
        EmailSenderJavaMail instance = buildEmailSender(emailAccount);

        // act:
        instance.testConnection(emailAccount);

        // assert:
        // nothing to assert
    }

    private EmailSenderJavaMail buildEmailSender(EmailAccount emailAccount) throws IOException {
        EmailQueueConfiguration mockEmailQueueConfiguration = mockEmailQueueConfiguration();
        EmailSignatureService mockEmailSignatureService = mockEmailSignatureService();
        EmailSenderJavaMail instance = new EmailSenderJavaMail(emailAccount,
                                                               mockEmailQueueConfiguration,
                new EmailTestHelper.MockInstanceInfoService(),
                mockEmailSignatureService);
        return instance;
    }

}
