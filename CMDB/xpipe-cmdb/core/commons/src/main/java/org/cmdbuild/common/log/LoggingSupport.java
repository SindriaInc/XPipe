package org.cmdbuild.common.log;

import static com.google.common.reflect.Reflection.getPackageName;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

@Deprecated
public interface LoggingSupport {

	Logger logger = getLogger(getPackageName(LoggingSupport.class));

}
