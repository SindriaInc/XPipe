package org.sindria.xpipe.core.policies.repositories;

import org.sindria.xpipe.core.policies.models.Action;
import org.sindria.xpipe.core.policies.models.PolicyUser;
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
public interface ActionRepository extends PagingAndSortingRepository<Action, Long> {

    @Query("SELECT t FROM Action t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Action> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT t FROM Action t WHERE t.name = :name")
    List<Action> findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE actions", nativeQuery = true)
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
