package com.project.backend.dtos.builders;

import com.project.backend.dtos.UserDTO;
import com.project.backend.entities.User;

public class UserBuilder {

    public UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getIdUser(),user.getUsername(),user.getPassword(),user.getRole(),user.getEmail());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getRole(),
                userDTO.getEmail());
    }

}
