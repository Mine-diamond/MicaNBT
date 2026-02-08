package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.*;

import java.io.DataInput;
import java.io.IOException;

public class TagFactory {

    private TagFactory() {
    }

    /**
     * Creates an instance of the tag with the given id, name and data.
     *
     * @param id      Id of the tag.
     * @param tagName Name to give the tag.
     * @param in      Data to create value.
     * @return The created tag.
     * @throws TagCreateException If an error occurs while creating the tag.
     */
    public static Tag createInstance(int id, String tagName, DataInput in) throws TagCreateException, IOException {
        return switch (id) {
            case 1 -> new ByteTag(tagName, in);
            case 2 -> new ShortTag(tagName, in);
            case 3 -> new IntTag(tagName, in);
            case 4 -> new LongTag(tagName, in);
            case 5 -> new FloatTag(tagName, in);
            case 6 -> new DoubleTag(tagName, in);
            case 7 -> new ByteArrayTag(tagName, in);
            case 8 -> new StringTag(tagName, in);
            case 9 -> new ListTag<>(tagName, in);
            case 10 -> new CommonCompoundTag(tagName, in);
            case 11 -> new IntArrayTag(tagName, in);
            case 12 -> new LongArrayTag(tagName, in);
            default -> throw new TagCreateException("Could not find tag with ID \"" + id + "\".");
        };
    }
}
