/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.attribute.file;

import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.serialization.CardAttributeSerializerAdapter;
import org.cmdbuild.services.serialization.SerializationPrefixMode;
import org.cmdbuild.services.serialization.widget.WidgetSerializationData;
import org.cmdbuild.services.serialization.widget.WidgetWsSerializer;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.services.serialization.SerializationPrefixMode.SPM_ANONYMOUS_SERIALIZATION;

/**
 * Used in AttahcmentWsHelper, with serialization of
 * {@link CardAttributeFileWsSerializer} plus:
 * <ul>
 * <li>document (hash) <code>_&lt;attributeName&gt;__id</code>;
 * <li>related card (id) in <code>_&lt;attributeName&gt;__card</code>;
 * <li>(possibly present) widgets.
 * </ul>
 *
 * @author afelice
 */
public class CardAttributeFileFullWsSerializer extends CardAttributeFileWsSerializer {

    private final WidgetWsSerializer widgetSerializer = new WidgetWsSerializer();

    /**
     * Used in AttachmentWsHelper, with serialization of
     * {@link CardAttributeFileWsSerializer} plus:
     * <ul>
     * <li>document (hash) <code>_&lt;attributeName&gt;__id</code>;
     * <li>related card (id) in <code>_&lt;attributeName&gt;__card</code>;
     * <li>(possibly present) widgets.
     * </ul>
     *
     * @param attributeSerializer Handle recursion when serializing Card
     * attributes.
     */
    public CardAttributeFileFullWsSerializer(CardAttributeSerializerAdapter attributeSerializer) {
        super(attributeSerializer);
    }

    @Override
    public FluentMap<String, Object> serialize(CardAttributeFileSerializationData data) {
        FluentMap<String, Object> wsSpecificResult = map();
        wsSpecificResult
                .with(serializeWidgets(data.widgets));

        String attributeName = data.attributeName;

        // Prefix with _<attribute_name>_
        SerializationPrefixMode prefixMode = data.getPrefixMode();
        switch (prefixMode) {
            case SPM_ANONYMOUS_SERIALIZATION -> {
            }
            case SPM_PACKED -> {
                // Prefix all keys with _<attributeName>_
                wsSpecificResult = underscorePrefixKeysWith(attributeName, wsSpecificResult);
            }
            case SPM_JSON -> {
                wsSpecificResult = asMapStr(attributeName, wsSpecificResult);
            }
            default ->
                throw new UnsupportedOperationException(format("unhandled prefix mode =<%s>", prefixMode));
        }


        return super.serialize(data).with(wsSpecificResult);
    }

    @Override
    protected CmMapUtils.FluentMap<String, Object> serializeDocument(CardAttributeFileSerializationData data) {
        return super.serializeDocument(data).with(
                "_id", data.document.getDocumentId()
        );
    }

    private CmMapUtils.FluentMap<String, Object> serializeWidgets(List<WidgetSerializationData> widgetsData) {
        if (isNullOrEmpty(widgetsData)) {
            return map();
        }

        return widgetSerializer.serialize(widgetsData);
    }

}
