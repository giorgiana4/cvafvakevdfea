package com.project.backend.dtos;

import java.time.LocalDateTime;

public class RegisterDTO {
    private String username;
    private String password;
    private String role;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private LocalDateTime birthday;
    private double wallet;
    private double  cryptoWallet;

    public RegisterDTO() {
    }

    public RegisterDTO(String username, String password, String role, String email, String firstName, String lastName,
                       String address, LocalDateTime birthday, double wallet, double cryptoWallet) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthday = birthday;
        this.wallet = wallet;
        this.cryptoWallet = cryptoWallet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public double getCryptoWallet() {
        return cryptoWallet;
    }

    public void setCryptoWallet(double cryptoWallet) {
        this.cryptoWallet = cryptoWallet;
    }
}