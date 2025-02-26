package com.blackcow.blackcowgameinven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @NonNull
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)      //null이면 JSON에서 제외
    private T data;
}
