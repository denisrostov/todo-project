package com.todo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String text;
    private boolean completed;
} 