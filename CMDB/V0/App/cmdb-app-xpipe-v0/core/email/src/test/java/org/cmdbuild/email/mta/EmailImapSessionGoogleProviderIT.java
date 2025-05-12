/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.MessagingException;
import javax.mail.Store;
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
public class EmailImapSessionGoogleProviderIT {

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of getStore method, of class EmailImapSessionJavaMailProvider.
     *
     * @throws javax.mail.MessagingException
     */
    @Test
    public void testGetStore() throws MessagingException {
        System.out.println("getStore");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Receiver();

        Store store;
        try ( EmailImapSessionProvider emailImapSessionProvider = new EmailImapSessionGoogleProvider(
                emailAccount, logger)) {
            // act:
            store = emailImapSessionProvider.getStore();

            // assert:
            assertTrue(store.isConnected());
        } // assert fro close():
        assertFalse(store.isConnected());
    }

}
