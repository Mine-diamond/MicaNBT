package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.NBT.NBTReader;
import tech.minediamond.micanbt.NBT.NBTWriter;
import tech.minediamond.micanbt.tag.map.OrderedListMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;

public class OrderedCompoundTag extends Tag implements Iterable<Tag> {
    public static final int ID = 10;
    OrderedListMap<String, Tag> value;

    public OrderedCompoundTag(String name) {
        super(name);
    }

    public OrderedCompoundTag(String name, OrderedListMap<String, Tag> map) {
        super(name);
        this.value = map;
    }

    public OrderedCompoundTag(String name, Map<String, Tag> map) {
        super(name);
        this.value = new OrderedListMap<>(map);
    }

    public void put(Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    public void put(int index, Tag tag) {
        this.value.put(tag.getName(), tag, index);
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    public Tag get(String tagName) {
        return this.value.get(tagName);
    }

    public Tag remove(String tagName) {
        return this.value.remove(tagName);
    }

    public void swap(int fromIndex, int toIndex) {
        this.value.swap(fromIndex, toIndex);
    }

    public void moveTo(int fromIndex, int toIndex) {
        this.value.moveTo(fromIndex, toIndex);
    }

    public void moveTo(String tagName, int toIndex) {
        this.value.moveTo(tagName, toIndex);
    }

    public void sort(Comparator<String> comparator) {
        this.value.sort(comparator);
    }

    public int getIndexOf(String tagName) {
        return this.value.indexOf(tagName);
    }

    @Override
    public int getTagId() {
        return ID;
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
    public Object getRawValue() {
        return value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        List<Tag> tags = new ArrayList<>();
        try {
            Tag tag;
            while ((tag = NBTReader.readTag(in)) != null) {
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
    public void write(DataOutput out) throws IOException {
        for (Tag tag : this.value) {
            NBTWriter.writeTag(out, tag);
        }

        out.writeByte(0);
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.value.iterator();
    }

    @Override
    public Tag copy() {
        return new OrderedCompoundTag(getName(), getClonedValue());
    }
}
