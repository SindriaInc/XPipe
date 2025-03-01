/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.List;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public interface DomainSync {

    Domain read(String domainName);

    List<Domain> readAll();

    void insert(DomainDefinition domainDefinition);

    void update(DomainDefinition domainDefinition);

    void deactivate(Domain domain);

    void remove(String domainName);

    CmMapUtils.FluentMap<String, Object> serializeDomainProps(Domain domain);
}
