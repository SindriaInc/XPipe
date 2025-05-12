package org.cmdbuild.dms.cmis;

import org.cmdbuild.dms.DmsConfiguration;

public interface CmisDmsConfiguration extends CmisConfiguration, DmsConfiguration {

    String getCmisPath();
}
