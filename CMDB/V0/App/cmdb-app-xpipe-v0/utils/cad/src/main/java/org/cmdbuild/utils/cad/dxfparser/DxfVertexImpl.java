/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;

public class DxfVertexImpl implements DxfVertex {

    private double x;
    private double y;
    private double z;

    public DxfVertexImpl() {
    }

    public DxfVertexImpl(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "DxfVertex{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public DxfVertex withOffset(CadPoint offset) {
        return offset.isZero() ? this : new DxfVertexImpl(x + offset.getX(), y + offset.getY());
    }

    @Override
    public DxfVertex withRotation(Double rotationAngle, double originX, double originY) {
        return rotationAngle == 0 ? this : new DxfVertexImpl(originX + (x - originX) * Math.cos(rotationAngle) - (y - originY) * Math.sin(rotationAngle),
                originY + (x - originX) * Math.sin(rotationAngle) + (y - originY) * Math.cos(rotationAngle));
    }

    @Override
    public DxfVertex withScale(double scaleX, double scaleY, double originX, double originY) {
        return scaleX == 1 && scaleY == 1 ? this : new DxfVertexImpl(x * scaleX, y * scaleY);
    }

}
