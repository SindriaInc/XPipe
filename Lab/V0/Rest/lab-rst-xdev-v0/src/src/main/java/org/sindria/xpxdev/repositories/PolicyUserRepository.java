package org.sindria.xpxdev.repositories;

import org.sindria.xpxdev.models.PolicyUser;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyUserRepository extends PagingAndSortingRepository<PolicyUser, Long> {

    @Query("SELECT t FROM PolicyUser t WHERE t.user_id = :userId")
    List<PolicyUser> findByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM PolicyUser t WHERE t.policy_id = :policyId")
    List<PolicyUser> findByPolicyId(@Param("policyId") Long policyId);
}
