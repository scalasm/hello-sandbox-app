package me.marioscalasm.hsa.architecture.exception;

public class ApplicationException extends RuntimeException {
    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String message, Exception cause) {
        super(message, cause);
    }
}
