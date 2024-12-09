package com.todo.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TodoWebSocketClient extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(TodoWebSocketClient.class);
    private CompletableFuture<String> messageFuture;
    private final CompletableFuture<Void> connectionFuture;

    public TodoWebSocketClient(URI serverUri) {
        super(serverUri);
        this.messageFuture = new CompletableFuture<>();
        this.connectionFuture = new CompletableFuture<>();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket соединение открыто: {}", getURI());
        connectionFuture.complete(null);
    }

    @Override
    public void onMessage(String message) {
        logger.debug("Получено WebSocket сообщение: {}", message);
        messageFuture.complete(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket соединение закрыто: {} (код: {})", reason, code);
        String error = String.format("WebSocket соединение закрыто: %s (код: %d)", reason, code);
        connectionFuture.completeExceptionally(new RuntimeException(error));
        messageFuture.completeExceptionally(new RuntimeException(error));
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket ошибка", ex);
        connectionFuture.completeExceptionally(ex);
        messageFuture.completeExceptionally(ex);
    }

    public void resetMessageFuture() {
        this.messageFuture = new CompletableFuture<>();
    }

    public String waitForMessage(long timeout, TimeUnit unit) throws Exception {
        return messageFuture.get(timeout, unit);
    }

    public static TodoWebSocketClient connect(String url, long timeout, TimeUnit unit) throws Exception {
        TodoWebSocketClient client = new TodoWebSocketClient(new URI(url));
        client.connectBlocking(timeout, unit);
        
        // Ждем успешного подключения или ошибки
        try {
            client.connectionFuture.get(timeout, unit);
        } catch (Exception e) {
            client.close();
            throw new RuntimeException("Не удалось установить WebSocket соединение", e);
        }
        
        return client;
    }
} 