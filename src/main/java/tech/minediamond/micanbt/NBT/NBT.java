package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class NBT {

    public static CompoundTag read(Path path) throws IOException {
        return NBTReader.read(path);
    }

    public static CompoundTag read(Path path, boolean littleEndian) throws IOException {
        return NBTReader.read(path, littleEndian);
    }

    public static CompoundTag read(Path path, boolean compressed, boolean littleEndian) throws IOException {
        return NBTReader.read(path, compressed, littleEndian);
    }

    public static CompoundTag read(InputStream in, boolean littleEndian) throws IOException {
        return NBTReader.read(in, littleEndian);
    }

    public static Tag parse(InputStream in) throws IOException {
        return NBTReader.read(in);
    }

    public static Tag parse(InputStream in, boolean littleEndian) throws IOException {
        return NBTReader.readTag(in, littleEndian);
    }

    public static Tag parse(DataInput in) throws IOException {
        return NBTReader.readTag(in);
    }

    public static void write(CompoundTag tag, Path path) throws IOException {
        NBTWriter.write(tag, path);
    }

    public static void write(CompoundTag tag, Path path, boolean compressed, boolean littleEndian) throws IOException {
        NBTWriter.write(tag, path, compressed, littleEndian);
    }
}
