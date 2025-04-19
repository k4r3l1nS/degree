package com.practice.demo.repos.entity_repos;

import com.practice.demo.models.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

     Account findByName(String accountName);

     Account findByNameAndClientId(String accountName, Long clientId);

     boolean existsByName(String accountName);

     @Query("select E from Account E where E.isActive = true and E.accountKind = 1")
     List<Account> findActiveAccumulationAccounts();

     List<Account> findByClientId(Long clientId);
}
