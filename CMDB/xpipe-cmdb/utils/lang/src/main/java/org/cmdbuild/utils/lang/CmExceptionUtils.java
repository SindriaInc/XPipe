/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class CmExceptionUtils {

    /**
     * return a slf4j marker to mark log messages (usually warning messages) that
     * should be propagated to user
     *
     * @return
     */
    public static Marker marker() {
        return MarkerFactory.getMarker("NOTIFY");
    }

    public static Object lazyString(Supplier supplier) {
        checkNotNull(supplier);
        return new Object() {
            @Override
            public String toString() {
                return toStringOrNull(supplier.get());
            }
        };
    }

    public static RuntimeException cause(Throwable ex) {
        return extractCause(ex);
    }

    public static RuntimeException extractCause(Throwable ex) {
        if (ex.getCause() != null) {
            return toRuntimeException(ex.getCause());
        } else {
            return toRuntimeException(ex);
        }
    }

    public static RuntimeException runtime(Throwable ex) {
        return toRuntimeException(ex);
    }

    public static RuntimeException runtime(Throwable ex, String message, Object... args) {
        return new RuntimeException(format(message, args), ex);
    }

    public static RuntimeException runtime(String message, Object... args) {
        return new RuntimeException(format(message, args));
    }

    public static IllegalArgumentException illegalArgument(Throwable ex) {
        return new IllegalArgumentException(ex);
    }

    public static IllegalArgumentException illegalArgument(Throwable ex, String message, Object... args) {
        return new IllegalArgumentException(format(message, args), ex);
    }

    public static IllegalArgumentException illegalArgument(String message, Object... args) {
        return new IllegalArgumentException(format(message, args));
    }

    public static UnsupportedOperationException unsupported(String message) {
        return new UnsupportedOperationException(message);
    }

    public static UnsupportedOperationException unsupported(String message, Object... args) {
        return new UnsupportedOperationException(format(message, args));
    }

    public static Exception inner(Exception ex) {
        if ((ex instanceof ExecutionException || ex instanceof InvocationTargetException) && (ex.getCause() != null) && (ex.getCause() instanceof Exception)) {
            ex = (Exception) ex.getCause();
        }
        return ex;
    }

    public static RuntimeException toRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException re) {
            return re;
        } else {
            return new RuntimeException(ex);
        }
    }

    public static String exceptionToMessage(Throwable ex) {
        List<String> messages = list();
        while (ex != null) {
            messages.add(ex.toString());
            ex = ex.getCause();
        }
        return Joiner.on(", caused by: ").join(messages);
    }

    public static String printStackTrace(Throwable ex) {
        StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            ex.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    public static String printStackTrace(Thread thread) {
        Throwable throwable = new Throwable();
        throwable.setStackTrace(thread.getStackTrace());
        return printStackTrace(throwable).replaceFirst("^.*?[\n\r]+\\s+", Matcher.quoteReplacement("thread \"%s\" is %s ".formatted(thread.getName(), thread.getState())));
    }

    public static @Nullable
    <T extends Throwable> T extractExceptionOrNull(Throwable ex, Class<T> type) {
        if (type.isInstance(ex)) {
            return type.cast(ex);
        } else {
            if (ex.getCause() == null) {
                return null;
            } else {
                return extractExceptionOrNull(ex.getCause(), type);
            }
        }
    }
}
