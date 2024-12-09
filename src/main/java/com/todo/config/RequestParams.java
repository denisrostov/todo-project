package com.todo.config;

public class RequestParams {
    public static class Todos {
        public static class Params {
            public static final String LIMIT = "limit";
            public static final String OFFSET = "offset";
        }

        public static class Values {
            public static final int DEFAULT_LIMIT = 10;
            public static final int DEFAULT_OFFSET = 0;
            public static final int INVALID_VALUE = -1;
            public static final int MAX_LIMIT = 100;  // для получения всех записей
        }
    }
} 