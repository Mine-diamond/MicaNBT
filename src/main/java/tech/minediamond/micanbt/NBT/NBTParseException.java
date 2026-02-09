package tech.minediamond.micanbt.NBT;

import java.io.IOException;
import java.io.Serial;

public class NBTParseException extends IOException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NBTParseException() {
    }

    public NBTParseException(String message) {
        super(message);
    }

    public NBTParseException(Throwable cause) {
        super(cause);
    }

    public NBTParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
