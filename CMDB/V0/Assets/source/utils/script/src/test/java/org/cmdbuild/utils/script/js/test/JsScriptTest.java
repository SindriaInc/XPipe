/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.js.test;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.cmdbuild.utils.script.js.CmJsUtils.getJsScriptEngine;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsScriptTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    @Ignore // js script engine is NOT thread safe 
    public void testJsScriptEngineThreadSafety() throws InterruptedException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        List<Thread> threads = list();
        List<Exception> errors = synchronizedList(list());
        for (int i = 0; i < 10; i++) {
            int value = i;
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        int count = 100;
//                        synchronized (engine) {// js script engine is NOT thread safe !!!
                        Object res = engine.eval(format("var myVar=%s; var i; for (i = 0; i < %s; i++) {  myVar = myVar + 1; }; myVar;", value, count));
                        assertEquals(value + count, toInt(res));
//                        }
                    } catch (Exception ex) {
                        logger.error("error", ex);
                        errors.add(ex);
                    }
                }
            }));
        }
        threads.forEach(Thread::start);
        threads.forEach(rethrowConsumer(Thread::join));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testJsScriptEngine1() throws ScriptException {
        ScriptEngine engine = getJsScriptEngine();
        engine.eval("var hello = \"Hello\";");
        engine.eval("var world = \"Workd!\";");
        engine.eval("space = ' ';");
        engine.eval("result = hello+space+world;");
        assertEquals("Hello Workd!", engine.get("result"));
        assertEquals("Hello Workd!", engine.eval("result"));
    }

    @Test
    public void testJsScriptEngine2() throws ScriptException {
        ScriptEngine engine = getJsScriptEngine();
        engine.eval("var hello = \"Hello\"");
        engine.eval("var world = \"Workd!\"");
        engine.eval("space = ' '");
        engine.eval("result = hello+space+world");
        assertEquals("Hello Workd!", engine.get("result"));
        assertEquals("Hello Workd!", engine.eval("result"));
    }

    @Test 
    public void testJsScriptEngine3() throws ScriptException {
        ScriptEngine engine = getJsScriptEngine();
        engine.eval("var hello = \"Hello\";");
        engine.eval("const world = \"Workd!\";");
        engine.eval("let space = ' ';");
        engine.eval("result = hello+space+world;");
        assertEquals("Hello Workd!", engine.get("result"));
        assertEquals("Hello Workd!", engine.eval("result"));
    }

    @Test
    public void testJsScriptEngineInit() throws ScriptException {
        ScriptEngine engine = getJsScriptEngine("my_env = 'something';");
        assertEquals("something", engine.get("my_env"));
        assertEquals("something", engine.eval("my_env"));
    }
}
