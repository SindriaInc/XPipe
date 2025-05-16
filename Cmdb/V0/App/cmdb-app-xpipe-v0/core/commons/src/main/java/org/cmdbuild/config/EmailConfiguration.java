/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static java.util.Arrays.asList;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.TextContentSecurity;

public interface EmailConfiguration {

    @Nullable
    Integer getMaxAttachmentSizeForEmailMegs();

    String getDefaultEmailSignature();

    boolean emailJobContinueOnError();

    TextContentSecurity getDefaultTextContentSecurity();

    boolean allowDuplicateAttachmentName();

    default boolean hasDefaultTextContentSecurity(TextContentSecurity... values) {
        return asList(values).contains(getDefaultTextContentSecurity());
    }

}
