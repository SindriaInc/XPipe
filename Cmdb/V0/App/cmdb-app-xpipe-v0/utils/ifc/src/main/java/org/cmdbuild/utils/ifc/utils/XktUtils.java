package org.cmdbuild.utils.ifc.utils;

import com.google.common.base.Suppliers;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;
import javax.activation.DataSource;
import org.cmdbuild.utils.ifc.inner.IfcToXktHelper;
import org.cmdbuild.utils.ifc.inner.IfcToXktHelperImpl;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XktUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static Supplier<IfcToXktHelper> HELPER = Suppliers.memoize(XktUtils::prepareHelper);

    public static byte[] ifcToXkt(byte[] ifc, Long conversionTimeout) {
        return HELPER.get().ifcToXkt(ifc, conversionTimeout);
    }

    public static BigByteArray ifcToXkt(BigByteArray ifc, Long conversionTimeout) {
        return HELPER.get().ifcToXkt(ifc, conversionTimeout);
    }

    public static BigByteArray ifcToXkt(DataSource ifc, Long conversionTimeout) {
        return HELPER.get().ifcToXkt(toBigByteArray(ifc), conversionTimeout);
    }

    private static IfcToXktHelper prepareHelper() {
        LOGGER.debug("prepare ifc to xkt converter helper");
        File scriptFile = new File(tempDir(), "helper.sh");
        copy(XktUtils.class.getResourceAsStream("/org/cmdbuild/utils/ifc/ifc_to_xkt_converter.sh"), scriptFile);
        scriptFile.setExecutable(true);
        return new IfcToXktHelperImpl(scriptFile.getAbsolutePath());
    }

}
