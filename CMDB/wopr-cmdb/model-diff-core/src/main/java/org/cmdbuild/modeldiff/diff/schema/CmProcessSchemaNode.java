/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link Process} stuff, composed of {@link Attribute} items.
 *
 * @author afelice
 */
public class CmProcessSchemaNode extends CmSchemaItemDataNode {

    public CmProcessSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }
}
