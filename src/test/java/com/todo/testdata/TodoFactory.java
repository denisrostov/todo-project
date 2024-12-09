package com.todo.testdata;

import com.todo.model.TodoDTO;

/**
 * Фабрика для создания тестовых объектов TodoDTO.
 */
public class TodoFactory {
    private static final long ID_BASE = 10_000_000L;
    private static final long ID_RANGE = 1_000_000_000L;

    public static TodoDTO createTodo(String text) {
        return TodoDTO.builder()
            .id(ID_BASE + (long)(Math.random() * ID_RANGE))
            .text(text)
            .completed(false)
            .build();
    }

    public static TodoDTO createEmptyTodo() {
        return TodoDTO.builder()
            .text("")
            .completed(false)
            .build();
    }

    public static TodoDTO createCompletedTodo(String text) {
        TodoDTO todo = createTodo(text);
        todo.setCompleted(true);
        return todo;
    }
} 