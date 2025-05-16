/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * <i>dms model</i> (a special type of {@link Classe}) stuff, composed of
 * {@link Attribute} items.
 *
 * @author afelice
 */
public class CmDmsModelSchemaNode extends CmClasseSchemaNode {

    public CmDmsModelSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }
}
