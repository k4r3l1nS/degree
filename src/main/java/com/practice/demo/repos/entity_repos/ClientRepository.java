package com.practice.demo.repos.entity_repos;

import com.practice.demo.models.entities.Client;
import com.practice.demo.models.db_views.ClientView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    boolean existsByFirstNameAndLastNameAndBirthDate(String firstName, String lastName, LocalDate birthDate);

    @Query("select E from ClientView E where E.id=?1")
    ClientView findClientById(Long id);

    Optional<Client> findByUsername(String username);
}
