package tech.minediamond.micanbt.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * A tag containing a string.
 */
public class StringTag extends Tag {
    public static final int ID = 8;
    private String value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public StringTag(String name) {
        this(name, "");
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readUTF();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override
    public StringTag copy() {
        return new StringTag(this.getName(), this.getValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(value, ((StringTag) o).value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(value);
    }
}
