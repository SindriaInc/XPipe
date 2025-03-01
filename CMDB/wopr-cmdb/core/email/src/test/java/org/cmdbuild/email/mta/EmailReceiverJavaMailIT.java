/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.EmailAccountJavaMailHelper.buildEmailAccount_Receiver;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_DoNothing;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_MoveReceivedMsgs;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailRepository;
import static org.cmdbuild.email.mta.EmailTestHelper.mockLockService;
import org.cmdbuild.lock.LockService;
import org.junit.Test;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EmailReceiverJavaMailIT {

    /**
     * Test of testConnection method, of class EmailReceiverJavaMail.
     */
    @Test
    public void testTestConnection() {
        System.out.println("testConnection");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverJavaMail instance = buildEmailReceiver(emailAccount);

        // act:
        instance.testConnection(emailAccount);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, of class EmailReceiverJavaMail.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testReceiveMails() throws MessagingException {
        System.out.println("receiveMails");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverJavaMail instance = buildEmailReceiver(emailAccount);

        // act:
        // Throws MessagingException
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, moving received messages to another folder,
     * of class EmailReceiverJavaMail
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testReceiveMails_MoveReceivedMsgs() throws MessagingException {
        System.out.println("receiveMails_MoveReceivedMsgs");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverJavaMail instance = buildEmailReceiver(emailAccount, mockEmailReceiveConfig_MoveReceivedMsgs());

        // act:
        // Throws MessagingException
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    private EmailReceiverJavaMail buildEmailReceiver(EmailAccount emailAccount) {
        return buildEmailReceiver(emailAccount, mockEmailReceiveConfig_DoNothing());
    }

    private EmailReceiverJavaMail buildEmailReceiver(EmailAccount emailAccount, EmailReceiveConfig emailReceiveConfig) {
        when(emailReceiveConfig.getIncomingFolder()).thenReturn(EmailAccountJavaMailHelper.A_KNOWN_EMAIL_INCOMING_FOLDER);
        EmailRepository mockEmailRepository = mockEmailRepository();
        LockService mockLockService = mockLockService();
        EmailReceiverJavaMail instance = new EmailReceiverJavaMail(emailAccount, emailReceiveConfig, mockLockService, mockEmailRepository);
        return instance;
    }

}
