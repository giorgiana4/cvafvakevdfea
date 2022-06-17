package com.project.backend.dtos.builders;

import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.entities.Reservation;

import java.util.UUID;

public class ReservationBuilder {

    private  ReservationBuilder(){
    }

    public static ReservationDetailsDTO toReservationDetailsDTO(Reservation reservation,String username, UUID courtId){
        return new ReservationDetailsDTO(reservation.getIdReservation(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getTotalPrice(),
                username,
                courtId);
    }

    public static Reservation toEntity(ReservationDetailsDTO reservationDetailsDTO){
        return new Reservation(reservationDetailsDTO.getDate(),
                reservationDetailsDTO.getStartTime(),
                reservationDetailsDTO.getEndTime(),
                reservationDetailsDTO.getTotalPrice());
    }

}
