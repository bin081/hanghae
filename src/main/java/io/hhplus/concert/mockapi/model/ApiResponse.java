package io.hhplus.concert.mockapi.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private String message;
    private int status;

    // 기본 생성자
    public ApiResponse() {}

    // 데이터를 포함한 생성자
    public ApiResponse(T data) {
        this.data = data;
        this.status = 200;  // 기본 성공 코드
        this.message = "Success";
    }

    public ApiResponse(String error, String s, Object o) {
    }


    // Getter 및 Setter
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
