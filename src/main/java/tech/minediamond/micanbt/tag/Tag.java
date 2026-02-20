package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.path.NBTFinder;
import tech.minediamond.micanbt.path.NBTPath;
import tech.minediamond.micanbt.path.NBTPathParseException;

/// The base class for all NBT (Named Binary Tag) structures.
///
/// NBT tags are used to store structured data in a tree-like format. Each tag
/// consists of a name and a payload defined by its specific implementation.
///
/// ### Supported NBT Types
///
/// | Tag type       | Class          | ID                       |
/// | :------------- | -------------- | :----------------------- |
/// | TAG_End        |                | 0                        |
/// | TAG_Byte       | [ByteTag]      | {@value ByteTag#ID}      |
/// | TAG_Short      | [ShortTag]     | {@value ShortTag#ID}     |
/// | TAG_Int        | [IntTag]       | {@value IntTag#ID}       |
/// | TAG_Long       | [LongTag]      | {@value LongTag#ID}      |
/// | TAG_Float      | [FloatTag]     | {@value FloatTag#ID}     |
/// | TAG_Double     | [DoubleTag]    | {@value DoubleTag#ID}    |
/// | TAG_Byte_Array | [ByteArrayTag] | {@value ByteArrayTag#ID} |
/// | TAG_String     | [StringTag]    | {@value StringTag#ID}    |
/// | TAG_List       | [ListTag]      | {@value ListTag#ID}      |
/// | TAG_Compound   | [CompoundTag]  | {@value CompoundTag#ID}  |
/// | TAG_Int_Array  | [IntArrayTag]  | {@value IntArrayTag#ID}  |
/// | TAG_Long_Array | [LongArrayTag] | {@value LongArrayTag#ID} |
///
/// @see <a href="https://minecraft.wiki/w/NBT_format#TAG_definition">Minecraft Wiki: NBT format</a>
public abstract class Tag {
    private final String name;

    /// Creates a tag with the specified name.
    ///
    /// @param name The name.
    public Tag(String name) {
        this.name = name;
    }

    /// Gets the name of this tag.
    ///
    /// @return The name of this tag.
    public final String getName() {
        return this.name;
    }

    /// Gets the deep copied value of this tag.
    ///
    /// Modifying the returned value does not affect the state inside this tag
    ///
    /// @return The deep copied value of this tag.
    public abstract Object getClonedValue();

    /// Gets the original reference of the value of this tag.
    ///
    /// Modifying the returned value may reflect in this tag.
    ///
    /// @return The original reference of value of this tag.
    public abstract Object getRawValue();

    /// Navigates the NBT tree starting from this tag and retrieves the tag located at
    /// the specified [NBTPath].
    ///
    /// @param nbtPath The pre-parsed path object.
    /// @return The [Tag] at the specified path, or `null` if the path cannot be resolved.
    /// @throws NBTPathParseException If the path format is invalid.
    public Tag at(NBTPath nbtPath) {
        return NBTFinder.get(this, nbtPath);
    }

    /// Navigates the NBT tree starting from this tag and retrieves the tag located at
    /// the specified string path.
    ///
    /// This is a convenience method that internally calls [NBTPath#of(String)].
    /// Note that only static paths are supported (e.g., "Data.Level.Seed").
    ///
    /// @param path The NBT path string to resolve.
    /// @return The [Tag] at the specified path, or `null` if the path cannot be resolved.
    /// @throws NBTPathParseException If the path format is invalid.
    public Tag at(String path) {
        return at(NBTPath.of(path));
    }

    /// Navigates the NBT tree starting from this tag and retrieves the tag located at
    /// the specified path token.
    ///
    /// This is a convenience method that internally calls [NBTPath#fromParts(Object...)].
    /// Note that only static paths are supported (e.g., "Data.Level.Seed").
    ///
    /// @param parts The NBT path token string to resolve.
    /// @return The [Tag] at the specified path, or `null` if the path cannot be resolved.
    /// @throws NBTPathParseException If the path format is invalid.
    public Tag atParts(Object... parts) {
        return at(NBTPath.fromParts(parts));
    }

    /// Get the tag type ID defined according to the NBT specification.
    ///
    /// @return ID of the tag type.
    public abstract int getTagId();

    /// Creates and returns a copy of this tag.
    ///
    /// @return a new tag with the same content.
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

    /// Gets SNBT representation of this tag.
    ///
    /// @return the SNBT representation of this tag.
    @Override
    public String toString() {
        return this.toString(true, SNBTStyle.COMPACT);
    }

    /// Gets SNBT representation of this tag.
    ///
    /// @param stringifyRootTagName does snbt include tag names
    /// @param snbtStyle            The format of SNBT
    /// @return the SNBT representation of this tag.
    /// @see SNBTStyle
    public String toString(boolean stringifyRootTagName, SNBTStyle snbtStyle) {
        return SNBT.stringify(this, stringifyRootTagName, snbtStyle);
    }
}
