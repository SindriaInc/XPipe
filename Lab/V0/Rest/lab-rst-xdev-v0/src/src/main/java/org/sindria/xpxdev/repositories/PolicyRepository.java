package org.sindria.xpxdev.repositories;

import org.sindria.xpxdev.models.Policy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends PagingAndSortingRepository<Policy, Long> {

    @Query("SELECT t FROM Policy t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Policy> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

}
