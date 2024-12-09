package com.todo.config;

public class ApiEndpoints {
    private static final String BASE_PATH = "/todos";
    
    public static class Todos {
        public static final String GET_ALL = BASE_PATH;
        public static final String CREATE = BASE_PATH;
        public static final String GET_BY_ID = BASE_PATH + "/{id}";
        public static final String UPDATE = BASE_PATH + "/{id}";
        public static final String DELETE = BASE_PATH + "/{id}";
    }

    public static class WebSocket {
        public static final String CONNECT = "/ws";
    }
} 