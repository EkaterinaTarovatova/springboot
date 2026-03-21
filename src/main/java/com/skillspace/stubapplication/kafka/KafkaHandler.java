package com.skillspace.stubapplication.kafka;

import com.skillspace.stubapplication.dto.*;
import com.skillspace.stubapplication.exception.InternalServerException;
import com.skillspace.stubapplication.service.RequestHandlerService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
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
            JsonNode jsonNode = objectMapper.readTree(request);
            if (jsonNode.has("id") && jsonNode.has("name")) {
                GetKafkaRequest getKafkaRequest = objectMapper.treeToValue(jsonNode, GetKafkaRequest.class);
                GetPersonResponse response = requestHandlerService.handleGetRequest(getKafkaRequest.getId(), getKafkaRequest.getName());
                kafkaTemplate.send(RESPONSE_TOPIC_NAME, objectMapper.writeValueAsString(response));
            } else if (jsonNode.has("name") && jsonNode.has("surname") && jsonNode.has("age")) {
                PostRequest postRequest = objectMapper.treeToValue(jsonNode, PostRequest.class);
                PostPersonResponse response = requestHandlerService.handlePostRequest(postRequest.getName(), postRequest.getSurname(), postRequest.getAge());
                kafkaTemplate.send(RESPONSE_TOPIC_NAME, objectMapper.writeValueAsString(response));
            } else {
                kafkaTemplate.send(RESPONSE_TOPIC_NAME, "Нет логики для обработки этого запроса.");
            }
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
}
