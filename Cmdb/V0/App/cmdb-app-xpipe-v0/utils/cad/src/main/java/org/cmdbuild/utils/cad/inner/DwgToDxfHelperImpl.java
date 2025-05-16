/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.inner;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.cmdbuild.utils.cad.CadUtils.getDwgVersion;
import org.cmdbuild.utils.cad.dxfparser.CadException;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DwgToDxfHelperImpl implements DwgToDxfHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String converterExecutable;

    public DwgToDxfHelperImpl(String converterExecutable) {
        this.converterExecutable = checkNotBlank(converterExecutable);
    }

    @Override
    public BigByteArray dwgToDxf(BigByteArray dwg) {

        logger.debug("preparing conversion of dwg to dxf; using converter =< {} >", converterExecutable);

        logger.debug("source file version =< {} >", firstNotBlank(getDwgVersion(dwg::toInputStream), "unknown"));

        File dir = tempDir();

        try {
            File sourceFile = new File(dir, "in.dwg");
            File targetFile = new File(dir, "out.dxf");
            copy(dwg.toInputStream(), sourceFile);

            logger.debug("begin conversion, source file = {} ( {} {} )", sourceFile, getContentType(sourceFile), byteCountToDisplaySize(sourceFile.length()));

            executeProcess(converterExecutable, sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());

            checkArgument(targetFile.isFile(), "target file not found (conversion failed)");

            logger.debug("conversion completed, found target file = {} ( {} {} )", targetFile, getContentType(targetFile), byteCountToDisplaySize(targetFile.length()));

            return toBigByteArray(targetFile);
        } catch (Exception ex) {
            throw new CadException(ex, "error converting dwg to dxf");
        } finally {
            deleteQuietly(dir);
        }
    }
}
