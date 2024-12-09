package com.todo.config;

public class HttpStatus {
    public static class Code {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int NO_CONTENT = 204;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
    }

    public static class Message {
        public static final String BAD_REQUEST = "Bad Request";
        public static final String UNAUTHORIZED = "Unauthorized";
        public static final String NOT_FOUND = "Not Found";
    }
} 