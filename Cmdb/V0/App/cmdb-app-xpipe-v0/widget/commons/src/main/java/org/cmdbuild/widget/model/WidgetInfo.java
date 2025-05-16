package org.cmdbuild.widget.model;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

public interface WidgetInfo {

    String getType();

    boolean isActive();

    String getId();

    String getLabel();

    default boolean isCustomComponent() {
        return isNumber(getId());//TODO check this !! is id? is type?
    }

}
