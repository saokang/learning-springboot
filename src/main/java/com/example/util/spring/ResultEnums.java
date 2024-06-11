package com.example.util.spring;

/**
 *
 */
public enum ResultEnums {
    SUCCESS(2000, "success"),
    FAILED(4000,"failed");

    private int code;
    private String msg;

    ResultEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
