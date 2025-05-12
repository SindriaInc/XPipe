package org.sindria.xpipe.core.policies.repositories;

import org.sindria.xpipe.core.policies.models.Type;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TypeRepository extends PagingAndSortingRepository<Type, Long> {

    @Query("SELECT t FROM Type t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Type> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE types", nativeQuery = true)
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
