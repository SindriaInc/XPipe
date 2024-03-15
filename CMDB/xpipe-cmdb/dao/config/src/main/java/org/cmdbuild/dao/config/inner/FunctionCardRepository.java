/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import java.util.Map;
import org.cmdbuild.dao.sql.utils.SqlFunction;

public interface FunctionCardRepository {

    Map<String, SqlFunction> getFunctions(String category);

    void update(String category, SqlFunction function);

}
