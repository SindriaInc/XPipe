package org.cmdbuild.etl.job;

public interface EtlResultProcessingService {

    EtlResultProcessor getResultProcessor(EtlResultProcessingConfig config);

}
