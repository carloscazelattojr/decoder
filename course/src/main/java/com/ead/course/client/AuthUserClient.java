package com.ead.course.client;

import com.ead.course.client.dtos.CourseUserDTO;
import com.ead.course.controllers.dtos.ResponsePageDTO;
import com.ead.course.controllers.dtos.UserDTO;
import com.ead.course.services.UtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AuthUserClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URI_AUTHUSER;

    public Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable) {

        List<UserDTO> listResult = null;
        String url = REQUEST_URI_AUTHUSER + utilsService.createURLAllUsersByCourse(courseId, pageable);

        log.debug("Request URL: {}", url);
        log.info("Request URL: {}", url);

        List<UserDTO> searchResult = null;
        ResponseEntity<ResponsePageDTO<UserDTO>> result = null;

        try {
            ParameterizedTypeReference<ResponsePageDTO<UserDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<UserDTO>>() {
            };
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();

            log.debug("Response number of elements: {}", listResult.size());
        } catch (HttpStatusCodeException error) {
            log.error("Error request /course: {}", error);
        }
        log.info("Ending request /courses courseId: {}", courseId);

        return new PageImpl<>(searchResult);
    }

    public ResponseEntity<UserDTO> getOneUserById(UUID userId) {
        String url = REQUEST_URI_AUTHUSER + "/users/" + userId;
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDTO.class);
    }


    public void postSubscritionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URI_AUTHUSER + "/users/" + userId + "/courses/subscription";
        var courseUserDTO = new CourseUserDTO(courseId, userId);
        restTemplate.postForObject(url, courseUserDTO, String.class);
    }

    public void deleteCourseInAuthUser(UUID courseId) {
        String url = REQUEST_URI_AUTHUSER + "/users/courses/" + courseId;
        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
