package com.bigdata.datashops.api.exception;

public class DataShopsException extends Exception {
    private static final long serialVersionUID = -1L;

    public DataShopsException() {
    }

    public DataShopsException(String message) {
        super(message);
    }

    public DataShopsException(String message, Throwable cause) {
        super(message + ":" + cause.getMessage(), cause);
    }

    public DataShopsException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
