package com.project.backend.repositories;

import com.project.backend.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, UUID> {
    @Query(value = "SELECT r FROM Reservation r WHERE r.date = :date AND r.court.idCourt = :idCourt")
    List<Reservation> findByDateAndCourt(@Param("date") LocalDate date, @Param("idCourt") UUID idCourt);

    @Query(value = "SELECT r FROM Reservation r WHERE r.court.idCourt = :idCourt")
    List<Reservation> findByCourt(@Param("idCourt") UUID idCourt);
}
