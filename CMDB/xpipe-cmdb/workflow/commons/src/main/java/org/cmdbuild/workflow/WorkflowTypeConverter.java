package org.cmdbuild.workflow;

import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.workflow.model.Process;

//TODO duplicated from SharkTypeConverter; move up in workflow-core and remove SharkTypeConverter
public interface WorkflowTypeConverter {

    @Nullable
    Object cardValueToFlowValue(@Nullable Object value, Attribute attribute);

    @Nullable
    Object cardValueToFlowValue(@Nullable Object value, CardAttributeType attributeType);

    @Nullable
    <T> T rawValueToFlowValue(@Nullable Object defaultValue, Class<T> javaType);

    Map<String, Object> flowValuesToCardValues(Process classe, Map<String, Object> data);

    @Nullable
    Object flowValueToCardValue(Process classe, String key, @Nullable Object value);

    Map<String, Object> widgetValuesToFlowValues(Map<String, Object> varsAndWidgetData);

    <T> T defaultValueForFlowInitialization(Class<T> javaType);

    @Nullable
    Object inflateFlowValueToCardValue(Object value);

}
