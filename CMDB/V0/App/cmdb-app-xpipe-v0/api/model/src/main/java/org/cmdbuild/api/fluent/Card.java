package org.cmdbuild.api.fluent;

import static org.cmdbuild.common.Constants.CODE_ATTRIBUTE;
import static org.cmdbuild.common.Constants.DESCRIPTION_ATTRIBUTE;

import java.util.Map;
import java.util.Set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public interface Card extends CardDescriptor {

    boolean hasAttribute(String name);

    Set<String> getAttributeNames();

    Map<String, Object> getAttributes();

    Object get(String name);

    void set(String name, Object value);

    default boolean has(String name) {
        return hasAttribute(name);
    }

    default <T> T get(String name, Class<T> clazz) {
        return convert(get(name), clazz);
    }

    default String getCode() {
        return get(CODE_ATTRIBUTE, String.class);
    }

    default String getDescription() {
        return get(DESCRIPTION_ATTRIBUTE, String.class);
    }

    default void setCode(String value) {
        set(CODE_ATTRIBUTE, value);
    }

    default void setDescription(String value) {
        set(DESCRIPTION_ATTRIBUTE, value);
    }

}
