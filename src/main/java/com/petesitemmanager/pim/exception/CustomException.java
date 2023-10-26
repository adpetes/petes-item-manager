package com.petesitemmanager.pim.exception;

public class CustomException extends Exception {
    private int errorCode;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getCustomErrorCode() {
        return errorCode;
    }
}
