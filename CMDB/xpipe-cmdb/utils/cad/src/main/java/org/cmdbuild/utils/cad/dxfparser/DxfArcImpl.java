/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.PI;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.cad.CadGeometryUtils.normalizeAngle;
import org.cmdbuild.utils.cad.dxfparser.model.DxfArc;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.model.CadPoint;

public class DxfArcImpl implements DxfArc {

    private final CadPoint center;
    private final double startAngle;
    private final double endAngle;
    private final double radius;
    private final String layer;
    private final DxfExtendedData xdata;

    public DxfArcImpl(CadPoint center, Double startAngle, Double endAngle, Double radius, @Nullable String layer, DxfExtendedData xdata) {
        this.center = checkNotNull(center);
        this.startAngle = normalizeAngle(startAngle);
        this.endAngle = normalizeAngle(endAngle);
        this.radius = radius;
        this.layer = layer;
        this.xdata = new DxfExtendedDataImpl(xdata);
    }

    @Override
    public CadPoint getCenter() {
        return center;
    }

    @Override
    public double getStartAngle() {
        return startAngle;
    }

    @Override
    public double getEndAngle() {
        return endAngle;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Nullable
    @Override
    public String getLayer() {
        return layer;
    }

    @Override
    public String getType() {
        return "ARC";
    }

    @Override
    public DxfExtendedData getXdata() {
        return xdata;
    }

    @Override
    public String toString() {
        return "DxfArc{" + "center=" + center + ", startAngle=" + startAngle + ", endAngle=" + endAngle + ", radius=" + radius + '}';
    }

    @Override
    public DxfEntity withLayer(String layer) {
        return new DxfArcImpl(center, startAngle, endAngle, radius, layer, xdata);
    }

    @Override
    public DxfEntity withOffset(CadPoint offset) {
        return offset.isZero() ? this : new DxfArcImpl(center.addOffset(offset), startAngle, endAngle, radius, layer, xdata);
    }

    @Override
    public DxfEntity withRotation(double rotationAngle, double originX, double originY) {
        return rotationAngle == 0 ? this : new DxfArcImpl(new CadPoint(originX + (center.getX() - originX) * Math.cos(rotationAngle) - (center.getY() - originY) * Math.sin(rotationAngle), originY + (center.getX() - originX) * Math.sin(rotationAngle) + (center.getY() - originY) * Math.cos(rotationAngle)), startAngle - (rotationAngle / PI * 180), endAngle - (rotationAngle / PI * 180), radius, layer, xdata);
    }

    @Override
    public DxfEntity withScale(double scaleX, double scaleY, double originX, double originY) {
        return scaleX == 1 && scaleY == 1 ? this : new DxfArcImpl(center.scale(scaleX, scaleY), startAngle, endAngle, radius * scaleX, layer, xdata);
    }

}
