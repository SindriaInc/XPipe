package org.cmdbuild.dao.entrytype.attributetype;

public enum FormulaAttributeType implements CardAttributeType<Object> {

	INSTANCE;

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.FORMULA;
	}
}
