package org.sindria.xpxdev.repositories;

import org.sindria.xpxdev.models.Type;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends PagingAndSortingRepository<Type, Long> {

    @Query("SELECT t FROM Type t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Type> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

}
