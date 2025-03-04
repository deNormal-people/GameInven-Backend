package com.blackcow.blackcowgameinven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)      //null이면 JSON에서 제외
    private T data;

    //message 값 필수로 받기위해서 @Builder 사용대신 직접 생성
    public static class Builder<T>{
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

    //생성자 직접 호출금지
    private ApiResponse() {}            

    private ApiResponse(Builder<T> builder) {
        this.message = builder.message;
        this.data = builder.data;
    }

    public static <T> Builder<T> builder(String message){
        return new Builder<>(message);
    }

}
