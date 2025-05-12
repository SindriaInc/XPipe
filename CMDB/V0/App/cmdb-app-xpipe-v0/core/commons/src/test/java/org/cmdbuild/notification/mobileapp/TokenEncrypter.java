/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.notification.mobileapp;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;

/**
 *
 * @author afelice
 */
class TokenEncrypter {

    static String encrypt(InputStream inputStream) throws IOException {
        return encrypt(IOUtils.toString(inputStream));
    }

    static String encrypt(String content) {

        String encryptedContent = content;
        setJavaEncryptionKey();
        if (!Cm3EasyCryptoUtils.isEncrypted(content)) {
            encryptedContent = Cm3EasyCryptoUtils.encryptValue(content);
        }

        return encryptedContent;

    }
    
    static void setJavaEncryptionKey() {
        System.setProperty("org.cmdbuild.cm3easy.key",
                           "/usr/local/bin/key_cm3easy");
    }

    static void clearJavaEncryptionKey() {
        System.clearProperty("org.cmdbuild.cm3easy.key");
    }    
}
