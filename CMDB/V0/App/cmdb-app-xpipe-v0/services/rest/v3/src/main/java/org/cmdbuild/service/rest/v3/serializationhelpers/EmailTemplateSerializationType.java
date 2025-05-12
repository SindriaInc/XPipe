/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

/**
 *
 * @author ataboga
 */
public enum EmailTemplateSerializationType {

    /**
     * ETS_APPLYTEMPLATE is independent
     * ETS_UPLOADTEMPLATEATTACHMENTS is independent
     * ETS_TEMPLATEONLY implicates ETS_APPLYTEMPLATE
     */
    ETS_TEMPLATEONLY, ETS_APPLYTEMPLATE, ETS_UPLOADTEMPLATEATTACHMENTS

}
