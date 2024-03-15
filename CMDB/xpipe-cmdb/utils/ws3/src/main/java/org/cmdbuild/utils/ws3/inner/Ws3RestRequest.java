/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public interface Ws3RestRequest extends Ws3Request {

    String getResourceUri();

    Ws3RestRequest withParams(Map<String, String> otherParams);

    @Nullable
    List<String> getParams(String key);

}
