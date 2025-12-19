package com.test.ntt.repositories;

import com.test.ntt.models.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {


    Optional<Movement> findTopByAccountIdOrderByIdDesc(Long accountId);

    @Query("SELECT m FROM Movement m " +
            "JOIN FETCH m.movementType " + // Trae el catálogo del tipo de movimiento
            "WHERE m.account.id = :accountId " +
            "AND m.createdAt BETWEEN :start AND :end")
    List<Movement> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    }
