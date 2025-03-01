/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad;

import com.google.common.base.Suppliers;
import jakarta.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FilenameUtils.getExtension;
import org.cmdbuild.utils.cad.dxfparser.CadException;
import org.cmdbuild.utils.cad.dxfparser.DxfReader;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.inner.DwgToDxfHelper;
import org.cmdbuild.utils.cad.inner.DwgToDxfHelperImpl;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.hasContentType;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.safeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static Supplier<DwgToDxfHelper> HELPER = Suppliers.memoize(CadUtils::prepareHelper);

    public static byte[] dwgToDxf(byte[] dwg) {
        return HELPER.get().dwgToDxf(dwg);
    }

    public static BigByteArray dwgToDxf(BigByteArray dwg) {
        return HELPER.get().dwgToDxf(dwg);
    }

    public static BigByteArray dwgToDxf(DataSource dwg) {
        return HELPER.get().dwgToDxf(toBigByteArray(dwg));
    }

    @Nullable
    public static String getDwgVersion(DataSource dwg) {
        return getDwgVersion(safeSupplier(dwg::getInputStream));
    }

    @Nullable
    public static String getDwgVersion(Supplier<InputStream> dwg) {
        try {
            try (InputStream in = dwg.get()) {
                Matcher matcher = Pattern.compile("^AC[0-9]+").matcher(new String(in.readNBytes(1024), US_ASCII));
                if (matcher.find()) {
                    return matcher.group();
                } else {
                    return null;
                }
            }
        } catch (Exception ex) {
            LOGGER.debug("unable to read dwg version", ex);
            return null;
        }
    }

    public static DxfDocument parseCadFile(DataSource data) {
        if (hasContentType(data, "image/vnd.dwg", "application/dwg")) {
            return parseDwgFile(data);
        } else if (hasContentType(data, "image/vnd.dxf")) {
            return parseDxfFile(data);
        }
        return switch (getExtension(data.getName()).toLowerCase()) {
            case "dwg" ->
                parseDwgFile(data);
            case "dxf" ->
                parseDxfFile(data);
            default ->
                throw new CadException("unable to detect type of cad file = %s", data);
        };
    }

    public static DxfDocument parseDwgFile(byte[] dwg) {
        return parseDxfFile(dwgToDxf(dwg));
    }

    public static DxfDocument parseDwgFile(BigByteArray dwg) {
        return parseDxfFile(dwgToDxf(dwg));
    }

    public static DxfDocument parseDwgFile(DataSource dwg) {
        return parseDxfFile(dwgToDxf(dwg));
    }

    public static DxfDocument parseDwgFile(InputStream dwg) {
        return parseDxfFile(dwgToDxf(toBigByteArray(dwg)));
    }

    public static DxfDocument parseDxfFile(byte[] dxf) {
        return parseDxfFile(new BigByteArray(dxf));
    }

    public static DxfDocument parseDxfFile(BigByteArray dxf) {
        return parseDxfFile(dxf.toInputStream());
    }

    public static DxfDocument parseDxfFile(DataSource file) {
        try (InputStream in = file.getInputStream()) {
            return parseDxfFile(in);
        } catch (IOException ex) {
            throw new CadException(ex);
        }
    }

    public static DxfDocument parseDxfFile(InputStream stream) {
        return new DxfReader().readStream(new InputStreamReader(stream, StandardCharsets.UTF_8));//TODO charset
    }

    private static DwgToDxfHelper prepareHelper() {
        LOGGER.debug("prepare dwg to dxf converter helper");
        File scriptFile = new File(tempDir(), "helper.sh");
        copy(CadUtils.class.getResourceAsStream("/org/cmdbuild/utils/cad/converter_support_script_1.sh"), scriptFile);
        scriptFile.setExecutable(true);
        return new DwgToDxfHelperImpl(scriptFile.getAbsolutePath());
    }

}
