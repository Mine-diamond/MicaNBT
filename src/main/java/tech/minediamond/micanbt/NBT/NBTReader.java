package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.TagCreateException;
import tech.minediamond.micanbt.tag.TagFactory;
import tech.minediamond.micanbt.tag.builtin.CompoundTag;
import tech.minediamond.micanbt.tag.builtin.Tag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class NBTReader {

    public static CompoundTag read(Path path) throws IOException {
        return read(path, false);
    }

    public static CompoundTag read(Path path, boolean littleEndian) throws IOException {
        try (InputStream fis = Files.newInputStream(path);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            bis.mark(3);
            byte[] header = new byte[3];
            if (bis.read(header) < 3) {
                throw new IOException("File is too small");
            }
            bis.reset();

            try (InputStream in = (header[0] == 0x1f && header[1] == (byte) 0x8b && header[2] == 0x08)
                    ? new GZIPInputStream(bis) : bis) {
                return read(in, littleEndian);
            }
        }
    }

    public static CompoundTag read(Path path, boolean compressed, boolean littleEndian) throws IOException {
        try (InputStream fis = Files.newInputStream(path);
             BufferedInputStream bis = new BufferedInputStream(fis);
             InputStream in = compressed ? new GZIPInputStream(bis) : bis) {
            return read(in, littleEndian);
        }
    }

    public static Tag read(InputStream in) throws IOException {
        return readTag(in, false);
    }

    public static CompoundTag read(InputStream in, boolean littleEndian) throws IOException {
        Tag tag = readTag(in, littleEndian);
        if (tag instanceof CompoundTag compoundTag) {
            return compoundTag;
        }
        throw new IOException("Root tag is not a CompoundTag! Found: " +
                (tag == null ? "null" : tag.getClass().getSimpleName()));
    }

    public static Tag readTag(InputStream in, boolean littleEndian) throws IOException {
        return readTag(littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in));
    }

    public static Tag readTag(DataInput in) throws IOException {
        int id = in.readUnsignedByte();
        if (id == 0) {
            return null;
        }

        String name = in.readUTF();
        Tag tag;

        try {
            tag = TagFactory.createInstance(id, name);
        } catch (TagCreateException e) {
            throw new IOException("Failed to create tag.", e);
        }

        tag.read(in);
        return tag;
    }
}
