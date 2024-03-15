/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.test;

import java.util.Date;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.deserializeValue;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.serializeValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlowDataSerializationTest {

    @Test
    public void testDateSerialization() {
        Date date = toJavaDate("2019-03-24T12:33:14Z");
        String value = (String) serializeValue(date);
        assertEquals("2019-03-24T12:33:14Z", value);

        RiverVariableInfo variable = mock(RiverVariableInfo.class);
        when(variable.getJavaType()).thenReturn(Date.class);

        Date newDate = (Date) deserializeValue(value, variable);
        assertEquals(date.getTime(), newDate.getTime());
    }

    @Test
    public void testNullDateSerialization() {
        Object value = serializeValue(null);
        assertNull(value);

        RiverVariableInfo variable = mock(RiverVariableInfo.class);
        when(variable.getJavaType()).thenReturn(Date.class);

        Date newDate = (Date) deserializeValue(value, variable);
        assertNull(newDate);
    }

}
