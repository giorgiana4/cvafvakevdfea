package com.project.backend.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class ReservationDetailsDTO extends RepresentationModel<ReservationDetailsDTO> {
    private UUID idReservation;
    private LocalDate date;
    private int startTime;
    private int endTime;
    private double totalPrice;
    private String username;
    private UUID courtId;

    public ReservationDetailsDTO() {
    }

    public ReservationDetailsDTO(UUID idReservation, LocalDate date, int startTime, int endTime,
                                 double totalPrice, String username, UUID courtId) {
        this.idReservation=idReservation;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice=totalPrice;
        this.username=username;
        this.courtId=courtId;
    }

    public ReservationDetailsDTO(UUID idReservation, String username, UUID courtId) {
        this.idReservation = idReservation;
        this.username = username;
    }

    public ReservationDetailsDTO(LocalDate date, UUID courtId) {
        this.date = date;
        this.courtId=courtId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public UUID getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(UUID idReservation) {
        this.idReservation = idReservation;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getCourtId() {
        return courtId;
    }

    public void setCourtId(UUID courtId) {
        this.courtId = courtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDetailsDTO that = (ReservationDetailsDTO) o;
        return Double.compare(that.totalPrice, totalPrice) == 0
                && Objects.equals(idReservation, that.idReservation)
                && Objects.equals(date, that.date)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(username,that.username)
                && Objects.equals(courtId,that.courtId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation, date, startTime, endTime, totalPrice,username,courtId);
    }
}
