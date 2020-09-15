package com.ice.exception;

/**
 * User: jason
 * Date: 13-2-5
 */
public class FailException extends RuntimeException {

    public FailException() {
    }

    public FailException(String s) {
        super(s);
    }

}
