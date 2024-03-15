/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class LockScopeUtils {

    public static LockScope parseLockScope(String scope) {
        return parseEnum(scope, LockScope.class);
    }

    public static String serializeLockScope(LockScope scope) {
        return scope.name().toLowerCase().replaceFirst("ls_", "");
    }

}
