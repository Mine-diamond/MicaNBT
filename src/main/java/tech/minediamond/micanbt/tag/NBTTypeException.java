package tech.minediamond.micanbt.tag;

import java.io.Serial;

public class NBTTypeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NBTTypeException() {
        super();
    }

    public NBTTypeException(String message) {
        super(message);
    }

    public NBTTypeException(Throwable cause) {
        super(cause);
    }

    public NBTTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
