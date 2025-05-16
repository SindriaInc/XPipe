package org.cmdbuild.config;

/**
 *
 * @author ataboga
 */
public interface SoapConfiguration {

    final static String SOAP_CONFIG_NAMESPACE = "org.cmdbuild.services.soap",
            SOAP_CONFIG_ENABLED = "enabled";

    boolean isSoapEnabled();

}
