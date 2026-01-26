package tech.minediamond.micanbt.tag.builtin;

import tech.minediamond.micanbt.tag.TagCreateException;
import tech.minediamond.micanbt.tag.TagFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tag containing a list of tags.
 */
public class ListTag<T extends Tag> extends Tag implements Iterable<T> {
    public static final int ID = 9;
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
     * Creates an empty list tag with the specified name and type.
     *
     * @param name   The name of the tag.
     * @param typeId Tag id of the list.
     */
    public ListTag(String name, int typeId) {
        this(name);

        this.typeId = typeId;
    }

    /**
     * Creates a list tag with the specified name and value.
     * The list tag's type will be set to that of the first tag being added, or null if the given list is empty.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     * @throws IllegalArgumentException If all tags in the list are not of the same type.
     */
    public ListTag(String name, List<T> value) throws IllegalArgumentException {
        this(name);

        this.setValue(value);
    }

    @Override
    public List<T> getValue() {
        return new ArrayList<>(this.value);
    }

    /**
     * Sets the value of this tag.
     * The list tag's type will be set to that of the first tag being added, or null if the given list is empty.
     *
     * @param value New value of this tag.
     * @throws IllegalArgumentException If all tags in the list are not of the same type.
     */
    public void setValue(List<T> value) throws IllegalArgumentException {
        this.value.clear();
        this.typeId = 0;

        for (T tag : value) {
            this.add(tag);
        }
    }

    @Override
    public int getTagId() {
        return ID;
    }

    /**
     * Gets the element type id of the ListTag.
     *
     * @return The ListTag's element type id, or null if the list does not yet have a defined type.
     */
    public int getElementTypeId() {
        return this.typeId;
    }

    /**
     * Adds a tag to this list tag.
     * If the list does not yet have a type, it will be set to the type of the tag being added.
     *
     * @param tag Tag to add. Should not be null.
     * @return If the list was changed as a result.
     * @throws IllegalArgumentException If the tag's type differs from the list tag's type.
     */
    public boolean add(T tag) throws IllegalArgumentException {
        if (tag == null) {
            return false;
        }

        int incomingId = tag.getTagId();
        // If empty list, use this as tag type.
        if (this.typeId == 0) {
            this.typeId = incomingId;
        } else if (this.typeId != incomingId) {
            throw new IllegalArgumentException(String.format("Tag type mismatch. Expected ID: %d, got: %d", this.typeId, incomingId));
        }

        return this.value.add(tag);
    }

    /**
     * Removes a tag from this list tag.
     *
     * @param tag Tag to remove.
     * @return If the list contained the tag.
     */
    public boolean remove(T tag) {
        return this.value.remove(tag);
    }

    /**
     * Gets the tag at the given index of this list tag.
     *
     * @param index Index of the tag.
     * @return The tag at the given index.
     */
    public T get(int index) {
        return this.value.get(index);
    }

    /**
     * Gets the number of tags in this list tag.
     *
     * @return The size of this list tag.
     */
    public int size() {
        return this.value.size();
    }

    @Override
    public Iterator<T> iterator() {
        return this.value.iterator();
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
    @SuppressWarnings("unchecked") // Safe cast: tag.clone() returns a Tag of the same concrete type
    public ListTag<T> clone() {
        ListTag<T> copy = new ListTag<>(this.getName(), this.typeId);
        for (T tag : this.value) {
            copy.add((T) tag.clone());
        }
        return copy;
    }
}
