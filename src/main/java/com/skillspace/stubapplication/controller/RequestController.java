package com.skillspace.stubapplication.controller;

import com.skillspace.stubapplication.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @GetMapping("/getRequest")
    public ResponseEntity<GetPersonResponse> getRequest(
            @RequestParam String id,
            @RequestParam String name
    ) {
        try {
            logger.info("Получен запрос с id={}, name={}", id, name);

            int parsedId = Integer.parseInt(id);

            if (parsedId <= 10) {
                String errorMessage = String.format("ID (%d) должно быть больше 10.", parsedId);
                logger.error(errorMessage);
                return ResponseEntity.internalServerError().build();
            }

            if (name.length() <= 5) {
                String errorMessage = String.format("Длина name ('%s') должна быть больше 5 символов. Текущая длина: %d", name, name.length());
                logger.error(errorMessage);
                return ResponseEntity.internalServerError().build();
            }

            if (parsedId < 50) {
                Thread.sleep(1000);
            } else {
                Thread.sleep(500);
            }

            GetPersonDto person = new GetPersonDto(parsedId, name);
            GetPersonResponse personResponse = new GetPersonResponse(person);

            logger.debug("Сформирован ответ: {}", personResponse);

            return ResponseEntity.ok(personResponse);

        } catch (NumberFormatException e) {
            logger.warn("Неверный формат параметра id: '{}'. Ожидается число.", id);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при обработке запроса: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/postRequest")
    public ResponseEntity<PostPersonResponse> postRequest(
            @RequestBody PostRequest request
    ) {
        String name = request.getName();
        String surname = request.getSurname();
        Integer age = request.getAge();
        logger.info("Получен запрос с name={}, surname={}, age={}", name, surname, age);

        if (name == null || surname == null || age == null) {
            logger.error("Передаваемые значения в теле запроса не могут быть null");
            return ResponseEntity.internalServerError().build();
        }
        PostPersonDto dto1 = new PostPersonDto(name, surname, age);
        PostPersonDto dto2 = new PostPersonDto(surname, name, age * 2);

        PostPersonResponse response = new PostPersonResponse(dto1, dto2);

        logger.debug("Сформирован ответ: {}", response);

        return ResponseEntity.ok(response);
    }
}