/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.test;

import org.cmdbuild.utils.ws3.inner.Ws3RpcRequest;
import static org.cmdbuild.utils.ws3.utils.Ws3RpcUtils.parseRpcRequest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Ws3RpcTest {

    @Test
    public void testRequestSerialization() {
        String payload = "{\"service\":\"MyService\",\"method\":\"myMethod\",\"id\":\"123\",\"params\":{\"par\":\"am\"},\"headers\":{\"hea\":\"der\"}}";

        Ws3RpcRequest request = parseRpcRequest(payload);
        assertEquals("my", request.getService());
        assertEquals("mymethod", request.getMethod());
        assertEquals("123", request.getId());
        assertEquals("am", request.getParam("par"));
        assertEquals("der", request.getHeader("hea"));
    }

}
