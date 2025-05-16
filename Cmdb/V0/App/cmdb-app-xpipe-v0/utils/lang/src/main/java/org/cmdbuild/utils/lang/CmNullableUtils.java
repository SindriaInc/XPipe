/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.notNull;
import static java.util.Arrays.asList;
import java.util.Collection;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class CmNullableUtils {

    public static boolean isNullOrBlank(@Nullable Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String string) {
            return StringUtils.isBlank(string);
        } else if (value instanceof Collection collection) {
            return collection.isEmpty() || collection.stream().allMatch(CmNullableUtils::isNullOrBlank);
        } else {
            return false;
        }
    }

    public static boolean isNullOrEmpty(@Nullable byte[] value) {
        return value == null || value.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String string) {
            return StringUtils.isEmpty(string);
        } else {
            return false;
        }
    }

    public static boolean isBlank(@Nullable Object value) {
        return isNullOrBlank(value);
    }

    public static boolean isNotBlank(@Nullable Object value) {
        return isNotNullNorBlank(value);
    }

    public static boolean isNotNullNorBlank(@Nullable Object value) {
        return !isNullOrBlank(value);
    }

    public static <T> T firstNotNull(T... values) {
        return asList(values).stream().filter(notNull()).findFirst().get();
    }

    public static <T> T nullIf(@Nullable T value, T nullValue) {
        if (equal(value, nullValue)) {
            return null;
        } else {
            return value;
        }
    }

    @Nullable
    public static <T> T firstNotNullOrNull(@Nullable T first, @Nullable T second) {
        return first != null ? first : second;
    }

    @Nullable
    public static <T> T firstNotNullOrNull(T... values) {
        return asList(values).stream().filter(notNull()).findFirst().orElse(null);
    }

    @Nullable
    public static Integer parseNullableInteger(@Nullable String nullableValue) {
        return isNullOrBlank(nullableValue) ? null : Integer.valueOf(nullableValue);
    }

    @Nullable
    public static Boolean parseNullableBoolean(@Nullable String nullableValue) {
        return isNullOrBlank(nullableValue) ? null : Boolean.valueOf(nullableValue);
    }

    @Nullable
    public static <E extends Enum<E>> E parseNullableEnum(@Nullable String nullableValue, Class<E> enumClass) {
        return isNullOrBlank(nullableValue) ? null : Enum.valueOf(enumClass, nullableValue);
    }

    @Nullable
    public static <T extends Number> T ltEqZeroToNull(@Nullable T input) {
        if (input == null || input.longValue() <= 0) {
            return null;
        } else {
            return input;
        }
    }

    @Nullable
    public static <T extends Number> T ltZeroToNull(@Nullable T input) {
        if (input == null || input.longValue() < 0) {
            return null;
        } else {
            return input;
        }
    }

    public static boolean isNotNullAndGtZero(@Nullable Number number) {
        return number != null && number.longValue() > 0;
    }

    public static boolean isNullOrLtEqZero(@Nullable Number number) {
        return number == null || number.longValue() <= 0;
    }

    /**
     * @return Void if param is null, obj.class otherwise
     */
    public static Class getClassOfNullable(@Nullable Object obj) {
        if (obj == null) {
            return Void.class;
        } else {
            return obj.getClass();
        }
    }

    public static String getClassNameOfNullable(@Nullable Object obj) {
        return getClassOfNullable(obj).getName();
    }

    public static Class nullToVoid(@Nullable Class classe) {
        return firstNonNull(classe, Void.class);
    }

}
