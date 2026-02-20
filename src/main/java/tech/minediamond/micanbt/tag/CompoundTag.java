package tech.minediamond.micanbt.tag;

import java.util.Map;

/// TAG_Compound (ID: {@value #ID}).
///
/// A map-like container for named tags. Each entry consists of a unique `String` key
/// and a [Tag] value. It is the primary structure used to group related data.
public abstract class CompoundTag extends Tag implements Iterable<Tag> {
    public static final int ID = 10;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public CompoundTag(String name) {
        super(name);
    }

    /// Replaces the current content of this compound tag with the provided map.
    ///
    /// @param map The map containing the new tags.
    public abstract void setValue(Map<String, Tag> map);

    /// Adds a tag to this compound.
    ///
    /// @param tag The tag to add.
    public abstract void put(Tag tag);

    /// Retrieves a tag by its name.
    ///
    /// @param tagName The name of the tag to find.
    /// @return The tag associated with the name, or `null` if not found.
    public abstract Tag get(String tagName);

    /// Retrieves a tag by its name, or returns a default value if the tag is missing.
    ///
    /// @param key          The name of the tag.
    /// @param defaultValue The value to return if the key is not found.
    /// @return The tag associated with the key, or the default value.
    public abstract Tag getOrDefault(String key, Tag defaultValue);

    /// Computes a tag value if the specified name is not already associated with a value.
    ///
    /// @param key             The name of the tag.
    /// @param mappingFunction The function to compute the value.
    /// @return The current (existing or computed) tag.
    public abstract Tag computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Tag> mappingFunction);

    /// Removes a tag from this compound by its name.
    ///
    /// @param tagName The name of the tag to remove.
    /// @return The removed tag, or `null` if no tag was found with that name.
    public abstract Tag remove(String tagName);

    /// Checks if this compound contains a tag with the specified name.
    ///
    /// @param tagName The name to check.
    /// @return `true` if the tag exists, `false` otherwise.
    public abstract boolean contains(String tagName);

    /// Checks if this compound contains the specified tag.
    ///
    /// @param tag The tag to check for.
    /// @return `true` if the tag exists, `false` otherwise.
    public abstract boolean contains(Tag tag);

    /// Checks if the compound tag contains no tags.
    ///
    /// @return `true` if empty.
    public abstract boolean isEmpty();

    /// Returns the number of tags inside this compound.
    ///
    /// @return The size of the compound.
    public abstract int size();

    /// Removes all tags from this compound.
    public abstract void clear();

    @Override
    public abstract CompoundTag copy();

    @Override
    public int getTagId() {
        return ID;
    }
}
