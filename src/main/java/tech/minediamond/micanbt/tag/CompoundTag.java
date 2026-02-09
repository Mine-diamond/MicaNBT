package tech.minediamond.micanbt.tag;

import java.util.Map;

/**
 * Represents an NBT Compound tag, which serves as a container for a named collection of other tags.
 */
public abstract class CompoundTag extends Tag implements Iterable<Tag> {
    public static final int ID = 10;

    public CompoundTag(String name) {
        super(name);
    }

    /**
     * Replaces the current content of this compound tag with the provided map.
     *
     * @param map The map containing the new tags.
     */
    public abstract void setValue(Map<String, Tag> map);

    /**
     * Adds a tag to this compound.
     *
     * @param tag The tag to add.
     */
    public abstract void put(Tag tag);

    /**
     * Retrieves a tag by its name.
     *
     * @param tagName The name of the tag to find.
     * @return The tag associated with the name, or {@code null} if not found.
     */
    public abstract Tag get(String tagName);

    /**
     * Retrieves a tag by its name, or returns a default value if the tag is missing.
     *
     * @param key          The name of the tag.
     * @param defaultValue The value to return if the key is not found.
     * @return The tag associated with the key, or the default value.
     */
    public abstract Tag getOrDefault(String key, Tag defaultValue);

    /**
     * Computes a tag value if the specified name is not already associated with a value.
     *
     * @param key             The name of the tag.
     * @param mappingFunction The function to compute the value.
     * @return The current (existing or computed) tag.
     */
    public abstract Tag computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Tag> mappingFunction);

    /**
     * Removes a tag from this compound by its name.
     *
     * @param tagName The name of the tag to remove.
     * @return The removed tag, or {@code null} if no tag was found with that name.
     */
    public abstract Tag remove(String tagName);

    /**
     * Checks if this compound contains a tag with the specified name.
     *
     * @param tagName The name to check.
     * @return {@code true} if the tag exists, {@code false} otherwise.
     */
    public abstract boolean contains(String tagName);

    /**
     * Checks if the compound tag contains no tags.
     *
     * @return {@code true} if empty.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the number of tags inside this compound.
     *
     * @return The size of the compound.
     */
    public abstract int size();

    /**
     * Removes all tags from this compound.
     */
    public abstract void clear();

    @Override
    public int getTagId() {
        return ID;
    }
}
