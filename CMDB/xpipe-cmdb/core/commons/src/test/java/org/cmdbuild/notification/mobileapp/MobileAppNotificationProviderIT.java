/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.notification.mobileapp;

import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.notification.NotificationStatus;
import static org.cmdbuild.notification.mobileapp.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.notification.mobileapp.TokenLoaderHelper.loadCredentialsStr;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class MobileAppNotificationProviderIT {

    private static final String TOPIC_CMDBUILD_TEST = "topic_cmdbuild_test_2"; // Change this if receiving "message": "Topic quota exceeded.", "status": "RESOURCE_EXHAUSTED"
    
    static String GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS;
    private static final String GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH = "/org/cmdbuild/notification/mobileapp/service/test/tokens/cmdbuild-mobile-test-firebase-adminsdk-jsz6a-806345d57c.encr";

    static {
        GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS = loadCredentialsStr(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH, MobileAppNotificationProviderIT.class.getName());
    }
    
    private static final String MISSING_PROJECT_ID_CREDENTIALS = """
                                                                 {
                                                                   "type": "service_account",
                                                                   "client_id": "108038488858066818469"
                                                                 }
                                                                 """;

    private static final String MISSING_CLIENT_ID_CREDENTIALS = """
                                                                {
                                                                  "type": "service_account",
                                                                  "project_id": "tecn77"
                                                                }
                                                                """;
    
    MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    
    @BeforeClass
    public static void setUpClass() {
        try (InputStream serviceAccount = IOUtils.toInputStream(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS)){
            // Nothing to do
        } catch (IOException ex) {
            System.err.println(format("error in test %s - error opening stream from file =< %s >",
                    MobileAppNotificationProviderIT.class.getName(),
                    GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH));
        }
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }    
   
    /**
     * Test of sendNotification method, of class MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification() {
        System.out.println("sendNotification");
        
        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(TOPIC_CMDBUILD_TEST)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        MobileAppNotificationProvider instance = new MobileAppNotificationProvider(new FirebaseSender(), mobileConfiguration);
        
        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);
        
        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());        
    }

    /**
     * Test of sendNotification method, of class MobileAppNotificationProvider. mobile disabled.
     */
    @Test
    public void testSendNotification_MobileDisabled() {
        System.out.println("sendNotification_MobileDisabled");
        
        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(TOPIC_CMDBUILD_TEST)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(false);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        FirebaseSender sender = mock(FirebaseSender.class);
        MobileAppNotificationProvider instance = new MobileAppNotificationProvider(sender, mobileConfiguration);
        
        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);
        
        // assert:
        verifyZeroInteractions(sender);
        assertEquals(NotificationStatus.NS_ERROR, result.getStatus());        
    }    
    
    /**
     * Test of sendNotification method, missing project_id in auth json, of class MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification_MissingProjectId() {
        System.out.println("sendNotification_MissingProjectId");
        
        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(TOPIC_CMDBUILD_TEST)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(MISSING_PROJECT_ID_CREDENTIALS);
        MobileAppNotificationProvider instance = new MobileAppNotificationProvider(new FirebaseSender(), mobileConfiguration);
        
        // act:
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("Invalid serviceAccount json, project_id is missing");
        MobileAppNotificationData result = instance.sendNotification(notificationData);
        
        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());        
    }

    /**
     * Test of sendNotification method, missing project_id in auth json, of class MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification_MissingClientId() {
        System.out.println("sendNotification_MissingClientId");
        
        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(TOPIC_CMDBUILD_TEST)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(MISSING_CLIENT_ID_CREDENTIALS);
        MobileAppNotificationProvider instance = new MobileAppNotificationProvider(new FirebaseSender(), mobileConfiguration);
        
        // act:
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("Invalid serviceAccount json, client_id is missing");
        MobileAppNotificationData result = instance.sendNotification(notificationData);
        
        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());        
    }    
    
    /**
     * Test of release method, of class MobileAppNotificationProvider.
     */
    @Test
    public void testRelease() {
        System.out.println("release");
        
        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withAuthInfo(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS)
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(TOPIC_CMDBUILD_TEST)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        MobileAppNotificationProvider instance = new MobileAppNotificationProvider(new FirebaseSender(), mobileConfiguration);
        instance.sendNotification(notificationData);
        
        // act:
        boolean result = instance.releaseSender(notificationData);
        
        // assert:
        assertTrue(result);    
    }
    
}
