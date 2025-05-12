/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.MessagingException;

/**
 *
 * @author afelice
 */
public interface EmailReceiverProvider extends EmailProvider {
    void receiveMails() throws MessagingException;
}
