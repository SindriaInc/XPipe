/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper.WsDomainData;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class DomainSyncImpl implements DomainSync {

    private static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    private final UserDomainService domainService;
    private final DomainSerializationHelper domainHelper;
    private final DomainRepository domainRepository;

    public DomainSyncImpl(UserDomainService domainService, DomainSerializationHelper domainSerializationHelper,
            DomainRepository domainRepository) {
        this.domainService = checkNotNull(domainService);
        this.domainHelper = checkNotNull(domainSerializationHelper);
        this.domainRepository = checkNotNull(domainRepository);
    }

    @Override
    public Domain read(String domainName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Requires that current user has <i>service list permission</i>
     * (<code>PS_SERVICE</code>, <code>CP_LIST</code>).
     *
     * @return
     */
    @Override
    public List<Domain> readAll() {
        return domainService.getUserDomains();
    }

    @Override
    public void insert(DomainDefinition domainDefinition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(DomainDefinition domainDefinition) {
        domainRepository.updateDomain(domainDefinition);
    }

    @Override
    public void deactivate(Domain domain) {
        Domain deactivatedDomain = DomainImpl.copyOf(domain)
                .withMetadata(
                        DomainMetadataImpl.copyOf(domain.getMetadata())
                                .withIsActive(false)
                                .build()
                ).build();
        update(domain.getName(), buildDomainData(deactivatedDomain));
    }

    @Override
    public void remove(String domainName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CmMapUtils.FluentMap<String, Object> serializeDomainProps(Domain domain) {
        return domainHelper.serializeDetailedDomain(domain);
    }

    private Domain update(String domainName, WsDomainData domainData) {
        // See DomainWs.update()
        return domainRepository.updateDomain(domainHelper.toDomainDefinition(domainData).build());
    }

    private WsDomainData buildDomainData(Domain domain) {
        return buildDomainData(domainHelper.serializeDetailedDomain(domain));
    }

    /**
     * {@link Domain} <i>metadata</i> is exploded in CMDBuild serialization and
     * needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param domainCmdbSerialization
     * @return
     */
    private WsDomainData buildDomainData(Map<String, Object> domainCmdbSerialization) {
        // Reconstructs metadata from Domain serialization        
        return getSystemObjectMapper().convertValue(domainCmdbSerialization, DomainSerializationHelper.WsDomainData.class);
    }

    private ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }
}
