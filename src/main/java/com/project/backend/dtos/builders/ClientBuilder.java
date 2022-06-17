package com.project.backend.dtos.builders;


import com.project.backend.dtos.ClientDTO;
import com.project.backend.dtos.ClientDetailsDTO;
import com.project.backend.entities.Client;

public class ClientBuilder {

    private ClientBuilder() {
    }

    public static ClientDTO toClientDTO(Client client) {
        return new ClientDTO(client.getIdClient(),
                client.getFirstName(),
                client.getLastName());
    }

    public static ClientDetailsDTO toClientDetailsDTO(Client client,String username) {
        return new ClientDetailsDTO(client.getIdClient(),
                client.getFirstName(),
                client.getLastName(),
                client.getAddress(),
                client.getBirthday(),
                client.getWallet(),
                client.getCryptoWallet(),
                username);
    }

    public static Client toEntity(ClientDetailsDTO clientDetailsDTO) {
        return new Client(clientDetailsDTO.getFirstName(),
                clientDetailsDTO.getLastName(),
                clientDetailsDTO.getAddress(),
                clientDetailsDTO.getBirthday(),
                clientDetailsDTO.getWallet(),
                clientDetailsDTO.getCryptoWallet())
                ;
    }
}
