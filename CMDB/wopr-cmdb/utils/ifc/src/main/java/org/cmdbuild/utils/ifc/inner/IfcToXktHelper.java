package org.cmdbuild.utils.ifc.inner;

import org.cmdbuild.utils.io.BigByteArray;

public interface IfcToXktHelper {

    default byte[] ifcToXkt(byte[] ifc, Long conversionTimeout) {
        return ifcToXkt(new BigByteArray(ifc), conversionTimeout).toByteArray();
    }

    BigByteArray ifcToXkt(BigByteArray ifc, Long conversionTimeout);
    
}
