package com.skillspace.stubapplication.controller;

import com.skillspace.stubapplication.dto.Person;
import com.skillspace.stubapplication.dto.PersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @GetMapping("/app/v1/getRequest")
    public ResponseEntity<PersonResponse> getRequest(
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

            Person person = new Person(parsedId, name);
            PersonResponse personResponse = new PersonResponse(person);

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
}

