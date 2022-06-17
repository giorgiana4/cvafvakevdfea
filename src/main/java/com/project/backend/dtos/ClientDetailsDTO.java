package com.project.backend.dtos;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ClientDetailsDTO extends RepresentationModel<ClientDetailsDTO>{
    private UUID idClient;
    private String firstName;
    private String lastName;
    private String address;
    private LocalDateTime birthday;
    private double wallet;
    private double cryptoWallet;
    private String username;

    public ClientDetailsDTO(UUID idClient, String firstName, String lastName, String address, LocalDateTime birthday, double wallet, double cryptoWallet,String username) {
        this.idClient=idClient;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthday = birthday;
        this.wallet = wallet;
        this.cryptoWallet = cryptoWallet;
        this.username=username;
    }

    public UUID getIdClient() {
        return idClient;
    }

    public void setIdClient(UUID idClient) {
        this.idClient = idClient;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientDetailsDTO clientDetailsDTO = (ClientDetailsDTO) o;
        return Double.compare(clientDetailsDTO.wallet, wallet) == 0 &&
                Double.compare(clientDetailsDTO.cryptoWallet, cryptoWallet) == 0 &&
                Objects.equals(idClient, clientDetailsDTO.idClient) &&
                Objects.equals(firstName, clientDetailsDTO.firstName) &&
                Objects.equals(lastName, clientDetailsDTO.lastName) &&
                Objects.equals(address, clientDetailsDTO.address) &&
                Objects.equals(birthday, clientDetailsDTO.birthday) &&
                Objects.equals(username, clientDetailsDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, address, birthday, wallet, cryptoWallet, username);
    }
}
