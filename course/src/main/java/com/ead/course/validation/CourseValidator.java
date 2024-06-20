package com.ead.course.validation;

import com.ead.course.controllers.dtos.CourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;


@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        CourseDTO courseDTO = (CourseDTO) obj;
        validator.validate(courseDTO, errors);

        if (!errors.hasErrors()) {
            validateUserInstructor(courseDTO.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructorId, Errors errors) {
//        ResponseEntity<UserDTO> responseUserInstructor;
//        try {
//            responseUserInstructor = authUserClient.getOneUserById(userInstructorId);
//            if (!responseUserInstructor.getBody().getUserType().equals(UserType.INSTRUCTOR)) {
//                errors.rejectValue("userInstructor", "UserInstructor", "User must be INSTRUCTOR or ADMIM");
//            }
//        } catch (HttpStatusCodeException e) {
//            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//                errors.rejectValue("userInstructor", "UserInstructor", "Instructor not found");
//
//            }
//        }
    }
}
