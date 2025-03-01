/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * <i>dms category</i> (a special type of {@link LookupType}) stuff, composed of
 * value items.
 *
 * @author afelice
 */
public class CmDmsCategorySchemaNode extends CmLookupSchemaNode {

    public CmDmsCategorySchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }
}
