package com.blackcow.blackcowgameinven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter  // JSON 변환을 위해 Getter 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // Jackson 직렬화 문제 해결
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)  // null 값일 경우 JSON 변환에서 제외
    private T data;

    // message 값 필수로 받기위해 @Builder 대신 직접 Builder 구현
    public static class Builder<T> {
        private final String message;
        private T data;

        public Builder(String message) {
            this.message = message;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<T>(this);
        }
    }

    // Builder 패턴을 위한 private 생성자
    private ApiResponse(Builder<T> builder) {
        this.message = builder.message;
        this.data = builder.data;
    }

    public static <T> Builder<T> builder(String message) {
        return new Builder<>(message);
    }
}
