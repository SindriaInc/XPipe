package org.cmdbuild.utils.ifc.inner;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcToXktHelperImpl implements IfcToXktHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String converterExecutable;

    public IfcToXktHelperImpl(String converterExecutable) {
        this.converterExecutable = checkNotBlank(converterExecutable);
    }

    @Override
    public BigByteArray ifcToXkt(BigByteArray ifc, Long conversionTimeout) {
        logger.debug("preparing conversion of ifc to xkt; using converter =< {} >", converterExecutable);
        File dir = tempDir();
        try {
            File ifcSourceFile = new File(dir, "in.ifc");
            File xktTargetFile = new File(dir, "out.xkt");
            copy(ifc.toInputStream(), ifcSourceFile);

            logger.info("begin conversion, source file = {} ( {} {} )", ifcSourceFile, getContentType(ifcSourceFile), byteCountToDisplaySize(ifcSourceFile.length()));

            String output = executeProcess(list(converterExecutable, ifcSourceFile.getAbsolutePath(), xktTargetFile.getAbsolutePath()), conversionTimeout);

            checkArgument(xktTargetFile.isFile(), "target file not found (conversion failed); output=< %s >", output);

            logger.info("conversion completed, found target file = {} ( {} {} )", xktTargetFile, getContentType(xktTargetFile), byteCountToDisplaySize(xktTargetFile.length()));

            return toBigByteArray(xktTargetFile);
        } catch (Exception ex) {
            throw ex;
        } finally {
            deleteQuietly(dir);
        }
    }
}
