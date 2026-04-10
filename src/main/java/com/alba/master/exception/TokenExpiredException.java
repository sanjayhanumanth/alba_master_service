package com.alba.master.exception;
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) { super(message); }
}
