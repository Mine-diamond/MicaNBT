package tech.minediamond.micanbt.tag;

import org.jetbrains.annotations.NotNull;

/// TAG_Long (ID: {@value #ID}).
///
/// Stores a `long` value.
public class LongTag extends Tag {
    public static final int ID = 4;
    private long value;

    /// Creates a tag with blank name.
    public LongTag() {
        this("");
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public LongTag(@NotNull String name) {
        this(name, 0);
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public LongTag(@NotNull String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public @NotNull Long getClonedValue() {
        return this.value;
    }

    @Override
    public @NotNull Long getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(long value) {
        this.value = value;
    }

    /// @return {@value #ID}
    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public @NotNull LongTag copy() {
        return new LongTag(this.getName(), this.getClonedValue());
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
