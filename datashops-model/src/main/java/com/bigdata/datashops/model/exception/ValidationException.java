package com.bigdata.datashops.model.exception;

public class ValidationException extends DataShopsRuntimeException {
    private static final long serialVersionUID = -1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

}
