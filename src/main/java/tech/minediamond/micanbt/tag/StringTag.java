package tech.minediamond.micanbt.tag;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/// TAG_String (ID: {@value #ID}).
///
/// Stores a `String` value.
public class StringTag extends Tag {
    public static final int ID = 8;
    private String value;

    /// Creates a tag with blank name.
    public StringTag() {
        this("");
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public StringTag(@NotNull String name) {
        this(name, "");
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public StringTag(@NotNull String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public @NotNull String getClonedValue() {
        return this.value;
    }

    @Override
    public @NotNull String getRawValue() {
        return this.value;
    }

    /// Sets the value of this tag.
    ///
    /// @param value New value of this tag.
    public void setValue(@NotNull String value) {
        this.value = value;
    }

    /// @return {@value #ID}
    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public @NotNull StringTag copy() {
        return new StringTag(this.getName(), this.getClonedValue());
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
