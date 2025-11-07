package com.hcltech.leave.exception;

public class InvalidLeaveOperationException extends RuntimeException {
    public InvalidLeaveOperationException(String message) {
        super(message);
    }
}