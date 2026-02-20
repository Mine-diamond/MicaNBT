package tech.minediamond.micanbt.tag;

/// TAG_Byte (ID: {@value #ID}).
///
/// Stores a `byte` value.
public class ByteTag extends Tag {
    public static final int ID = 1;
    private byte value;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public ByteTag(String name) {
        this(name, (byte) 0);
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    @Override
    public Byte getClonedValue() {
        return this.value;
    }

    @Override
    public Byte getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public ByteTag copy() {
        return new ByteTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value == ((ByteTag) o).value;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Byte.hashCode(value);
    }
}
