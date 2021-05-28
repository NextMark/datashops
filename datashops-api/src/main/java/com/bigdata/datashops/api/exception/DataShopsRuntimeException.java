package com.bigdata.datashops.api.exception;

public class DataShopsRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -1L;

    public DataShopsRuntimeException() {
        super();
    }

    public DataShopsRuntimeException(String message) {
        super(message);
    }

    public DataShopsRuntimeException(String message, Throwable cause) {
        super(message + ":" + cause.getMessage(), cause);
    }

    public DataShopsRuntimeException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
