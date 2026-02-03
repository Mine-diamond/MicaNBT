package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.NBT.NBTReader;
import tech.minediamond.micanbt.NBT.NBTWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractCompoundTag extends Tag implements Iterable<Tag> {
    public static final int ID = 10;

    public AbstractCompoundTag(String name) {
        super(name);
    }

    public abstract void setValue(Map<String, Tag> map);

    public abstract void put(Tag tag);

    public abstract Tag get(String tagName);

    public abstract Tag getOrDefault(String key, Tag defaultValue);

    public abstract Tag computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Tag> mappingFunction);

    public abstract Tag remove(String tagName);

    public abstract boolean contains(String tagName);

    public abstract boolean isEmpty();

    public abstract int size();

    public abstract void clear();

    @Override
    public int getTagId() {
        return ID;
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
        for (Tag tag : this) {
            NBTWriter.writeTag(out, tag);
        }

        out.writeByte(0);
    }
}
