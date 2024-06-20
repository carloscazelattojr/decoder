package com.ead.authuser.controllers.dtos;

import com.ead.authuser.controllers.enums.CourseLevel;
import com.ead.authuser.controllers.enums.CourseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseDTO {

    private UUID courseId;

    private String name;

    private String description;

    private String imageUrl;

    private CourseStatus courseStatus;

    private CourseLevel courseLevel;

    private UUID userInstructor;

}
