package tech.minediamond.micanbt.NBT;

import java.io.Serial;

/**
 * An exception thrown when an error occurs while created a tag instance.
 */
public class TagCreateException extends Exception {
    @Serial
    private static final long serialVersionUID = -2022049594558041160L;

    public TagCreateException() {
        super();
    }

    public TagCreateException(String message) {
        super(message);
    }

    public TagCreateException(Throwable cause) {
        super(cause);
    }

    public TagCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
