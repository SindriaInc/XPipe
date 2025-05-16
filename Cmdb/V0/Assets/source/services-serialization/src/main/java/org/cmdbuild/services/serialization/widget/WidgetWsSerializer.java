/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.widget;

import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.date.CmDateUtils.isDate;
import static org.cmdbuild.utils.date.CmDateUtils.isDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.isTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author afelice
 */
public class WidgetWsSerializer {

    public CmMapUtils.FluentMap<String, Object> serialize(List<WidgetSerializationData> widgetsData) {
        FluentMap<String, Object> result = map();

        result.put("_widgets", widgetsData.stream().map(w -> serializeWidget(w)).collect(toList()));

        return result;
    }

    /**
     * Same as
     * {@link ClassSerializationHelper#serializeWidget(org.cmdbuild.widget.model.WidgetData, java.lang.String)}
     *
     * @param data
     * @return
     */
    private static FluentMap<String, Object> serializeWidget(WidgetSerializationData data) {
        FluentMap<String, Object> result = map();

        result.put("_id", data.widgetData.getId(),
                "_label", data.widgetData.getLabel(),
                "_type", data.widgetData.getType(),
                "_active", data.widgetData.isActive(),
                "_required", data.widgetData.isRequired(),
                "_alwaysenabled", data.widgetData.isAlwaysEnabled(),
                "_hideincreation", data.widgetData.hideInCreation(),
                "_hideinedit", data.widgetData.hideInEdit(),
                "_inline", data.widgetData.isInline(),
                "_inlineclosed", data.widgetData.isInlineClosed(),
                "_inlinebefore", data.widgetData.getInlineBefore(),
                "_inlineafter", data.widgetData.getInlineAfter(),
                "_output", data.widgetData.getOutputParameterOrNull(),
                "_label_translation", data.descriptionTranslation
        );
        result.with(map(data.widgetData.getExtendedData()).mapValues(WidgetWsSerializer::serializeExtendedValue));

        return result;
    }

    /**
     * Same as
     * {@link ClassSerializationHelper#serializeWidgetExtendedValue(java.lang.Object) )}
     *
     * @param value
     * @return
     */
    private static Object serializeExtendedValue(@Nullable Object value) {
        if (value == null || isPrimitiveOrWrapper(value)) {
            return value;
        } else if (isDateTime(value)) {
            return toIsoDateTimeUtc(value);
        } else if (isDate(value)) {
            return toIsoDate(value);
        } else if (isTime(value)) {
            return toIsoTime(value);
        } else {
            return value;
        }
    }

}
