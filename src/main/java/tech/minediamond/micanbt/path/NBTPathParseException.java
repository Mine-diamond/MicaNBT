package tech.minediamond.micanbt.path;

import java.io.Serial;

public class NBTPathParseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NBTPathParseException() {
    }

    public NBTPathParseException(String message) {
        super(message);
    }

    public NBTPathParseException(Throwable cause) {
        super(cause);
    }

    public NBTPathParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
