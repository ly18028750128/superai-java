package com.lianziyou.bot.base.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lianziyou.bot.enums.sys.ResultEnum;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApiResult<T> implements Serializable {

    @JSONField(ordinal = 1)
    private int status;
    @JSONField(ordinal = 2)
    private String message;
    @JSONField(ordinal = 3)
    private LocalDateTime timestamp;
    @JSONField(ordinal = 4)
    private T data;

    private ApiResult(int status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    private ApiResult(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    private ApiResult(int status, T data) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    private ApiResult(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    private ApiResult(int status, String message, LocalDateTime timestamp, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <T> ApiResult<T> build(int status, String message, T data) {
        return new ApiResult<>(status, message, data);
    }

    public static <T> ApiResult<T> okBuild(T data) {
        return new ApiResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    public static <T> ApiResult<T> okBuild() {
        return new ApiResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    public static <T> ApiResult<T> finalBuild(String msg) {
        return new ApiResult<>(ResultEnum.FAIL.getCode(), msg);
    }

    public static <T> ApiResult<T> build(int status, String message) {
        return new ApiResult<>(status, message);
    }

    public static <T> ApiResult<T> build(int status, String message, LocalDateTime timestamp, T data) {
        return new ApiResult<>(status, message, timestamp, data);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }


}
