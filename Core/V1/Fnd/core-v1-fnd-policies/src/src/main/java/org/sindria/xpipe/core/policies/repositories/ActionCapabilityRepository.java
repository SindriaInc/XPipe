package org.sindria.xpipe.core.policies.repositories;

import org.sindria.xpipe.core.policies.models.ActionCapability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ActionCapabilityRepository extends PagingAndSortingRepository<ActionCapability, Long> {

    @Query("SELECT t FROM ActionCapability t WHERE t.action_id = :actionId")
    List<ActionCapability> findByActionId(@Param("actionId") Long actionId);

    @Query("SELECT t FROM ActionCapability t WHERE t.capability_id = :capabilityId")
    List<ActionCapability> findByCapabilityId(@Param("capabilityId") Long capabilityId);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE action_capability", nativeQuery = true)
    void truncate();

    @Transactional
    @Modifying
    @Query(value = "SET foreign_key_checks = 1", nativeQuery = true)
    public void enableForeignCheck();

    @Transactional
    @Modifying
    @Query(value = "SET foreign_key_checks = 0", nativeQuery = true)
    public void disableForeignCheck();

}