/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/License0s/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public interface DataSerializer<T> {

    CmMapUtils.FluentMap<String, Object> serialize(T data);
}
