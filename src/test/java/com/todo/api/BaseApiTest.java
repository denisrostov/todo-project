package com.todo.api;

import com.todo.config.TestConfig;
import com.todo.websocket.TodoWebSocketClient;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected TodoWebSocketClient wsClient;
    protected TodoApiClient todoApi;
    protected RequestSpecification requestSpec;

    @BeforeAll
    void setUp() {
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        
        logger.info("Инициализация тестового окружения");
        
        requestSpec = new RequestSpecBuilder()
            .setBaseUri(TestConfig.BASE_URL)
            .setContentType(TestConfig.DEFAULT_CONTENT_TYPE)
            .build();
        
        todoApi = new TodoApiClient(requestSpec);
        cleanupData();
    }

    @AfterAll
    void tearDown() {
        logger.info("Очистка тестового окружения");
        cleanupData();
        closeWebSocketClient();
    }

    protected void cleanupData() {
        todoApi.deleteAllTodos();
    }

    protected void closeWebSocketClient() {
        if (wsClient != null && !wsClient.isClosed()) {
            wsClient.close();
        }
    }
} 