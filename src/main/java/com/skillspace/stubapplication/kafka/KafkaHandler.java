package com.skillspace.stubapplication.kafka;

import com.skillspace.stubapplication.dto.*;
import com.skillspace.stubapplication.exception.InternalServerException;
import com.skillspace.stubapplication.service.RequestHandlerService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class KafkaHandler {

    private final ObjectMapper objectMapper;
    private final RequestHandlerService requestHandlerService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String RESPONSE_TOPIC_NAME = "test-topic2";

    @KafkaListener(topics = "test-topic", groupId = "stubapplication")
    public void handleRequests(String request) {
        try {

            if (request.startsWith("GET ") && request.contains("/app/v1/getRequest?")) {
                GetKafkaRequest getKafkaRequest = extractGetParams(request);
                GetPersonResponse response = requestHandlerService.handleGetRequest(getKafkaRequest.getId(), getKafkaRequest.getName());
                kafkaTemplate.send(RESPONSE_TOPIC_NAME, objectMapper.writeValueAsString(response));
                return;
            } else if (request.startsWith("POST ") && request.contains("/app/v1/postRequest")) {
                PostRequest postRequest = extractPostRequest(request);
                if (postRequest != null) {
                    PostPersonResponse response = requestHandlerService.handlePostRequest(
                            postRequest.getName(),
                            postRequest.getSurname(),
                            postRequest.getAge()
                    );
                    kafkaTemplate.send(RESPONSE_TOPIC_NAME, objectMapper.writeValueAsString(response));
                    return;
                }
            }
            kafkaTemplate.send(RESPONSE_TOPIC_NAME, "Нет логики для обработки этого запроса.");
        } catch (InternalServerException e) {
            kafkaTemplate.send(RESPONSE_TOPIC_NAME, "500: " + e.getMessage());
        } catch (NumberFormatException e) {
            String errorMessage = "400: Неверный формат параметра id. Ожидается число.";
            kafkaTemplate.send(RESPONSE_TOPIC_NAME, errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("500: Непредвиденная ошибка при обработке запроса: %s, %s", e.getMessage(), e);
            kafkaTemplate.send(RESPONSE_TOPIC_NAME, errorMessage);
        }
    }

    private GetKafkaRequest extractGetParams(String request) {
        String queryPart = request.substring(request.indexOf("?") + 1);

        String[] params = queryPart.split("&");

        String id = null;
        String name = null;

        for (String param : params) {
            if (param.startsWith("id=")) {
                id = param.substring(3);
            } else if (param.startsWith("name=")) {
                name = param.substring(5);
            }
        }
        return new GetKafkaRequest(id, name);
    }

    private PostRequest extractPostRequest(String request) {
        int jsonStart = request.indexOf('{');
        if (jsonStart == -1) {
            return null;
        }

        try {
            String jsonString = request.substring(jsonStart);
            return objectMapper.readValue(jsonString, PostRequest.class);
        } catch (Exception e) {
            return null;
        }
    }
}
