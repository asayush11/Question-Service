package com.example.questions_service.Utility;

public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public APIResponse(boolean success, String message, T data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> APIResponse<T> success(String message, T data){
        return new APIResponse<>(true, message, data, null);
    }

    public static <T> APIResponse<T> error(String message, String error){
        return new APIResponse<>(false, message, null, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
