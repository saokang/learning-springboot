package com.example.util.spring;

/**
 *
 */
public enum ResultEnum {
    SUCCESS(2000, "success"),
    FAILED(4000,"failed");

    private int code;
    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
