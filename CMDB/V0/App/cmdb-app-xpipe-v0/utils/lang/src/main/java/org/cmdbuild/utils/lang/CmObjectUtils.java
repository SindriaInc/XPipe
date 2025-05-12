/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.size;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.beanutils.PropertyUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.extractCmPrimitiveIfAvailable;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNullSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmObjectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static boolean cmEquals(@Nullable Object one, @Nullable Object two) {
        return equal(one, two) || equal(extractCmPrimitiveIfAvailable(one), extractCmPrimitiveIfAvailable(two));
    }

    public static long estimateObjectSizeBytes(@Nullable Object obj) {//TODO test this
        if (obj == null) {
            return 0;
        } else if (obj instanceof String) {
            return 32 + ((String) obj).length() * 2;
        } else if (obj instanceof byte[]) {
            return ((byte[]) ((byte[]) obj)).length;
        } else if (isPrimitiveOrWrapper(obj) || obj instanceof Class) {
            return 8;
        } else if (obj.getClass().isArray()) {
            return estimateObjectSizeBytes(convert(obj, List.class));
        } else if (obj instanceof Iterable) {
            return stream((Iterable<Object>) obj).mapToLong(CmObjectUtils::estimateObjectSizeBytes).sum() + 32 + size((Iterable) obj) * 8;
        } else if (obj instanceof Map) {
            return estimateObjectSizeBytes(((Map) obj).keySet()) + estimateObjectSizeBytes(((Map) obj).values()) + 32;
        } else {
            try {
                return estimateObjectSizeBytes(PropertyUtils.describe(obj));
            } catch (Throwable ex) {
                LOGGER.warn("error estimating size of object = {} type = {}", obj, getClassOfNullable(obj).getName(), ex);
                return estimateObjectSizeBytes(toStringOrNullSafe(obj));
            }
        }
    }
}
