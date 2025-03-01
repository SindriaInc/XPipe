/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static java.util.Collections.emptyList;
import java.util.List;
import org.cmdbuild.utils.ws3.api.Ws3WarningSource;

public enum Ws3DummyWarningSource implements Ws3WarningSource {
    INSTANCE;

    @Override
    public List<Object> getWarningJsonMessages() {
        return emptyList();
    }
}
