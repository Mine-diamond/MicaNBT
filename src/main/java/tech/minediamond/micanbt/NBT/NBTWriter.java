package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class NBTWriter {

    public static void write(CompoundTag tag, Path path) throws IOException {
        write(tag, path, true, false);
    }

    public static void write(CompoundTag tag, Path path, boolean compressed, boolean littleEndian) throws IOException {
        Path parent = path.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }

        try (OutputStream fos = Files.newOutputStream(path);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             OutputStream out = compressed ? new GZIPOutputStream(bos) : bos) {
            writeTag(out, tag, littleEndian);
        }
    }

    public static void writeTag(OutputStream out, Tag tag) throws IOException {
        writeTag(out, tag, false);
    }

    public static void writeTag(OutputStream out, Tag tag, boolean littleEndian) throws IOException {
        writeTag((DataOutput) (littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out)), tag);
    }

    public static void writeTag(DataOutput out, Tag tag) throws IOException {
        if (tag != null) {
            out.writeByte(tag.getTagId());
            out.writeUTF(tag.getName());
            tag.write(out);
        } else {
            out.writeByte(0);
        }
    }
}
