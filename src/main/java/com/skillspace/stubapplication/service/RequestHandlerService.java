package com.skillspace.stubapplication.service;

import com.skillspace.stubapplication.dto.GetPersonDto;
import com.skillspace.stubapplication.dto.GetPersonResponse;
import com.skillspace.stubapplication.dto.PostPersonDto;
import com.skillspace.stubapplication.dto.PostPersonResponse;
import com.skillspace.stubapplication.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerService.class);

    public GetPersonResponse handleGetRequest(String id, String name) throws InternalServerException, InterruptedException {
        logger.info("Получен запрос с id={}, name={}", id, name);

        int parsedId = Integer.parseInt(id);

        if (parsedId <= 10) {
            String errorMessage = String.format("ID (%d) должно быть больше 10.", parsedId);
            logger.error(errorMessage);
            throw new InternalServerException(errorMessage);
        }

        if (name.length() <= 5) {
            String errorMessage = String.format("Длина name ('%s') должна быть больше 5 символов. Текущая длина: %d", name, name.length());
            logger.error(errorMessage);
            throw new InternalServerException(errorMessage);
        }

        if (parsedId < 50) {
            Thread.sleep(1000);
        } else {
            Thread.sleep(500);
        }

        GetPersonDto person = new GetPersonDto(parsedId, name);
        GetPersonResponse personResponse = new GetPersonResponse(person);

        logger.debug("Сформирован ответ: {}", personResponse);

        return personResponse;
    }

    public PostPersonResponse handlePostRequest(String name, String surname, Integer age) {
        logger.info("Получен запрос с name={}, surname={}, age={}", name, surname, age);

        if (name == null || surname == null || age == null) {
            logger.error("Передаваемые значения в теле запроса не могут быть null");
            throw new InternalServerException("Передаваемые значения в теле запроса не могут быть null");
        }
        PostPersonDto dto1 = new PostPersonDto(name, surname, age);
        PostPersonDto dto2 = new PostPersonDto(surname, name, age * 2);

        PostPersonResponse response = new PostPersonResponse(dto1, dto2);

        logger.debug("Сформирован ответ: {}", response);
        return response;
    }

}
