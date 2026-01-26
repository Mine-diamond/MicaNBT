package tech.minediamond.micanbt.tag.builtin;

import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Represents an NBT tag.
 * <p>
 * All tags must have a constructor with a single string parameter for reading tags (can be any visibility).
 * Tags should also have setter methods specific to their value types.
 */
public abstract class Tag {
    private final String name;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name.
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this tag.
     *
     * @return The name of this tag.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    public abstract Object getValue();

    public abstract int getTagId();

    /**
     * Reads this tag from an input stream.
     *
     * @param in Stream to read from.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public abstract void read(DataInput in) throws IOException;

    /**
     * Writes this tag to an output stream.
     *
     * @param out Stream to write to.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public abstract void write(DataOutput out) throws IOException;

    /**
     * Creates and returns a copy of this tag.
     *
     * @return a new tag with the same content.
     */
    public abstract Tag copy();

    @Override
    public boolean equals(Object o) {
        if (o != null && getClass() == o.getClass()) {
            return this.name.equals(((Tag) o).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * get SNBT present of this tag.
     *
     * @return the SNBT present of this tag.
     */
    @Override
    public String toString() {
        return this.toString(true, SNBTStyle.COMPACT);
    }

    /**
     * get SNBT present of this tag.
     *
     * @param stringifyRootTagName does snbt include tag names
     * @param snbtStyle The format of SNBT
     *
     * @return the SNBT present of this tag.
     *
     * @see SNBTStyle
     */
    public String toString(boolean stringifyRootTagName, SNBTStyle snbtStyle) {
        return SNBT.stringify(this, stringifyRootTagName, snbtStyle);
    }
}
