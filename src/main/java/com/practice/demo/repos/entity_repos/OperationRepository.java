package com.practice.demo.repos.entity_repos;

import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.db_views.OperationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query("select E from OperationView E where E.operationId = :operationId")
    OperationView findViewById(Long operationId);

    @Query(
        """
        SELECT OW from OperationView OW where OW.clientId = (
            SELECT C.id from Client C where C.username = :username
        )
        """
    )
    List<OperationView> fetchOperationViewsByUsername(String username);
}
