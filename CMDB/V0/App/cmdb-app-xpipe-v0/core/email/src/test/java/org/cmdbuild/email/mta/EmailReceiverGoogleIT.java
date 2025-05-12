/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.data.EmailRepository;
import org.junit.Test;
import static org.cmdbuild.email.mta.EmailAccountGoogleHelper.buildEmailAccount_Receiver;
import static org.cmdbuild.email.mta.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailRepository;
import static org.cmdbuild.email.mta.EmailTestHelper.mockLockService;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_DoNothing;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_MoveReceivedMsgs;
import static org.cmdbuild.email.mta.TokenEncrypter.setJavaEncryptionKey;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author afelice
 */
public class EmailReceiverGoogleIT {

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    @Test
    public void testConnection() {
        System.out.println("testConnection");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount);

        // act:
        instance.testConnection(emailAccount);

        // assert:
        // nothing to assert
    }

    @Test
    public void testReceiveMails() throws MessagingException {
        System.out.println("receiveMails");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount);

        // act:
        instance.receiveMails();

        // assert:
        // nothing to assert
    }

    /**
     * Test of receiveMails method, moving received messages to another folder,
     * of class EmailReceiverGoogle
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_MoveReceivedMsgs() throws MessagingException {
        System.out.println("receiveMails_MoveReceivedMsgs");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount,
                                                          mockEmailReceiveConfig_MoveReceivedMsgs());

        // act:
        // Throws MessagingException
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    private EmailReceiverGoogle buildEmailReceiver(EmailAccount emailAccount) {
        return buildEmailReceiver(emailAccount,
                                  mockEmailReceiveConfig_DoNothing());
    }

    private EmailReceiverGoogle buildEmailReceiver(EmailAccount emailAccount,
                                                   EmailReceiveConfig emailReceiveConfig) {
        EmailRepository mockEmailRepository = mockEmailRepository();
        LockService mockLockService = mockLockService();
        EmailReceiverGoogle instance = new EmailReceiverGoogle(emailAccount,
                                                               emailReceiveConfig,
                                                               mockLockService,
                                                               mockEmailRepository);
        return instance;
    }


}
