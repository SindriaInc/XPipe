package org.sindria.xppipelines.repositories;

import org.sindria.xppipelines.models.Pipeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineRepository extends PagingAndSortingRepository<Pipeline, Long> {

    @Query("SELECT t FROM Pipeline t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    Page<Pipeline> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}
