/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author afelice
 */
public interface EmailImapSessionProvider extends AutoCloseable {

    /**
     * @return a {@link javax.mail.Session} for given emailAcocunt
     */
    Session getSession();

    /**
     * {@link javax.mail.Session#getStore()}
     *
     * @return a already connected {@link javax.mail.Store}
     * @see javax.mail.Store#connect()
     */
    Store getStore();

    void close();
}
