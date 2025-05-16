/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.oracleucm;

public interface OracleUcmConfiguration {

    String getOracleUcmIdcContextUsername();

    String getOracleUcmIdcUrl();

    String getOracleUcmSecurityGroup();

    int getOracleUcmTimeoutMillis();

    String getOracleUcmDescriptionField();
}
