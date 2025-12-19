package com.test.ntt.repositories;

import com.test.ntt.models.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(String number);

    @Query("SELECT a FROM Account a " +
            "WHERE a.status = true " +
            "AND a.idClient = :userId ")
    List<Account> getAccounts(@Param("userId") Long userId);

    List<Account> findByIdClient(Long idClient);


}
