package com.project.backend.dtos;

import org.springframework.hateoas.RepresentationModel;
import java.util.UUID;

public class TariffDTO extends RepresentationModel<TariffDTO> {
    private UUID idTariff;
    private String season;
    private String dayOfWeek;
    private boolean night;
    private double price;
    private String locationAddress;

    public TariffDTO(){}

    public TariffDTO(UUID idTariff, String season, String dayOfWeek, boolean night, double price, String locationAddress) {
        this.idTariff = idTariff;
        this.season = season;
        this.dayOfWeek = dayOfWeek;
        this.night = night;
        this.price = price;
        this.locationAddress = locationAddress;
    }

    public UUID getIdTariff() {
        return idTariff;
    }

    public void setIdTariff(UUID idTariff) {
        this.idTariff = idTariff;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }
}
