/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session;

public interface TransientSessionHelper extends AutoCloseable {

    void restorePreviousSession();

    @Override
    public default void close() {
        restorePreviousSession();
    }

}
