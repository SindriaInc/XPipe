package org.sindria.xpipe.academy.password.repository;

import org.sindria.xpipe.academy.password.model.Credential;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasswordRepository extends PagingAndSortingRepository<Credential, Long> {


    //test query
    @Query("SELECT t FROM Credential t WHERE t.name = :name")
    List<Credential> findCredentialByName(@Param("credentialId") Long credentialId);


}
