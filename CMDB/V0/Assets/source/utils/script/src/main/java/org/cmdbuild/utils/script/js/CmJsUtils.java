/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.js;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

public class CmJsUtils {

    public static ScriptEngine getJsScriptEngine() {
        return getJsScriptEngine(new String[]{});
    }

    public static ScriptEngine getJsScriptEngine(String... init) { //TODO improve performance with js precompilation, powered by graal vm; also: replace ScriptEngine api with graal vm Context api (?)
        try {
//            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
            ScriptEngine scriptEngine = checkNotNull(new ScriptEngineManager().getEngineByName("graal.js"), "js script engine not found");
            asList(init).forEach(rethrowConsumer(scriptEngine::eval));
            return scriptEngine;
        } catch (ScriptException ex) {
            throw runtime(ex);
        }
    }
}
