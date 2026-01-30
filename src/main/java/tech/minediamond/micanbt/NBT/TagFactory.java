package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.*;

public class TagFactory {

    private TagFactory() {
    }

    /**
     * Creates an instance of the tag with the given id and name.
     *
     * @param id      Id of the tag.
     * @param tagName Name to give the tag.
     * @return The created tag.
     * @throws TagCreateException If an error occurs while creating the tag.
     */
    public static Tag createInstance(int id, String tagName) throws TagCreateException {
        return switch (id) {
            case 1 -> new ByteTag(tagName);
            case 2 -> new ShortTag(tagName);
            case 3 -> new IntTag(tagName);
            case 4 -> new LongTag(tagName);
            case 5 -> new FloatTag(tagName);
            case 6 -> new DoubleTag(tagName);
            case 7 -> new ByteArrayTag(tagName);
            case 8 -> new StringTag(tagName);
            case 9 -> new ListTag<>(tagName);
            case 10 -> new CompoundTag(tagName);
            case 11 -> new IntArrayTag(tagName);
            case 12 -> new LongArrayTag(tagName);
            default -> throw new TagCreateException("Could not find tag with ID \"" + id + "\".");
        };
    }
}
