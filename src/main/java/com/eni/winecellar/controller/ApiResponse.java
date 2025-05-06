package com.eni.winecellar.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    public static final boolean IS_SUCCESSFUL = true;
    public static final boolean NOT_SUCCESSFUL = false;

    private boolean success;
    private String message;
    private T data;
}
