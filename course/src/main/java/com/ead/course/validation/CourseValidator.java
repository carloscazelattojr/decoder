package com.ead.course.validation;

import com.ead.course.controllers.dtos.CourseDTO;
import com.ead.course.controllers.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;


@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    private UserService userService;


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

        Optional<UserModel> userModelOptional = userService.findById(userInstructorId);
        if (userModelOptional.isEmpty())
            errors.rejectValue("userInstructor", "UserInstructor", "Instructor not found");
        else if (userModelOptional.get().getUserType().equals(UserType.STUDENT.toString()))
            errors.rejectValue("userInstructor", "UserInstructor", "User must be INSTRUCTOR or ADMIM");

    }
}
