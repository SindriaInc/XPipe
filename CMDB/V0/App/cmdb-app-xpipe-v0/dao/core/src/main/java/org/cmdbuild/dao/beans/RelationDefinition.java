/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

public interface RelationDefinition extends DatabaseEntryDefinition {

	RelationDefinition setSourceCard(Card card);

	RelationDefinition setTargetCard(Card card);

	@Override
	RelationDefinition set(String key, Object value);

	CMRelation create();

	CMRelation update();

	void delete();

	@Override
	CMRelation save();

}
