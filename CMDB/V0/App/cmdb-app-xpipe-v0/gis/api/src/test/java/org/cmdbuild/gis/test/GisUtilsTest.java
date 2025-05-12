/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.test;

import org.cmdbuild.gis.model.PointImpl;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GisUtilsTest {

    @Test
    public void testGeometrySerialization() {
        assertEquals("POINT(13.2245340172543 46.1082230000902)", cmGeometryToPostgisSql(new PointImpl(13.224534017254335, 46.10822300009022)));
        assertEquals("POINT(13.2244618053538 46.1081516889815)", cmGeometryToPostgisSql(new PointImpl(13.224461805353847, 46.108151688981515)));
        assertEquals("POINT(13.2244474822752 46.1081811144386)", cmGeometryToPostgisSql(new PointImpl(13.224447482275199, 46.108181114438615)));
        assertEquals("POINT(13.2244331589926 46.1082105402186)", cmGeometryToPostgisSql(new PointImpl(13.224433158992625, 46.108210540218614)));
        assertEquals("POINT(13.2244188518932 46.108239933737)", cmGeometryToPostgisSql(new PointImpl(13.224418851893196, 46.10823993373704)));
    }

}
