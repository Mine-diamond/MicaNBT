package tech.minediamond.micanbt.tag;

import org.jetbrains.annotations.NotNull;

/// TAG_Int (ID: {@value #ID}).
///
/// Stores a `int` value.
public class IntTag extends Tag {
    public static final int ID = 3;
    private int value;

    /// Creates a tag with blank name.
    public IntTag() {
        this("");
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public IntTag(@NotNull String name) {
        this(name, 0);
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public IntTag(@NotNull String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public @NotNull Integer getClonedValue() {
        return this.value;
    }

    @Override
    public @NotNull Integer getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(int value) {
        this.value = value;
    }

    /// @return {@value #ID}
    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public @NotNull IntTag copy() {
        return new IntTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value == ((IntTag) o).value;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.value;
    }
}
