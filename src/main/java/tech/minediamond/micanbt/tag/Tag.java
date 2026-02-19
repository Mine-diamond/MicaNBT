package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.path.NBTFinder;
import tech.minediamond.micanbt.path.NBTPath;
import tech.minediamond.micanbt.path.NBTPathParseException;

/**
 * The base class for all NBT (Named Binary Tag) structures.
 * <p>
 * NBT tags are used to store structured data in a tree-like format. Each tag
 * consists of an optional name and a payload defined by its specific implementation.
 *
 * @see <a href="https://minecraft.wiki/w/NBT_format#TAG_definition">Minecraft Wiki: NBT format</a>
 */
public abstract class Tag {
    private final String name;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name.
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this tag.
     *
     * @return The name of this tag.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the copied value of this tag.
     *
     * @return The copied value of this tag.
     */
    public abstract Object getClonedValue();

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    public abstract Object getRawValue();

    /**
     * Navigates the NBT tree starting from this tag and retrieves the tag located at
     * the specified {@link NBTPath}.
     *
     * @param nbtPath The pre-parsed path object.
     * @return The {@link Tag} at the specified path, or {@code null} if the path cannot be resolved.
     * @throws NBTPathParseException If the path format is invalid.
     */
    public Tag at(NBTPath nbtPath) {
        return NBTFinder.get(this, nbtPath);
    }

    /**
     * Navigates the NBT tree starting from this tag and retrieves the tag located at
     * the specified string path.
     * <p>
     * This is a convenience method that internally calls {@link NBTPath#of(String)}.
     * Note that only static paths are supported (e.g., "Data.Level.Seed").
     *
     * @param path The NBT path string to resolve.
     * @return The {@link Tag} at the specified path, or {@code null} if the path cannot be resolved.
     * @throws NBTPathParseException If the path format is invalid.
     */
    public Tag at(String path) {
        return at(NBTPath.of(path));
    }

    /**
     * Navigates the NBT tree starting from this tag and retrieves the tag located at
     * the specified path token.
     * <p>
     * This is a convenience method that internally calls {@link NBTPath#fromParts(Object...)}.
     * Note that only static paths are supported (e.g., "Data.Level.Seed").
     *
     * @param parts The NBT path token string to resolve.
     * @return The {@link Tag} at the specified path, or {@code null} if the path cannot be resolved.
     * @throws NBTPathParseException If the path format is invalid.
     */
    public Tag atParts(Object... parts) {
        return at(NBTPath.fromParts(parts));
    }

    /**
     * Get the tag type ID defined according to the NBT specification.
     * <table>
     *     <caption>id table</caption>
     *   <thead>
     *     <tr><th>Tag type</th><th>ID</th></tr>
     *   </thead>
     *   <tbody>
     *     <tr><td>TAG_Byte</td><td>{@value ByteTag#ID}</td></tr>
     *     <tr><td>TAG_Short</td><td>{@value ShortTag#ID}</td></tr>
     *     <tr><td>TAG_Int</td><td>{@value IntTag#ID}</td></tr>
     *     <tr><td>TAG_Long</td><td>{@value LongTag#ID}</td></tr>
     *     <tr><td>TAG_Float</td><td>{@value FloatTag#ID}</td></tr>
     *     <tr><td>TAG_Double</td><td>{@value DoubleTag#ID}</td></tr>
     *     <tr><td>TAG_Byte_Array</td><td>{@value ByteArrayTag#ID}</td></tr>
     *     <tr><td>TAG_String</td><td>{@value StringTag#ID}</td></tr>
     *     <tr><td>TAG_List</td><td>{@value ListTag#ID}</td></tr>
     *     <tr><td>TAG_Compound</td><td>{@value CompoundTag#ID}</td></tr>
     *     <tr><td>TAG_Int_Array</td><td>{@value IntArrayTag#ID}</td></tr>
     *     <tr><td>TAG_Long_Array</td><td>{@value LongArrayTag#ID}</td></tr>
     *   </tbody>
     * </table>
     *
     * @return ID of the tag type.
     *
     * @see <a href="https://minecraft.wiki/w/NBT_format#TAG_definition">Minecraft Wiki: NBT format</a>
     */
    public abstract int getTagId();

    /**
     * Creates and returns a copy of this tag.
     *
     * @return a new tag with the same content.
     */
    public abstract Tag copy();

    @Override
    public boolean equals(Object o) {
        if (o != null && getClass() == o.getClass()) {
            return this.name.equals(((Tag) o).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * get SNBT present of this tag.
     *
     * @return the SNBT present of this tag.
     */
    @Override
    public String toString() {
        return this.toString(true, SNBTStyle.COMPACT);
    }

    /**
     * get SNBT present of this tag.
     *
     * @param stringifyRootTagName does snbt include tag names
     * @param snbtStyle            The format of SNBT
     * @return the SNBT representation of this tag.
     * @see SNBTStyle
     */
    public String toString(boolean stringifyRootTagName, SNBTStyle snbtStyle) {
        return SNBT.stringify(this, stringifyRootTagName, snbtStyle);
    }
}
