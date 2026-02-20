package tech.minediamond.micanbt.tag;

/// TAG_Float (ID: {@value #ID}).
///
/// Stores a `float` value.
public class FloatTag extends Tag {
    public static final int ID = 5;
    private float value;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public FloatTag(String name) {
        this(name, 0);
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public Float getClonedValue() {
        return this.value;
    }

    @Override
    public Float getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public FloatTag copy() {
        return new FloatTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Float.compare(value, ((FloatTag) o).value) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Float.hashCode(value);
    }
}
