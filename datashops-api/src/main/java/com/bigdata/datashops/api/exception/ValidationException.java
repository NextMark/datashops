package com.bigdata.datashops.api.exception;

public class ValidationException extends DataShopsRuntimeException {
    private static final long serialVersionUID = -1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

}
