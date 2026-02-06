package tech.minediamond.micanbt.SNBT;

import java.io.Serial;

public class SNBTParseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SNBTParseException() {
    }

    public SNBTParseException(String message) {
        super(message);
    }

    public SNBTParseException(Throwable cause) {
        super(cause);
    }

    public SNBTParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
