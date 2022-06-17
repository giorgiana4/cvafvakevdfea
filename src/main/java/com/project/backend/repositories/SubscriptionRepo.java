package com.project.backend.repositories;

import com.project.backend.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription, UUID> {
    @Query(value = "SELECT s FROM Subscription s WHERE s.dayOfWeek = :dayOfWeek AND s.court.idCourt = :idCourt ")
    List<Subscription> findByDayOfWeekAndCourt(@Param("dayOfWeek") String dayOfWeek, @Param("idCourt") UUID idCourt);

}
