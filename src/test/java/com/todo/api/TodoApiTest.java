package com.todo.api;

import com.todo.config.TestConfig;
import com.todo.model.TodoDTO;
import com.todo.testdata.TodoFactory;
import com.todo.websocket.TodoWebSocketClient;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static com.todo.config.ApiEndpoints.Todos.*;
import static com.todo.config.HttpStatus.Code.*;
import static com.todo.config.HttpStatus.Message;
import static com.todo.config.RequestParams.Todos.Params.LIMIT;
import static com.todo.config.RequestParams.Todos.Params.OFFSET;
import static com.todo.config.RequestParams.Todos.Values.DEFAULT_LIMIT;
import static com.todo.config.RequestParams.Todos.Values.*;
import static com.todo.testdata.TestData.*;
import static com.todo.testdata.TestData.Text.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class TodoApiTest extends BaseApiTest {

    @Nested
    @Order(1)
    @DisplayName("1. GET")
    class GetTodosTests {
        @Test
        @Order(1)
        @DisplayName("1.1 Should return list of todos")
        void shouldReturnListOfTodos() {
            given(requestSpec)
                .queryParam(LIMIT, DEFAULT_LIMIT)
                .queryParam(OFFSET, DEFAULT_OFFSET)
            .when()
                .get(GET_ALL)
            .then()
                .statusCode(OK);
        }

        @Test
        @Order(2)
        @DisplayName("1.2 Should return 400 for invalid limit")
        void shouldReturnErrorForInvalidLimit() {
            given(requestSpec)
                .queryParam(LIMIT, INVALID_VALUE)
                .queryParam(OFFSET, DEFAULT_OFFSET)
            .when()
                .get(GET_ALL)
            .then()
                .statusCode(BAD_REQUEST)
                .statusLine(containsString(Message.BAD_REQUEST));
        }

        @Test
        @Order(3)
        @DisplayName("1.3 Should return 400 for invalid offset")
        void shouldReturnErrorForInvalidOffset() {
            given(requestSpec)
                .queryParam(LIMIT, DEFAULT_LIMIT)
                .queryParam(OFFSET, INVALID_VALUE)
            .when()
                .get(GET_ALL)
            .then()
                .statusCode(BAD_REQUEST)
                .statusLine(containsString(Message.BAD_REQUEST));
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2. POST")
    class PostTodosTests {
        @Test
        @Order(1)
        @DisplayName("2.1 Should create new todo")
        void shouldCreateNewTodo() {
            TodoDTO todo = TodoFactory.createTodo(TEST_TODO);
            assertDoesNotThrow(() -> todoApi.createTodo(todo),
                "Создание TODO должно быть успешным");
        }

        @Test
        @Order(2)
        @DisplayName("2.2 Should return 400 for empty text")
        void shouldReturnErrorForEmptyText() {
            TodoDTO todo = TodoFactory.createEmptyTodo();
            var response = given(requestSpec)
                .body(todo)
            .when()
                .post(CREATE);
            response.then()
                .statusCode(BAD_REQUEST)
                .statusLine(containsString(Message.BAD_REQUEST));
        }

        @Test
        @Order(3)
        @DisplayName("2.3 Performance test for POST /todos")
        void shouldMeasureCreateTodoPerformance() {
            long startTime = System.currentTimeMillis();
            
            assertDoesNotThrow(() -> {
                for (int i = 0; i < PERFORMANCE_TEST_COUNT.getValue(); i++) {
                    TodoDTO todo = TodoFactory.createTodo(PERFORMANCE_TODO + " " + i);
                    todoApi.createTodo(todo);
                }
            }, "Массовое создание TODO должно быть успешным");
            
            long totalTime = System.currentTimeMillis() - startTime;
            double averageTime = (double) totalTime / PERFORMANCE_TEST_COUNT.getValue();
            
            logger.info("Performance Test Results for POST /todos");
            logger.info("Total requests: {}", PERFORMANCE_TEST_COUNT.getValue());
            logger.info("Total time: {} ms (threshold: {} ms)", 
                totalTime, PERFORMANCE_THRESHOLD_TOTAL_MS.getValue());
            logger.info("Average time per request: {} ms (threshold: {} ms)", 
                String.format("%.2f", averageTime), PERFORMANCE_THRESHOLD_AVG_MS.getValue());

            assertAll(
                () -> assertTrue(totalTime < PERFORMANCE_THRESHOLD_TOTAL_MS.getValue(),
                    String.format("Общее время выполнения %d ms превышает порог %d ms", 
                        totalTime, PERFORMANCE_THRESHOLD_TOTAL_MS.getValue())),
                () -> assertTrue(averageTime < PERFORMANCE_THRESHOLD_AVG_MS.getValue(),
                    String.format("Среднее время запроса %.2f ms превышает порог %d ms", 
                        averageTime, PERFORMANCE_THRESHOLD_AVG_MS.getValue()))
            );

            todoApi.deleteAllTodos();
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3. PUT")
    class PutTodosTests {
        @Test
        @Order(1)
        @DisplayName("3.1 Should update existing todo")
        void shouldUpdateExistingTodo() {
            TodoDTO todo = TodoFactory.createTodo(INITIAL_TODO);
            
            assertAll(
                () -> assertDoesNotThrow(() -> todoApi.createTodo(todo),
                    "Создание TODO должно быть успешным"),
                () -> {
                    todo.setCompleted(true);
                    todo.setText(UPDATED_TODO);
                    assertDoesNotThrow(() -> todoApi.updateTodo(todo),
                        "Обновление TODO должно быть успешным");
                },
                () -> assertDoesNotThrow(() -> todoApi.deleteTodo(todo.getId()),
                    "Удаление TODO должно быть успешным")
            );
        }

        @Test
        @Order(2)
        @DisplayName("3.2 Should return 404 for invalid id format")
        void shouldReturnErrorForInvalidId() {
            TodoDTO todo = TodoFactory.createTodo(TEST_TODO);

            given(requestSpec)
                .body(todo)
                .pathParam("id", "invalid_id")
                .log().all()
            .when()
                .put(UPDATE)
            .then()
                .log().all()
                .statusCode(NOT_FOUND)
                .statusLine(containsString(Message.NOT_FOUND));
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4. DELETE")
    class DeleteTodosTests {
        @Test
        @Order(1)
        @DisplayName("4.1 Should delete existing todo")
        void shouldDeleteExistingTodo() {
            TodoDTO todo = TodoFactory.createTodo(TEST_TODO);
            assertAll(
                () -> assertDoesNotThrow(() -> todoApi.createTodo(todo),
                    "Создание TODO должно быть успешным"),
                () -> assertDoesNotThrow(() -> todoApi.deleteTodo(todo.getId()),
                    "Удаление TODO должно быть успешным")
            );
        }

        @Test
        @Order(2)
        @DisplayName("4.2 Should return 401 without auth")
        void shouldReturnErrorWithoutAuth() {
            given(requestSpec)
                .pathParam("id", 1)
            .when()
                .delete(DELETE)
            .then()
                .statusCode(UNAUTHORIZED)
                .statusLine(containsString(Message.UNAUTHORIZED));
        }
    }

    @Nested
    @Order(5)
    @DisplayName("5. WebSocket")
    class WebSocketTests {
        @Test
        @Order(1)
        @DisplayName("5.1 Should receive notification for new todo")
        void shouldReceiveWebSocketNotification() throws Exception {
            wsClient = TodoWebSocketClient.connect(
                TestConfig.getWebSocketUrl(),
                WS_TIMEOUT_SECONDS.getValue(), 
                TimeUnit.SECONDS
            );
            assertTrue(wsClient.isOpen(), "WebSocket соединение должно быть открыто");

            TodoDTO todo = TodoFactory.createTodo(WEBSOCKET_TODO);
            assertDoesNotThrow(() -> todoApi.createTodo(todo),
                "Создание TODO должно быть успешным");

            String message = wsClient.waitForMessage(WS_TIMEOUT_SECONDS.getValue(), TimeUnit.SECONDS);
            assertAll(
                () -> assertNotNull(message, "Должно прийти WebSocket уведомление"),
                () -> assertTrue(message.contains(WEBSOCKET_TODO), 
                    "Уведомление должно содержать текст TODO")
            );

            closeWebSocketClient();
            todoApi.deleteAllTodos();
        }
    }
} 