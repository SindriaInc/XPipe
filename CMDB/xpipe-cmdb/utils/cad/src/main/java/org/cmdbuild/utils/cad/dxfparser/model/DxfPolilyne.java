/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import org.cmdbuild.utils.cad.model.CadPolyline;

public interface DxfPolilyne extends DxfEntity {

    CadPolyline getPolilyne();

    default boolean isPoint() {
        return getPolilyne().isPoint();
    }
}
