/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNoneBlank;

public class EmailTemplateBindingsImpl implements EmailTemplateBindings {

    private final Set<String> clientBindings, serverBindings;

    public EmailTemplateBindingsImpl(Collection<String> clientBindings, Collection<String> serverBindings) {
        this.clientBindings = ImmutableSet.copyOf(checkNoneBlank(clientBindings));
        this.serverBindings = ImmutableSet.copyOf(checkNoneBlank(serverBindings));
    }

    @Override
    public Collection<String> getClientBindings() {
        return clientBindings;
    }

    @Override
    public Collection<String> getServerBindings() {
        return serverBindings;
    }

}
