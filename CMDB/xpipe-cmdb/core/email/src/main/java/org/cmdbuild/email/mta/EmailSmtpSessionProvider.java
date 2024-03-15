/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.Session;
import javax.mail.Transport;

/**
 *
 * @author afelice
 */
public interface EmailSmtpSessionProvider extends AutoCloseable {

    /**
     * @return a {@link javax.mail.Session} for given emailAcocunt
     */
    Session getSession();

    /**
     * {@link javax.mail.Session#getTransport()}
     *
     * @return a already connected {@link javax.mail.Transport}
     * @see javax.mail.Transport#connect()
     */
    Transport getTransport();

    @Override
    void close();

}
