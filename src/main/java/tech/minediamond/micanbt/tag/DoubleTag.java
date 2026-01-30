package tech.minediamond.micanbt.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing a double.
 */
public class DoubleTag extends Tag {
    public static final int ID = 6;
    private double value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public DoubleTag(String name) {
        this(name, 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public DoubleTag copy() {
        return new DoubleTag(this.getName(), this.getValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Double.compare(value, ((DoubleTag) o).value) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Double.hashCode(value);
    }
}
