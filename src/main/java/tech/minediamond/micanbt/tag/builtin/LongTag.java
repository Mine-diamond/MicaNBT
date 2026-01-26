package tech.minediamond.micanbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing a long.
 */
public class LongTag extends Tag {
    public static final int ID = 4;
    private long value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public LongTag(String name) {
        this(name, 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readLong();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(this.value);
    }

    @Override
    public LongTag copy() {
        return new LongTag(this.getName(), this.getValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value == ((LongTag) o).value;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Long.hashCode(value);
    }
}
