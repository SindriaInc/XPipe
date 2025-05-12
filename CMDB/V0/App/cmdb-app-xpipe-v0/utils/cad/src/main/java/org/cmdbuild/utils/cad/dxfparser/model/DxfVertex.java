/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import org.cmdbuild.utils.cad.model.CadPoint;

public interface DxfVertex {

    double getX();

    double getY();

    double getZ();

    DxfVertex withOffset(CadPoint offset);

    DxfVertex withRotation(Double rotationAngle, double originX, double originY);

    DxfVertex withScale(double scaleX, double scaleY, double originX, double originY);

}
