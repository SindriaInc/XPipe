/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import com.google.common.base.Preconditions;

public class Ws3ResourceBeanWithInterface {

    private final Class iface;
    private final Object bean;

    public Ws3ResourceBeanWithInterface(Class iface, Object bean) {
        this.iface = Preconditions.checkNotNull(iface);
        this.bean = Preconditions.checkNotNull(bean);
    }

    public Class getIface() {
        return iface;
    }

    public Object getBean() {
        return bean;
    }

    @Override
    public String toString() {
        return "Ws3ResourceBeanWithInterface{" + "iface=" + iface + ", bean=" + bean + '}';
    }

}
