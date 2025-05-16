package org.cmdbuild.config;

import javax.annotation.Nullable;

public interface EtlConfiguration {

    String getThousandsSeparator();

    String getValueArraySeparator();

    @Nullable
    Integer getTemplateProcessingThreadCount();

    Integer getReferenceCacheMaxSize();

    Boolean allowDuplicateAttachmentName();

}
