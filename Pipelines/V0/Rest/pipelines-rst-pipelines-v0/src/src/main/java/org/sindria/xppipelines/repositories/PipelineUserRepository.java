package org.sindria.xppipelines.repositories;

import org.sindria.xppipelines.models.PipelineUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineUserRepository extends PagingAndSortingRepository<PipelineUser, Long> {

    @Query("SELECT t FROM PipelineUser t WHERE t.user_id = :userId")
    List<PipelineUser> findByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM PipelineUser t WHERE t.pipeline_id = :pipelineId")
    List<PipelineUser> findByPipelineId(@Param("pipelineId") Long policyId);
}