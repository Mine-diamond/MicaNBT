package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.tag.builtin.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry containing different tag classes.
 */
public class TagRegistry {
    private static final Map<Integer, Class<? extends Tag>> idToTag = new HashMap<Integer, Class<? extends Tag>>();
    private static final Map<Class<? extends Tag>, Integer> tagToId = new HashMap<Class<? extends Tag>, Integer>();

    static {
        register(ByteTag.ID, ByteTag.class);
        register(ShortTag.ID, ShortTag.class);
        register(IntTag.ID, IntTag.class);
        register(LongTag.ID, LongTag.class);
        register(FloatTag.ID, FloatTag.class);
        register(DoubleTag.ID, DoubleTag.class);
        register(ByteArrayTag.ID, ByteArrayTag.class);
        register(StringTag.ID, StringTag.class);
        register(ListTag.ID, ListTag.class);
        register(CompoundTag.ID, CompoundTag.class);
        register(IntArrayTag.ID, IntArrayTag.class);
        register(LongArrayTag.ID, LongArrayTag.class);
    }

    /**
     * Registers a tag class.
     *
     * @param id  ID of the tag.
     * @param tag Tag class to register.
     * @throws TagRegisterException If an error occurs while registering the tag.
     */
    private static void register(int id, Class<? extends Tag> tag) throws TagRegisterException {
        if (idToTag.containsKey(id)) {
            throw new TagRegisterException("Tag ID \"" + id + "\" is already in use.");
        }

        if (tagToId.containsKey(tag)) {
            throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered.");
        }

        idToTag.put(id, tag);
        tagToId.put(tag, id);
    }

    /**
     * Gets the tag class with the given id.
     *
     * @param id Id of the tag.
     * @return The tag class with the given id, or null if it cannot be found.
     */
    public static Class<? extends Tag> getClassFor(int id) {
        if (!idToTag.containsKey(id)) {
            return null;
        }

        return idToTag.get(id);
    }

    /**
     * Gets the id of the given tag class.
     *
     * @param clazz The tag class to get the id of.
     * @return The id of the given tag class, or -1 if it cannot be found.
     */
    public static int getIdFor(Class<? extends Tag> clazz) {
        if (!tagToId.containsKey(clazz)) {
            return -1;
        }

        return tagToId.get(clazz);
    }

    /**
     * Creates an instance of the tag with the given id, using the String constructor.
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
