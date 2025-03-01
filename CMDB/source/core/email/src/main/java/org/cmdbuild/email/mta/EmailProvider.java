/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.email.EmailAccount;

/**
 * Strategy pattern interface
 *
 * @author afelice
 */
public interface EmailProvider {

    void testConnection(EmailAccount emailAccount);
}
