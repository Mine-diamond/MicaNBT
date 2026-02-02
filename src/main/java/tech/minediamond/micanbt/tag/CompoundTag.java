package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.NBT.NBTReader;
import tech.minediamond.micanbt.NBT.NBTWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class CompoundTag extends AbstractCompoundTag {
    public static final int ID = 10;
    private Map<String, Tag> value;

    public CompoundTag(String name) {
        this(name, new LinkedHashMap<>());
    }

    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = new LinkedHashMap<>(value);
    }

    public void setValue(Map<String, Tag> value) {
        this.value = new LinkedHashMap<>(value);
    }

    public void put(Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    public void putAll(CompoundTag other) {
        this.value.putAll(other.getRawValue());
    }

    public Tag get(String tagName) {
        return this.value.get(tagName);
    }

    public Tag getOrDefault(String key, Tag defaultValue) {
        return this.value.getOrDefault(key, defaultValue);
    }

    public Tag computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Tag> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    public Tag remove(String tagName) {
        return this.value.remove(tagName);
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    public Collection<Tag> values() {
        return this.value.values();
    }

    public int size() {
        return this.value.size();
    }

    public void clear() {
        this.value.clear();
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.values().iterator();
    }

    @Override
    public int getTagId() {
        return ID;
    }

    @Override
    public Map<String, Tag> getClonedValue() {
        Map<String, Tag> clonedMap = new LinkedHashMap<>(Math.max((int) (this.value.size() / .75f) + 1, 16));
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().copy());
        }
        return clonedMap;
    }

    @Override
    public Map<String, Tag> getRawValue() {
        return this.value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        List<Tag> tags = new ArrayList<Tag>();
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
        for (Tag tag : this.value.values()) {
            NBTWriter.writeTag(out, tag);
        }

        out.writeByte(0);
    }

    @Override
    public CompoundTag copy() {
        Map<String, Tag> newMap = new LinkedHashMap<>(Math.max((int) (this.value.size() / .75f) + 1, 16));
        for (Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().copy());
        }

        return new CompoundTag(this.getName(), newMap);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value.equals(((CompoundTag) o).value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(value);
    }
}
