/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.inner;

import org.cmdbuild.utils.io.BigByteArray;

public interface DwgToDxfHelper {

    default byte[] dwgToDxf(byte[] dwg) {
        return dwgToDxf(new BigByteArray(dwg)).toByteArray();
    }

    BigByteArray dwgToDxf(BigByteArray dwg);
}
