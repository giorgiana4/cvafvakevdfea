package com.project.backend.repositories;

import com.project.backend.entities.Location;
import com.project.backend.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TariffRepo extends JpaRepository<Tariff, UUID> {
    List<Tariff> findAllByLocation(Location location);
}
