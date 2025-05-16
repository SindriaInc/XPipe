/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.MessagingException;
import javax.mail.Transport;
import static org.cmdbuild.dao.logging.LoggingSupport.logger;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.mta.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.email.mta.TokenEncrypter.setJavaEncryptionKey;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailSmtpSessionGoogleProviderIT {

    private static final Integer SMTP_TIMEOUT_SECONDS = null; // Timeout disabled

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of getTransport method, of class EmailSmtpSessionGoogleProvider.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testGetTransport() throws MessagingException {
        System.out.println("getTransport");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Sender();

        Transport transport;
        try ( EmailSmtpSessionProvider emailSmtpSessionProvider = new EmailSmtpSessionGoogleProvider(                        emailAccount, SMTP_TIMEOUT_SECONDS, logger)) {
            // act:
            transport = emailSmtpSessionProvider.getTransport();

            // assert: 
            assertTrue(transport.isConnected());
        } // assert fro close():
        assertFalse(transport.isConnected());
    }

}
