package com.project.backend.validators;

import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.entities.Reservation;
import com.project.backend.exceptions.CustomExceptionMessages;
import com.project.backend.repositories.ReservationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UniqueReservationValidator {
    private final ReservationRepo reservationRepo;

    @Autowired
    public UniqueReservationValidator(ReservationRepo reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    public void validateUniqueReservation(ReservationDetailsDTO reservationDetailsDTO){
        List<Reservation> reservations = reservationRepo.findAll();

        for(Reservation reservation:reservations){
            if (reservation.getClient().getUser().getUsername().equals(reservationDetailsDTO.getUsername()) &&
                reservation.getDate().isAfter(LocalDate.now())){
                    throw new IllegalArgumentException(CustomExceptionMessages.YOU_CAN_NOT_HAVE_TWO_RESERVATIONS);
            }
        }
    }
}
