/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.beans;

import java.util.Map;

public interface DatabaseEntryDefinition {

	DatabaseEntryDefinition set(String key, Object value);

	DatabaseEntryDefinition set(Iterable<? extends Map.Entry<String, ? extends Object>> keysAndValues);

	DatabaseEntryDefinition setUser(String user);

	// TODO check if this is really needed
	DatabaseRecord save();

}
