package com.example.cms.service;

public class ServiceResult<T> {
    public enum Status {
        OK,
        NOT_FOUND,
        INVALID_INPUT
    }

    private final Status status;
    private final T data;

    public ServiceResult(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() { return status; }
    public T getData() { return data; }

    public static <T> ServiceResult<T> ok(T data) {
        return new ServiceResult<>(Status.OK, data);
    }

    public static <T> ServiceResult<T> notFound() {
        return new ServiceResult<>(Status.NOT_FOUND, null);
    }

    public static <T> ServiceResult<T> invalidInput() {
        return new ServiceResult<>(Status.INVALID_INPUT, null);
    }
}
