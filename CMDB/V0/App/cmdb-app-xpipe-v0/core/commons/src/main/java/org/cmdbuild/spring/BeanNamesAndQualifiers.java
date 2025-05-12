/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.spring;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 *
 */
public interface BeanNamesAndQualifiers {

//	public static final String UPLOAD = "upload";
//	public static final String ROOT = "root";
    public static final String DEFAULT = "default", TEMPORARY = "temporary";
    /**
     * @deprecated use {@link ConfigurableBeanFactory.SCOPE_PROTOTYPE} instead.
     */
    @Deprecated
    public static final String PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
    public static final String WEB_SERVICE = "webservice", SOAP_WEB_SERVICE = "soap_webservice", REST_WEB_SERVICE = "rest_webservice";
//	public static final String SYSTEM_LEVEL_TWO = "system";
    public static final String LOCALIZED = "localized";
//	public static final String USER = "user";

    public static final String RAW_DATA_SOURCE = "rawDataSource";

    /**
     * require only db access
     *
     * lower level, direct db access, no tenant security, cache support, or any
     * access control (note: tenant-enabled tables may not work correctly)
     */
    public static final String SYSTEM_LEVEL_ONE = "system_level_one";

    /**
     * require db access, cache (and clustering config), and tenant
     * configuration
     *
     * tenant system enabled (but with admin access). This is the default system
     * access level
     */
    public static final String SYSTEM_LEVEL_TWO = "system_level_two";

    public static final String INNER = "inner";

    public static final String SERVICES_V3 = "services_rest_v3";

    public static final String DATABASE = "database", FILE = "file";
}
