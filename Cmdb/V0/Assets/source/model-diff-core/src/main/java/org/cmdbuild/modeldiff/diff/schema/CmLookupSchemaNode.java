/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link LookupType} stuff, composed of {@link LookupValue} items.
 *
 * @author afelice
 */
public class CmLookupSchemaNode extends CmSchemaItemDataNode {

    public CmLookupSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }

    /**
     *
     * @return name of parent; <code>null</code> if none.
     */
    public String getParent() {
        return toStringOrNull(getModelObj().getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION));
    }

}
