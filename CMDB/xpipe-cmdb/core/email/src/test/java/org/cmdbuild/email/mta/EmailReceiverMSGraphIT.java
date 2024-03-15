/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.mta.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailRepository;
import static org.cmdbuild.email.mta.EmailTestHelper.mockLockService;
import org.junit.Test;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_DoNothing;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_MoveReceivedMsgs;
import static org.cmdbuild.email.mta.TokenEncrypter.setJavaEncryptionKey;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author afelice
 */
public class EmailReceiverMSGraphIT {

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of testConnection method, authentication delegated, of class
     * EmailReceiverMSGraph.
     */
    @Test
    public void testTestConnection_Delegated() {
        System.out.println("testConnection_Delegated");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_Password(
                                                                   emailAccount));

        // act: 
        instance.testConnection(emailAccount);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, authentication delegated, of class
     * EmailReceiverMSGraph.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_Delegated() throws MessagingException {
        System.out.println("receiveMails_Delegated");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_Password(
                                                                   emailAccount));

        // act: 
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, moving received messages to another folder,
     * authentication delegated, of class EmailReceiverMSGraph.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_Delegated_MoveReceivedMsgs() throws MessagingException {
        System.out.println("receiveMails_Delegated_MoveReceivedMsgs");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_Password(
                                                                   emailAccount),
                                                           mockEmailReceiveConfig_MoveReceivedMsgs());

        // act: 
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of testConnection method, authentication Client secret, of class
     * EmailReceiverMSGraph.
     */
    @Test
    public void testTestConnection_ClientSecret() {
        System.out.println("testConnection_ClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_ClientSecret(
                                                                   emailAccount));

        // act: 
        instance.testConnection(emailAccount);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, authentication Client secret, of class
     * EmailReceiverMSGraph.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_ClientSecret() throws MessagingException {
        System.out.println("receiveMails_ClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_ClientSecret(
                                                                   emailAccount));

        // act: 
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, moving received messages to another folder,
     * authentication Client secret, of class EmailReceiverMSGraph.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_ClientSecret_MoveReceivedMsgs() throws MessagingException {
        System.out.println("receiveMails_ClientSecret_MoveReceivedMsgs");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_ClientSecret(
                                                                   emailAccount),
                                                           mockEmailReceiveConfig_MoveReceivedMsgs());

        // act: 
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of testConnection method, authentication Client certificate, of
     * class EmailReceiverMSGraph.
     */
    @Test
    public void testTestConnection_ClientCertificate() {
        System.out.println("testConnection_ClientCertificate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_ClientCertificate(
                                                                   emailAccount));

        // act: 
        instance.testConnection(emailAccount);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of receiveMails method, authentication Client certificate, of class
     * EmailReceiverMSGraph.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testReceiveMails_ClientCertificate() throws MessagingException {
        System.out.println("receiveMails_ClientCertificate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailReceiverMSGraph instance = buildEmailReceiver(emailAccount,
                                                           new EmailMSGraphClient_ClientCertificate(
                                                                   emailAccount));

        // act: 
        instance.receiveMails();

        // assert:
        // Nothing to assert
    }


    private static EmailReceiverMSGraph buildEmailReceiver(
            EmailAccount emailAccount,
            BaseEmailMSGraphClientProvider emailMSGraphClientProvider) {
        return buildEmailReceiver(emailAccount, emailMSGraphClientProvider,
                                  mockEmailReceiveConfig_DoNothing());
    }

    private static EmailReceiverMSGraph buildEmailReceiver(
            EmailAccount emailAccount,
            BaseEmailMSGraphClientProvider emailMSGraphClientProvider,
            EmailReceiveConfig emailReceiveConfig) {
        return new EmailReceiverMSGraph(emailAccount,
                                        emailMSGraphClientProvider,
                                        emailReceiveConfig,
                                        mockLockService(),
                                        mockEmailRepository());
    }

}
