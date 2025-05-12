package org.sindria.xpipe.core.policies.repositories;

import org.sindria.xpipe.core.policies.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query("SELECT t FROM User t WHERE t.uuid LIKE CONCAT('%',:searchTerm, '%')")
    Page<User> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

}
