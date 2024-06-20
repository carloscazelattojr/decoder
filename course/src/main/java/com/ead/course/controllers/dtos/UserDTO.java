package com.ead.course.controllers.dtos;

import com.ead.course.controllers.enums.UserStatus;
import com.ead.course.controllers.enums.UserType;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {

    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private UserStatus userStatus;
    private UserType userType;
    private String phoneNumber;
    private String cpf;
    private String imageUrl;

}
