package com.project.backend.repositories;

import com.project.backend.entities.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourtRepo extends JpaRepository<Court, UUID> {
}
