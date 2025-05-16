/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lookup;

import java.util.Map;
import javax.annotation.Nullable;

public interface LookupConfig extends DmsCategoryConfig {

    final String LOOKUP_CONFIG_IS_DEFAULT = "cm_is_default",
            LOOKUP_CONFIG_ICON_TYPE = "cm_icon_type",
            LOOKUP_CONFIG_ICON_IMAGE = "cm_icon_image",
            LOOKUP_CONFIG_ICON_FONT = "cm_icon_font",
            LOOKUP_CONFIG_ICON_COLOR = "cm_icon_color",
            LOOKUP_CONFIG_TEXT_COLOR = "cm_text_color";

    @Nullable
    String getDmsModelClass();

    boolean isDefault();

    IconType getIconType();

    @Nullable
    String getTextColor();

    @Nullable
    String getIconImage();

    @Nullable
    String getIconFont();

    @Nullable
    String getIconColor();

    Map<String, String> asMap();

    @Nullable
    default String getConfig(String key) {
        return asMap().get(key);
    }
}
