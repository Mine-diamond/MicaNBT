package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.NBT.NBTReader;
import tech.minediamond.micanbt.util.map.OrderedListMap;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * A specialized {@link CompoundTag} that allows elements to be reordered,
 * accessed by index, or sorted explicitly.
 */
public class ReorderableCompoundTag extends CompoundTag {
    OrderedListMap<String, Tag> value;

    public ReorderableCompoundTag(String name) {
        this(name, new OrderedListMap<>());
    }

    public ReorderableCompoundTag(String name, OrderedListMap<String, Tag> map) {
        super(name);
        this.value = map;
    }

    public ReorderableCompoundTag(String name, Map<String, Tag> map) {
        super(name);
        this.value = new OrderedListMap<>(map);
    }

    public ReorderableCompoundTag(String name, DataInput in) throws IOException {
        this(name);
        // read dataInput
        List<Tag> tags = new ArrayList<>();
        try {
            Tag tag;
            while ((tag = NBTReader.readNamedTag(in)) != null) {
                tags.add(tag);
            }
        } catch (EOFException e) {
            throw new IOException("Closing EndTag was not found!");
        }

        for (Tag tag : tags) {
            this.put(tag);
        }
    }

    @Override
    public void setValue(Map<String, Tag> map) {
        this.value = new OrderedListMap<>(map);
    }

    /**
     * Sets the internal map to the provided {@link OrderedListMap}.
     *
     * @param map The new ordered map.
     */
    public void setValue(OrderedListMap<String, Tag> map) {
        this.value = map;
    }

    @Override
    public void put(Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    /**
     * Inserts a tag at a specific index.
     *
     * @param index The target position.
     * @param tag   The tag to insert.
     */
    public void put(int index, Tag tag) {
        this.value.put(tag.getName(), tag, index);
    }

    /**
     * Adds all tags from another reorderable compound tag.
     *
     * @param other The source tag.
     */
    public void putAll(ReorderableCompoundTag other) {
        this.value.putAll(other.getRawValue());
    }

    @Override
    public Tag get(String tagName) {
        return this.value.get(tagName);
    }

    /**
     * Retrieves the tag at the specified index.
     *
     * @param index The index of the tag.
     * @return The tag at that index.
     */
    public Tag get(int index) {
        return this.value.get(index);
    }

    @Override
    public Tag getOrDefault(String tagName, Tag defaultTag) {
        return this.value.getOrDefault(tagName, defaultTag);
    }

    @Override
    public Tag computeIfAbsent(String key, Function<? super String, ? extends Tag> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Replaces the tag at a specific index with a new tag.
     *
     * @param index The index to replace.
     * @param tag   The new tag.
     * @return The old tag that was replaced.
     */
    public Tag replaceAt(int index, Tag tag) {
        return this.value.replaceAt(index, tag.getName(), tag);
    }

    /**
     * Replaces an existing tag with a new one. The tag to be replaced is identified by the name of oldTag. The position of the tag remains unchanged.
     *
     * @param oldTag The tag to be replaced.
     * @param newTag The new tag.
     * @return The old tag that was replaced.
     */
    public Tag replaceAt(Tag oldTag, Tag newTag) {
        Objects.requireNonNull(oldTag, "tag to replace is null");
        return this.value.replaceAt(oldTag.getName(), newTag.getName(), newTag);
    }

    @Override
    public Tag remove(String tagName) {
        return this.value.remove(tagName);
    }

    @Override
    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    /**
     * Swaps the positions of two tags within the compound.
     *
     * @param fromIndex The first index.
     * @param toIndex   The second index.
     */
    public void swap(int fromIndex, int toIndex) {
        this.value.swap(fromIndex, toIndex);
    }

    /**
     * Moves a tag from one index to another.
     *
     * @param fromIndex The current index.
     * @param toIndex   The target index.
     */
    public void moveTo(int fromIndex, int toIndex) {
        this.value.moveTo(fromIndex, toIndex);
    }

    /**
     * Moves a tag with a specific name to a new index.
     *
     * @param tagName The name of the tag to move.
     * @param toIndex The target index.
     */
    public void moveTo(String tagName, int toIndex) {
        this.value.moveTo(tagName, toIndex);
    }

    /**
     * Sorts the tags in this compound using the provided comparator for the tag names.
     *
     * @param comparator The comparator to define the order.
     */
    public void sort(Comparator<String> comparator) {
        this.value.sort(comparator);
    }

    /**
     * Finds the index of a tag by its name.
     *
     * @param tagName The name of the tag.
     * @return The index, or -1 if not found.
     */
    public int indexOf(String tagName) {
        return this.value.indexOf(tagName);
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public OrderedListMap<String, Tag> getClonedValue() {
        OrderedListMap<String, Tag> copy = new OrderedListMap<>();
        for (Tag tag : value) {
            copy.put(tag.getName(), tag);
        }
        return copy;
    }

    @Override
    public OrderedListMap<String, Tag> getRawValue() {
        return value;
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.value.iterator();
    }

    @Override
    public Tag copy() {
        return new ReorderableCompoundTag(getName(), getClonedValue());
    }
}
