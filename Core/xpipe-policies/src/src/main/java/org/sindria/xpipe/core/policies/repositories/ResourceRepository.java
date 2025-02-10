package org.sindria.xpipe.core.policies.repositories;

import org.sindria.xpipe.core.policies.models.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface ResourceRepository extends PagingAndSortingRepository<Resource, Long> {

    @Query("SELECT t FROM Resource t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Resource> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE resources", nativeQuery = true)
    void truncate();

}
