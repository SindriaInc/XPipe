/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.beans;

import java.util.Map;

public interface CardDefinition extends DatabaseEntryDefinition {

	@Override
	CardDefinition set(String key, Object value);

	@Override
	CardDefinition set(Iterable<? extends Map.Entry<String, ? extends Object>> keysAndValues);

	@Override
	CardDefinition setUser(String user);

	CardDefinition setCode(Object value);

	CardDefinition setDescription(Object value);

	CardDefinition setCurrentId(Long currentId);

	@Override
	Card save();

}
