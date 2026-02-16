package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;
import java.nio.file.Path;

public class NBT {

    public static NBTReader.Builder fromPath(Path path) throws IOException {
        return NBTReader.builder(path);
    }

    public static NBTReader.Builder fromDataInput(DataInput input) throws IOException {
        return NBTReader.builder(input);
    }

    public static NBTReader.Builder fromBytes(byte[] data) throws IOException {
        return NBTReader.builder(data);
    }

    public static Tag read(Path path) throws IOException {
        return NBTReader.builder(path).getTag();
    }

    public static Tag read(Path path, NBTCompressType compressType, boolean littleEndian, CompoundSelection compoundSelection) throws IOException {
        return fromPath(path).compressType(compressType).littleEndian(littleEndian).compoundSelection(compoundSelection).getTag();
    }

    public static Tag read(DataInput dataInput) throws IOException {
        return fromDataInput(dataInput).getTag();
    }

    public static Tag read(DataInput dataInput, CompoundSelection compoundSelection) throws IOException {
        return fromDataInput(dataInput).compoundSelection(compoundSelection).getTag();
    }

    public static Tag read(byte[] data) throws IOException {
        return fromBytes(data).getTag();
    }

    public static Tag read(byte[] data, NBTCompressType compressType, boolean littleEndian, CompoundSelection compoundSelection) throws IOException {
        return fromBytes(data).compressType(compressType).littleEndian(littleEndian).compoundSelection(compoundSelection).getTag();
    }

    public static void write(CompoundTag tag, Path path) throws IOException {
        new NBTWriter(tag, path, NBTCompressType.UNCOMPRESSED, false);
    }

    public static void write(CompoundTag tag, Path path, NBTCompressType compressType, boolean littleEndian) throws IOException {
        new NBTWriter(tag, path, compressType, littleEndian);
    }
}
