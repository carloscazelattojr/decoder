package com.ead.course.controllers;

import com.ead.course.client.AuthUserClient;
import com.ead.course.controllers.dtos.SubscriptionDTO;
import com.ead.course.controllers.dtos.UserDTO;
import com.ead.course.controllers.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseUserService courseUserService;

    @Autowired
    AuthUserClient authUserClient;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserDTO>> getAllUsersByCourse(@PathVariable(value = "courseId") UUID courseId,
                                                             @PageableDefault(page = 0, size = 10, sort = "courseId",
                                                                     direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, pageable));
    }


    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {

        ResponseEntity<UserDTO> responseUser;
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (courseModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDTO.getUserId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");


        try {
            responseUser = authUserClient.getOneUserById(subscriptionDTO.getUserId());
            if (responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked");


        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        CourseUserModel courseUserModel = courseUserService.saveAndSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDTO.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
    }

    @DeleteMapping("/courses/users/{userId}")
    public ResponseEntity<Object> deleteCourseUserByUser(@PathVariable(value = "userId") UUID userId){

        if (!courseUserService.existsByUserId(userId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseUser not found");
        }
        courseUserService.deleteCourseUserByUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body("CourseUser deleted successfully");
    }


}
