package tech.minediamond.micanbt.tag;

/// TAG_Short (ID: {@value #ID}).
///
/// Stores a `short` value.
public class ShortTag extends Tag {
    public static final int ID = 2;
    private short value;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public ShortTag(String name) {
        this(name, (short) 0);
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override
    public Short getClonedValue() {
        return this.value;
    }

    @Override
    public Short getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(short value) {
        this.value = value;
    }

    /// @return {@value #ID}
    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public ShortTag copy() {
        return new ShortTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value == ((ShortTag) o).value;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Short.hashCode(value);
    }
}
