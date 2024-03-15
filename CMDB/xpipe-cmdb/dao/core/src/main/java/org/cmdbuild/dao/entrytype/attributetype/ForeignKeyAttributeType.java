package org.cmdbuild.dao.entrytype.attributetype;

import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ForeignKeyAttributeType extends AbstractReferenceAttributeType {

    private final String targetClassName;

    public ForeignKeyAttributeType(String destinationClassName) {
        this.targetClassName = checkNotBlank(destinationClassName, "missing fk target class name");
    }

    public ForeignKeyAttributeType(EntryType entryType) {
        this(entryType.getName());
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    public String getForeignKeyDestinationClassName() {
        return targetClassName;
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.FOREIGNKEY;
    }

}
