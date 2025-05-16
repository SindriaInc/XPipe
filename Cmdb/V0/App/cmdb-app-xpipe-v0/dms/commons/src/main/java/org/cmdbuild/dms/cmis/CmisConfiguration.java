/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.cmis;

public interface CmisConfiguration {

    final String CMIS_URL = "url", CMIS_USER = "user", CMIS_PASSWORD = "password";

    String getCmisUrl();

    String getCmisUser();

    String getCmisPassword();
}
