package com.skillspace.stubapplication.controller;

import com.skillspace.stubapplication.dto.*;
import com.skillspace.stubapplication.exception.InternalServerException;
import com.skillspace.stubapplication.service.RequestHandlerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1")
@AllArgsConstructor
public class RequestController {

    private final RequestHandlerService requestHandlerService;

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @GetMapping("/getRequest")
    public ResponseEntity<GetPersonResponse> getRequest(
            @RequestParam String id,
            @RequestParam String name
    ) {
        try {
            return ResponseEntity.ok(requestHandlerService.handleGetRequest(id, name));
        } catch (InternalServerException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NumberFormatException e) {
            logger.warn("Неверный формат параметра id: '{}'. Ожидается число.", id);
            return ResponseEntity.badRequest().build();
        } catch (InterruptedException e) {
            logger.error("Непредвиденная ошибка при обработке запроса: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/postRequest")
    public ResponseEntity<PostPersonResponse> postRequest(
            @RequestBody PostRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    requestHandlerService.handlePostRequest(request.getName(), request.getSurname(), request.getAge()));
        } catch (InternalServerException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}