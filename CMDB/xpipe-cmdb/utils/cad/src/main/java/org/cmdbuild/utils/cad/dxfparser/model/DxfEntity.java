/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import javax.annotation.Nullable;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.cad.model.CadPolyline;

public interface DxfEntity {

    @Nullable
    String getLayer();

    String getType();

    DxfExtendedData getXdata();

    CadPolyline getPerimeter();

    boolean isClosedPerimeter();

    DxfEntity withLayer(String layer);

    DxfEntity withOffset(CadPoint offset);

    DxfEntity withRotation(double rotationAngle, double originX, double originY);

    DxfEntity withScale(double scaleX, double scaleY, double originX, double originY);

    default boolean hasXdata() {
        return getXdata().isNotEmpty();
    }

}
