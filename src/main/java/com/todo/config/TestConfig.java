package com.todo.config;

import static com.todo.config.ApiEndpoints.WebSocket;

public class TestConfig {
    public static final String BASE_URL = "http://localhost:8888";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BASIC_AUTH = "Basic YWRtaW46YWRtaW4=";
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    public static String getWebSocketUrl() {
        return BASE_URL.replace("http", "ws") + WebSocket.CONNECT;
    }
} 