package com.project.backend.dtos;

import org.springframework.hateoas.RepresentationModel;

public class ResetPasswordDTO extends RepresentationModel<ResetPasswordDTO> {
    private String password;
    private String confirmationPassword;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String password, String confirmationPassword) {
        this.password = password;
        this.confirmationPassword = confirmationPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationPassword() {
        return confirmationPassword;
    }

    public void setConfirmationPassword(String confirmationPassword) {
        this.confirmationPassword = confirmationPassword;
    }
}
