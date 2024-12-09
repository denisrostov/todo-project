package com.todo.testdata;

/**
 * Константы для тестовых данных.
 */
public enum TestData {
    PERFORMANCE_TEST_COUNT(100),
    WS_TIMEOUT_SECONDS(5),
    PERFORMANCE_THRESHOLD_TOTAL_MS(2000),
    PERFORMANCE_THRESHOLD_AVG_MS(20);

    private final int value;

    TestData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static class Text {
        public static final String TEST_TODO = "Test TODO";
        public static final String INITIAL_TODO = "Initial TODO";
        public static final String UPDATED_TODO = "Updated TODO";
        public static final String WEBSOCKET_TODO = "WebSocket Test TODO";
        public static final String PERFORMANCE_TODO = "Performance test TODO";
    }
} 