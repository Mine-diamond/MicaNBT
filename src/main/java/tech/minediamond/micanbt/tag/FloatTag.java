package tech.minediamond.micanbt.tag;

import org.jetbrains.annotations.NotNull;

/// TAG_Float (ID: {@value #ID}).
///
/// Stores a `float` value.
public class FloatTag extends Tag {
    public static final int ID = 5;
    private float value;

    /// Creates a tag with blank name.
    public FloatTag() {
        this("");
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public FloatTag(@NotNull String name) {
        this(name, 0);
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public FloatTag(@NotNull String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public @NotNull Float getClonedValue() {
        return this.value;
    }

    @Override
    public @NotNull Float getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(float value) {
        this.value = value;
    }

    /// @return {@value #ID}
    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public @NotNull FloatTag copy() {
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
