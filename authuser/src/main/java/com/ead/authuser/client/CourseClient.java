package com.ead.authuser.client;

import com.ead.authuser.controllers.dtos.CourseDTO;
import com.ead.authuser.controllers.dtos.ResponsePageDTO;
import com.ead.authuser.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CourseClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${ead.api.url.course}")
    private String REQUEST_URL_COURSE;

    //@Retry(name = "myRetry", fallbackMethod = "retryFallback")
    @CircuitBreaker(name ="circuitbreakerInstance", fallbackMethod = "circuitbreakerfallback")
    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable) {

        List<CourseDTO> listResult = null;
        String url = REQUEST_URL_COURSE + utilsService.createURL(userId, pageable);

        log.debug("Request URL: {}", url);
        log.info("Request URL: {}", url);

        List<CourseDTO> searchResult = null;
        ResponseEntity<ResponsePageDTO<CourseDTO>> result = null;
        System.out.println("----- Start Request ao Course Microservice -------");
        try {
            ParameterizedTypeReference<ResponsePageDTO<CourseDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<CourseDTO>>() {
            };
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();

            log.debug("Response number of elements: {}", listResult.size());
        } catch (HttpStatusCodeException error) {
            log.error("Error request /course: {}", error);
        }
        log.info("Ending request /courses userId: {}", userId);

        return new PageImpl<>(searchResult);
    }

    public Page<CourseDTO> retryFallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Inside retry retryfallback, cause: {}", t.toString());
        List<CourseDTO> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

    public Page<CourseDTO> circuitbreakerfallback(UUID userId, Pageable pageable, Throwable t) {
        log.info("Inside retry circuitbreakerfallback, cause: {}", t.toString());
        System.out.println("------ FALLBACK ----");
        List<CourseDTO> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }


}
