/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.microsoft.graph.requests.GraphServiceClient;
import java.io.File;
import static org.junit.Assert.assertTrue;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.mta.EmailProviderStrategyTest.PASSWORD_MS_CLIENT_CERTIFICATE;
import static org.cmdbuild.email.mta.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailAccountWithPassword;
import static org.cmdbuild.email.mta.TokenEncrypter.setJavaEncryptionKey;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_ClientCertificateIT {

    @BeforeClass
    public static void setup() {
        setJavaEncryptionKey();
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of create method, of class EmailMSGraphClient_ClientCertificate.
     */
    @Test
    public void testCreate() {
        System.out.println("create");

        // arrange:
        EmailAccount emailAccount = mockEmailAccountWithPassword(
                PASSWORD_MS_CLIENT_CERTIFICATE);

        // act:
        File tempFile = null;
        try ( EmailMSGraphClient_ClientCertificate instance = new EmailMSGraphClient_ClientCertificate(emailAccount)) {
            GraphServiceClient result = instance.create();
            tempFile = instance._tempCertificateFile;
            assertTrue(tempFile.exists());
        } // Test close(): temporary file should be deleted
        assertFalse(tempFile.exists());
    }

}
