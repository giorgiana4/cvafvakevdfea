package com.project.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Tariff {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID idTariff;

    @Column(name = "season")
    private String season;

    @Column(name = "dayOfWeek")
    private String dayOfWeek;

    @Column(name = "night")
    private boolean night;

    @Column(name = "price")
    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idLocation", nullable = false)
    @JsonIgnore
    private Location location;

    public Tariff() {
    }

    public Tariff(String season, String dayOfWeek, boolean night, double price) {
        this.season = season;
        this.dayOfWeek = dayOfWeek;
        this.night = night;
        this.price = price;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "idTariff=" + idTariff +
                ", season='" + season + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", night=" + night +
                ", price=" + price +
                ", location=" + location +
                '}';
    }
}
