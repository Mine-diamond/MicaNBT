package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.NBT.TagCreateException;
import tech.minediamond.micanbt.NBT.TagFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an NBT List tag.
 * A ListTag contains a sequence of unnamed tags of the same type.
 *
 * @param <T> The type of Tag stored in this list.
 */
public class ListTag<T extends Tag> extends Tag implements Iterable<T> {
    public static final int ID = 9;
    /**
     * The NBT Tag ID of the elements contained within this list. Defaults to 0 (TAG_End) for empty lists.
     */
    private int typeId = 0;
    private final List<T> value;

    /**
     * Creates an empty list tag with the specified name and no defined type.
     *
     * @param name The name of the tag.
     */
    public ListTag(String name) {
        super(name);

        this.typeId = 0;
        this.value = new ArrayList<>();
    }

    /**
     * Creates an empty ListTag with the specified name and a predefined element type.
     *
     * @param name   The name of the tag.
     * @param typeId The NBT Tag ID of the elements this list will hold.
     */
    public ListTag(String name, int typeId) {
        this(name);

        this.typeId = typeId;
    }

    /**
     * Creates a ListTag with the specified name and initial values.
     * The element type ID is automatically determined by the first tag in the list.
     *
     * @param name  The name of the tag.
     * @param value The initial list of tags to add.
     * @throws IllegalArgumentException If the tags in the provided list are not of the same type.
     */
    public ListTag(String name, List<T> value) {
        this(name);

        this.setValue(value);
    }

    @Override
    @SuppressWarnings("unchecked") // Safe cast: tag.copy() returns a Tag of the same concrete type
    public List<T> getClonedValue() {
        List<T> clonedList = new ArrayList<>(Math.max(this.value.size(), 10));
        for (T tag : value) {
            clonedList.add((T) tag.copy());
        }
        return clonedList;
    }

    @Override
    public List<T> getRawValue() {
        return this.value;
    }

    /**
     * Replaces the contents of this ListTag.
     *
     * @param value The new list of tags.
     * @throws IllegalArgumentException If the tags in the list are not of the same type or a tag is null.
     */
    public void setValue(List<T> value) {
        this.value.clear();
        for (T tag : value) {
            checkType(tag);
        }
        this.value.addAll(value);
    }

    @Override
    public int getTagId() {
        return ID;
    }

    /**
     * Gets the NBT Tag ID of the elements stored in this ListTag.
     *
     * @return The element type ID, or 0 if the list is empty and has no defined type.
     */
    public int getElementTypeId() {
        return this.typeId;
    }

    /**
     * Appends a tag to the end of this list.
     * If the list is currently empty and has no defined type, the type will be set to the added tag's type.
     *
     * @param tag The tag to add. Must not be null.
     * @throws IllegalArgumentException If the tag's type does not match the list's element type.
     */
    public void add(T tag) {
        checkType(tag);
        this.value.add(tag);
    }

    /**
     * Inserts a tag at the specified position in this list.
     *
     * @param tag   The tag to insert.
     * @param index The index at which to insert.
     * @throws IllegalArgumentException If the tag's type does not match the list's element type.
     */
    public void add(T tag, int index) {
        checkType(tag);
        this.value.add(index, tag);
    }

    /**
     * Appends all tags from the specified collection to this list.
     *
     * @param tags The collection of tags to add.
     * @throws IllegalArgumentException If any tag fails the type check.
     */
    public void addAll(Collection<T> tags) throws IllegalArgumentException {
        for (T tag : tags) {
            checkType(tag);
        }
        this.value.addAll(tags);
    }

    /**
     * Removes the first occurrence of the specified tag from this list.
     *
     * @param tag The tag to remove.
     * @return {@code true} if the list contained the specified element.
     */
    public boolean remove(T tag) {
        return this.value.remove(tag);
    }

    /**
     * Removes the tag at the specified position in this list.
     *
     * @param index The index of the tag to remove.
     * @return The tag that was removed.
     */
    public T remove(int index) {
        return this.value.remove(index);
    }

    /**
     * Returns the tag at the specified position in this list.
     *
     * @param index Index of the tag to return.
     * @return The tag at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public T get(int index) {
        return this.value.get(index);
    }

    /**
     * Replaces the tag at the specified position in this list.
     *
     * @param index Index of the tag to replace.
     * @param tag   The tag to be stored at the specified position.
     * @return The tag previously at the position.
     * @throws IllegalArgumentException If the new tag's type does not match.
     */
    public T set(int index, T tag) {
        checkType(tag);
        return value.set(index, tag);
    }

    /**
     * Returns the index of the first occurrence of the specified tag.
     *
     * @param tag The tag to search for.
     * @return The index of the tag, or -1 if not found.
     */
    public int indexOf(T tag) {
        return this.value.indexOf(tag);
    }

    /**
     * Returns the number of tags in this list.
     *
     * @return The size of the list.
     */
    public int size() {
        return this.value.size();
    }

    /**
     * Checks if the list is empty.
     *
     * @return {@code true} if the list contains no tags.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Removes all tags from this list. Does not reset the element type ID.
     */
    public void clear() {
        this.value.clear();
    }

    /**
     * Checks if this list contains the specified tag.
     *
     * @param tag The tag to check for.
     * @return {@code true} if the tag is present.
     */
    public boolean contains(T tag) {
        if (tag != null && tag.getTagId() != this.typeId) {
            return false;
        }
        return this.value.contains(tag);
    }

    @Override
    public Iterator<T> iterator() {
        return this.value.iterator();
    }

    /**
     * Returns a sequential Stream with this list as its source.
     */
    public Stream<T> stream() {
        return value.stream();
    }

    /**
     * Validates that the tag's type matches the list's defined type.
     * If the list is empty (typeId 0), the first tag defines the list's type.
     *
     * @param tag The tag to validate.
     * @throws IllegalArgumentException If the tag is null or the type is mismatched.
     */
    private void checkType(T tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag is null");
        }

        int incomingId = tag.getTagId();
        // If empty list, use this as tag type.
        if (this.typeId == 0) {
            this.typeId = incomingId;
        } else if (this.typeId != incomingId) {
            throw new IllegalArgumentException(String.format("Tag type mismatch. Expected ID: %d, got: %d", this.typeId, incomingId));
        }
    }

    @Override
    @SuppressWarnings("unchecked") // Safe cast: typeId guarantees all tags in the list are of type T
    public void read(DataInput in) throws IOException {
        this.value.clear();
        this.typeId = in.readUnsignedByte();
        int count = in.readInt();

        if (count > 0 && this.typeId == 0) {
            throw new IOException("ListTag type is TAG_End but count is > 0");
        }
        for (int index = 0; index < count; index++) {

            try {
                Tag tag = TagFactory.createInstance(this.typeId, "");
                tag.read(in);
                this.value.add((T) tag);
            } catch (TagCreateException e) {
                throw new IOException("Failed to create tag with ID: " + this.typeId, e);
            }
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.typeId);
        out.writeInt(this.value.size());
        for (T tag : this.value) {
            tag.write(out);
        }
    }

    @Override
    @SuppressWarnings("unchecked") // Safe cast: tag.copy() returns a Tag of the same concrete type
    public ListTag<T> copy() {
        ListTag<T> copy = new ListTag<>(this.getName(), this.typeId);
        for (T tag : this.value) {
            copy.add((T) tag.copy());
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            ListTag<?> listTag = (ListTag<?>) o;
            return this.typeId == listTag.typeId && this.value.equals(listTag.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), typeId, value);
    }
}
