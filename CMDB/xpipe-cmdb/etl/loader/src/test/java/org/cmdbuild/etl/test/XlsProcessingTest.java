/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import java.util.List;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class XlsProcessingTest {

    @Test
    public void testXlsWithFormulas() {
        List<List<Object>> records = getRecordsFromXlsFile(newDataSource(getClass().getResourceAsStream("/org/cmdbuild/etl/test/file_with_formulas.xlsx"), null, "file_with_formulas.xlsx"));

        assertEquals(3, records.size());

        assertEquals(15l, records.get(0).get(0));
        assertEquals(35l, records.get(0).get(2));
        assertEquals(32l, records.get(1).get(2));
        assertEquals(3l, records.get(2).get(2));
    }

}
