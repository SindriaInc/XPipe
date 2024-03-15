/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.utils.ws3.test;

import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.ws3.api.Ws3ResourceRepository;
import org.cmdbuild.utils.ws3.inner.Ws3ResourceBeanWithInterface;

public class Ws3ResourceRepositoryTestImpl implements Ws3ResourceRepository{
    
    private final List<Ws3ResourceBeanWithInterface> resources;

    public Ws3ResourceRepositoryTestImpl(Object... resources) {
        this.resources = list(resources).map(r->new Ws3ResourceBeanWithInterface(r.getClass(), r));
    }

    @Override
    public Iterable<Ws3ResourceBeanWithInterface> getResources() {
        return resources;
    }

}
