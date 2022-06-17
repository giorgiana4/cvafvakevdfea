package com.project.backend.repositories;

import com.project.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepo extends JpaRepository<Client, UUID> {
    @Query(value = "SELECT c FROM Client c WHERE c.user.username = :username ")
    Optional<Client> findByUsername(@Param("username") String username);
}