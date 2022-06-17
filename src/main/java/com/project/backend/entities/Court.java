package com.project.backend.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Court{

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID idCourt;

    @Column(name = "courtNumber", unique = true)
    private int courtNumber;

    @Column(name = "type")
    private String type;

    @Column(name = "details")
    private String details;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "court",cascade =CascadeType.ALL)
    private List<Reservation> reservations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idLocation", nullable=false)
    private Location location;

    public Court() {
    }

    public Court(int courtNumber, String type, String details) {
        this.courtNumber = courtNumber;
        this.type = type;
        this.details = details;
    }

    public UUID getIdCourt() {
        return idCourt;
    }

    public void setIdCourt(UUID idCourt) {
        this.idCourt = idCourt;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public void setCourtNumber(int courtNumber) {
        this.courtNumber = courtNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
