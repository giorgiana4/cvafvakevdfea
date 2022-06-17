package com.project.backend.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Location {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID idLocation;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "address", unique = true)
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "startHour")
    private int startHour;

    @Column(name = "endHour")
    private int endHour;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Court> courts;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Tariff> tariffs;

    public Location() {
    }

    public Location(double latitude, double longitude, String address, String name, int startHour, int endHour) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public UUID getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(UUID idLocation) {
        this.idLocation = idLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public List<Court> getCourts() {
        return courts;
    }

    public void setCourts(List<Court> courts) {
        this.courts = courts;
    }

    public List<Tariff> getTariffs() {
        return tariffs;
    }

    public void setTariffs(List<Tariff> tariffs) {
        this.tariffs = tariffs;
    }
}
