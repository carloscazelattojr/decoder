package com.ead.course.controllers;

import com.ead.course.controllers.dtos.SubscriptionDTO;
import com.ead.course.controllers.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(SpecificationTemplate.UserSPec spec,
                                                      @PathVariable(value = "courseId") UUID courseId,
                                                      @PageableDefault(page = 0, size = 10, sort = "courseId",
                                                              direction = Sort.Direction.ASC) Pageable pageable) {

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        return ResponseEntity.status(HttpStatus.OK).body(
                userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }


    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        if (courseService.existsByCourseAndUser(courseId, subscriptionDTO.getUserId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists");

        Optional<UserModel> userModelOptional = userService.findById(subscriptionDTO.getUserId());
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        if (userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked");

        courseService.saveSubscriptionUserInCourse(courseModelOptional.get().getCourseId(), userModelOptional.get().getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }


}
