/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.plugin;

import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.getEnumParamsFromType;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface used by all plugins (dms, waterway, ecc.)
 *
 * @author afelice
 */
public interface PluginService {

    static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String getName();

    default boolean isDummy() {
        return false;
    }

    default public Map<String, Object> getConfigs(String language) {
        return emptyMap();
    }

    default Map<String, Object> getConfigs() {
        return getConfigs(null);
    }

    default Map<String, Object> getConfigs(Class<?> classConfiguration, Object configuration, String language) {
        List<Field> declaredFields = list(classConfiguration.getDeclaredFields()).filter(d -> d.getAnnotation(ConfigValue.class) != null);
        declaredFields.forEach(f -> f.setAccessible(true));
        Map<String, Object> configs = declaredFields.stream().collect(toMap(this::getConfigKey, safe(f -> f.get(configuration), null)));
        return map(configs).with("_model", map("attributes", list(declaredFields).map(this::generateAttributeConfig)));
    }

    private Map<String, String> generateAttributeConfig(Field field) {
        String description = StringUtils.capitalize(field.getAnnotation(ConfigValue.class).key()).replace(".", " ");
        return map(
                "_id", getConfigKey(field),
                "name", field.getAnnotation(ConfigValue.class).key(),
                "description", description,
                "_description_translation", description,
                "type", field.getType().getSimpleName().toLowerCase(),
                "options", field.isEnumConstant() ? getEnumParamsFromType(field.getType()) : null
        );
    }

    private String getConfigKey(Field field) {
        return format("%s.%s", field.getDeclaringClass().getAnnotation(ConfigComponent.class).value(), field.getAnnotation(ConfigValue.class).key());
    }
}
