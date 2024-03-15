/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.function.StoredFunction;

public interface StoredFunctionRepository {

    List<StoredFunction> getAllFunctions();

    @Nullable
    StoredFunction getFunctionOrNull(String localname);

    default StoredFunction getFunctionByName(String name) {
        return checkNotNull(getFunctionOrNull(name), "function not found for name = %s", name);
    }

    @Nullable
    default StoredFunction getFunctionByIdOrNull(long id) {
        return getAllFunctions().stream().filter((f) -> f.getId() == id).collect(toOptional()).orElse(null);
    }

    default StoredFunction getFunctionById(long id) {
        return checkNotNull(getFunctionByIdOrNull(id), "function not found for id = %s", id);
    }

}
