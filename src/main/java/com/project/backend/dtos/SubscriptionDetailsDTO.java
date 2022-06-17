package com.project.backend.dtos;


import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class SubscriptionDetailsDTO extends RepresentationModel<SubscriptionDetailsDTO> {
    private UUID idSubscription;
    private LocalDate startDate;
    private int startTime;
    private int endTime;
    private String dayOfWeek;
    private double totalPrice;
    private String username;
    private UUID courtId;

    public SubscriptionDetailsDTO() {
    }

    public SubscriptionDetailsDTO(UUID idSubscription, LocalDate startDate, int startTime, int endTime, String dayOfWeek, double totalPrice, String username, UUID courtId) {
        this.idSubscription = idSubscription;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.totalPrice = totalPrice;
        this.username = username;
        this.courtId = courtId;
    }

    public SubscriptionDetailsDTO(LocalDate startDate, String dayOfWeek, UUID courtId) {
        this.startDate = startDate;
        this.dayOfWeek = dayOfWeek;
        this.courtId = courtId;
    }

    public UUID getIdSubscription() {
        return idSubscription;
    }

    public void setIdSubscription(UUID idSubscription) {
        this.idSubscription = idSubscription;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
        SubscriptionDetailsDTO that = (SubscriptionDetailsDTO) o;
        return Objects.equals(dayOfWeek, that.dayOfWeek)
                && Double.compare(that.totalPrice, totalPrice) == 0
                && Objects.equals(idSubscription, that.idSubscription)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(username, that.username)
                && Objects.equals(courtId, that.courtId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSubscription, startDate, startTime, endTime, dayOfWeek, totalPrice, username, courtId);
    }
}
