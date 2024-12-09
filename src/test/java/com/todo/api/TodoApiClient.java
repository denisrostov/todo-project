package com.todo.api;

import com.todo.config.TestConfig;
import com.todo.model.TodoDTO;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.todo.config.ApiEndpoints.Todos;
import static com.todo.config.HttpStatus.Code;
import static com.todo.config.RequestParams.Todos.Params;
import static com.todo.config.RequestParams.Todos.Values;
import static io.restassured.RestAssured.given;

public class TodoApiClient {
    private static final Logger logger = LoggerFactory.getLogger(TodoApiClient.class);
    private final RequestSpecification requestSpec;

    public TodoApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    /**
     * Получает список ID всех TODO.
     */
    public List<Number> getAllTodoIds() {
        logger.debug("Получение списка всех TODO ID");
        return given(requestSpec)
            .queryParam(Params.LIMIT, Values.MAX_LIMIT)
            .queryParam(Params.OFFSET, Values.DEFAULT_OFFSET)
        .when()
            .get(Todos.GET_ALL)
        .then()
            .statusCode(Code.OK)
            .extract()
            .path("id");
    }

    /**
     * Удаляет TODO по указанному ID.
     */
    public void deleteTodo(Number id) {
        logger.debug("Удаление TODO с ID: {}", id);
        given(requestSpec)
            .header(TestConfig.AUTH_HEADER, TestConfig.BASIC_AUTH)
            .pathParam("id", id)
        .when()
            .delete(Todos.DELETE)
        .then()
            .statusCode(Code.NO_CONTENT);
    }

    /**
     * Создает новый TODO.
     */
    public void createTodo(TodoDTO todo) {
        logger.debug("Создание нового TODO: {}", todo);
        given(requestSpec)
            .body(todo)
        .when()
            .post(Todos.CREATE)
        .then()
            .statusCode(Code.CREATED);
    }

    /**
     * Обновляет существующий TODO.
     */
    public void updateTodo(TodoDTO todo) {
        logger.debug("Обновление TODO с ID {}: {}", todo.getId(), todo);
        given(requestSpec)
            .body(todo)
            .pathParam("id", todo.getId())
        .when()
            .put(Todos.UPDATE)
        .then()
            .statusCode(Code.OK);
    }

    /**
     * Удаляет все существующие TODO.
     */
    public void deleteAllTodos() {
        logger.debug("Удаление всех TODO");
        List<Number> todoIds = getAllTodoIds();
        if (todoIds != null) {
            todoIds.forEach(this::deleteTodo);
        }
    }
} 