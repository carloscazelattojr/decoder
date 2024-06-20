package com.ead.authuser.controllers;

import com.ead.authuser.controllers.dtos.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "userId",
                                                               direction = Sort.Direction.ASC) Pageable pageable,
                                                       @RequestParam(required = false) UUID courseId) {

        var specification = (courseId != null) ? SpecificationTemplate.userCourseId(courseId).and(spec) : spec;

        Page<UserModel> userModelsPage = userService.findAll(specification, pageable);

        if (!userModelsPage.isEmpty()) {
            userModelsPage.forEach(item -> {
                item.add(linkTo(methodOn(UserController.class).getUserById(item.getUserId())).withSelfRel());
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelsPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        return userModelOptional.<ResponseEntity<Object>>map(userModel -> ResponseEntity.status(HttpStatus.OK).body(userModel))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
        log.debug("DELETE deleteUser userId received {} ", userId);

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (userModelOptional.isEmpty()) {
            log.warn("DELETE deleteUser userId {} not found ", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userService.delete(userModelOptional.get());
        log.debug("DELETE deleteUser userId deleted {} ", userId);
        log.info("User deleted successfully userId {} ", userId);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted sucessfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDTO.UserView.UserPut.class)
                                             @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDTO) {

        log.debug("PUT updateUser userDto received {} ", userDTO.toString());

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        var userModel = userModelOptional.get();
        userModel.setFullName(userDTO.getFullName());
        userModel.setPhoneNumber(userDTO.getPhoneNumber());
        userModel.setCpf(userDTO.getCpf());
        userModel.setLastUdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.save(userModel);
        log.debug("PUT updateUser userId saved {} ", userModel.getUserId());
        log.info("User updated successfully userId {} ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserDTO.UserView.PasswordPut.class)
                                                 @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO userDTO) {

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        if (!userModelOptional.get().getPassword().equals(userDTO.getOldPassword()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password");

        var userModel = userModelOptional.get();
        userModel.setPassword(userDTO.getPassword());
        userModel.setLastUdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body("Passowrd updated sucessfully");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId") UUID userId,
                                              @RequestBody @Validated(UserDTO.UserView.ImagePut.class)
                                              @JsonView(UserDTO.UserView.ImagePut.class) UserDTO userDTO) {

        log.debug("PUT updatePassword userDto received {} ", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (userModelOptional.isEmpty()) {
            log.warn("Mismatched old password userId {} ", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        var userModel = userModelOptional.get();
        userModel.setImageUrl(userDTO.getImageUrl());
        userModel.setLastUdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userService.save(userModel);
        log.debug("PUT updatePassword userId saved {} ", userModel.getUserId());
        log.info("Password updated successfully userId {} ", userModel.getUserId());

        return ResponseEntity.status(HttpStatus.OK).body("ImageUrl updated sucessfully");
    }

}