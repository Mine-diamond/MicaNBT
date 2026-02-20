package tech.minediamond.micanbt.tag;

import java.util.Arrays;

/// TAG_Long_Array (ID: {@value #ID}).
///
/// Stores a `long[]` value.
public class LongArrayTag extends Tag {
    public static final int ID = 12;
    private long[] value;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public LongArrayTag(String name) {
        this(name, new long[0]);
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public long[] getClonedValue() {
        return this.value.clone();
    }

    @Override
    public long[] getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// This method creates a clone of the passed array so modifying the raw value will not affect value in this tag.
    /// If the passed parameter is `null`, no operation is performed.
    ///
    /// @param value New value of this tag.
    public void setValue(long[] value) {
        //TODO: Change the behavior of passing null values to throw NullPointerException
        if (value == null) {
            return;
        }

        this.value = value.clone();
    }

    /// Gets a value in this tag's array.
    ///
    /// @param index Index of the value to read.
    /// @return The value at the given index.
    /// @throws IndexOutOfBoundsException If the index is out of range
    public long getValue(int index) {
        return this.value[index];
    }

    /// Sets the value at the specified index in the array.
    ///
    /// @param index Index of the value to set.
    /// @param value Value to set.
    /// @throws IndexOutOfBoundsException If the index is out of range
    public void setValue(int index, long value) {
        this.value[index] = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    /// Gets the length of this tag's array.
    ///
    /// @return This tag's array length.
    public int length() {
        return this.value.length;
    }

    @Override
    public LongArrayTag copy() {
        return new LongArrayTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Arrays.equals(value, ((LongArrayTag) o).value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(value);
    }
}
