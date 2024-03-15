/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.dxfparser.model.DxfPolilyne;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.cmdbuild.utils.cad.model.CadPolyline;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DxfPolylineImpl implements DxfPolilyne {

    private boolean isClosedPerimeter = false;
    private String layer;
    private final List<DxfVertex> vertexes = CmCollectionUtils.list();
    private final DxfExtendedDataImpl xdata;

    public DxfPolylineImpl() {
        xdata = new DxfExtendedDataImpl();
    }

    public DxfPolylineImpl(List<DxfVertex> vertexes, DxfExtendedData xdata, @Nullable String layer, boolean isClosedPerimeter) {
        this.layer = layer;
        this.vertexes.addAll(vertexes);
        this.xdata = new DxfExtendedDataImpl(xdata);
        this.isClosedPerimeter = isClosedPerimeter;
    }

    public void addVertex(DxfVertex vertex) {
        vertexes.add(vertex);
    }

    @Override
    public CadPolyline getPolilyne() {
        return new CadPolyline(list(vertexes).map(v -> point(v.getX(), v.getY())));
    }

    @Override
    public String getType() {
        return "POLYLINE";
    }

    @Nullable
    @Override
    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    @Override
    public DxfExtendedDataImpl getXdata() {
        return xdata;
    }

    @Override
    public boolean isClosedPerimeter() {
        return isClosedPerimeter;
    }

    public void setClosedPerimeter(boolean isClosedPerimeter) {
        this.isClosedPerimeter = isClosedPerimeter;
    }

    @Override
    public CadPolyline getPerimeter() {
        return getPolilyne();
    }

    @Override
    public DxfEntity withLayer(String layer) {
        return new DxfPolylineImpl(vertexes, xdata, layer, isClosedPerimeter);
    }

    @Override
    public DxfEntity withOffset(CadPoint offset) {
        return offset.isZero() ? this : new DxfPolylineImpl(list(vertexes).map(v -> v.withOffset(offset)), xdata, layer, isClosedPerimeter);
    }

    @Override
    public DxfEntity withRotation(double rotationAngle, double originX, double originY) {
        return rotationAngle == 0 ? this : new DxfPolylineImpl(list(vertexes).map(v -> v.withRotation(rotationAngle, originX, originY)), xdata, layer, isClosedPerimeter);
    }

    @Override
    public DxfEntity withScale(double scaleX, double scaleY, double originX, double originY) {
        return scaleX == 1 && scaleY == 1 ? this : new DxfPolylineImpl(list(vertexes).map(v -> v.withScale(scaleX, scaleY, originX, originY)), xdata, layer, isClosedPerimeter);
    }

    @Override
    public String toString() {
        return "DxfPolylineImpl{" + "closed=" + isClosedPerimeter + ", layer=" + layer + ", vertexes=" + vertexes.size() + '}';
    }

}
