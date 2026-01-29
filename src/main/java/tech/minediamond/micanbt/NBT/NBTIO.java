package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.TagCreateException;
import tech.minediamond.micanbt.tag.TagFactory;
import tech.minediamond.micanbt.tag.builtin.CompoundTag;
import tech.minediamond.micanbt.tag.builtin.Tag;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A class containing methods for reading/writing NBT tags.
 */
public class NBTIO {

    private NBTIO() {}

    /**
     * Reads the compressed, big endian root CompoundTag from the given file.
     *
     * @param path File to read from.
     * @return The read compound tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static CompoundTag read(Path path) throws IOException {
        return read(path, true, false);
    }

    /**
     * Reads the root CompoundTag from the given file.
     *
     * @param path         File to read from.
     * @param compressed   Whether the NBT file is compressed.
     * @param littleEndian Whether the NBT file is little endian.
     * @return The read compound tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static CompoundTag read(Path path, boolean compressed, boolean littleEndian) throws IOException {
        try (InputStream fis = Files.newInputStream(path);
             InputStream in = compressed ? new GZIPInputStream(fis) : fis) {
            Tag tag = readTag(in, littleEndian);
            if (tag instanceof CompoundTag compoundTag) {
                return compoundTag;
            }
            throw new IOException("Root tag is not a CompoundTag! Found: " +
                    (tag == null ? "null" : tag.getClass().getSimpleName()));
        }
    }

    /**
     * Writes the given root CompoundTag to the given file, compressed and in big endian.
     *
     * @param tag  Tag to write.
     * @param path File to write to.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeFile(CompoundTag tag, Path path) throws IOException {
        writeFile(tag, path, true, false);
    }

    /**
     * Writes the given root CompoundTag to the given file.
     *
     * @param tag          Tag to write.
     * @param path         File to write to.
     * @param compressed   Whether the NBT file should be compressed.
     * @param littleEndian Whether to write little endian NBT.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeFile(CompoundTag tag, Path path, boolean compressed, boolean littleEndian) throws IOException {
        Path parent = path.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }

        try (OutputStream fos = Files.newOutputStream(path);
             OutputStream out = compressed ? new GZIPOutputStream(fos) : fos) {
            writeTag(out, tag, littleEndian);
        }
    }

    /**
     * Reads a big endian NBT tag.
     *
     * @param in Input stream to read from.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readTag(InputStream in) throws IOException {
        return readTag(in, false);
    }

    /**
     * Reads an NBT tag.
     *
     * @param in           Input stream to read from.
     * @param littleEndian Whether to read little endian NBT.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readTag(InputStream in, boolean littleEndian) throws IOException {
        return readTag((DataInput) (littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in)));
    }

    /**
     * Reads an NBT tag.
     *
     * @param in Data input to read from.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readTag(DataInput in) throws IOException {
        int id = in.readUnsignedByte();
        if(id == 0) {
            return null;
        }

        String name = in.readUTF();
        Tag tag;

        try {
            tag = TagFactory.createInstance(id, name);
        } catch(TagCreateException e) {
            throw new IOException("Failed to create tag.", e);
        }

        tag.read(in);
        return tag;
    }

    /**
     * Reads a big endian NBT tag.
     *
     * @param in Input stream to read from.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readAnyTag(InputStream in) throws IOException {
        return readAnyTag(in, false);
    }

    /**
     * Reads an NBT tag.
     *
     * @param in           Input stream to read from.
     * @param littleEndian Whether to read little endian NBT.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readAnyTag(InputStream in, boolean littleEndian) throws IOException {
        return readAnyTag((DataInput) (littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in)));
    }

    /**
     * Reads an NBT tag.
     *
     * @param in Data input to read from.
     * @return The read tag, or null if the tag is an end tag.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static Tag readAnyTag(DataInput in) throws IOException {
        int id = in.readUnsignedByte();
        if(id == 0) {
            return null;
        }

        Tag tag;

        try {
            tag = TagFactory.createInstance(id, "");
        } catch(TagCreateException e) {
            throw new IOException("Failed to create tag.", e);
        }

        tag.read(in);
        return tag;
    }

    /**
     * Writes an NBT tag in big endian.
     *
     * @param out Output stream to write to.
     * @param tag Tag to write.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeTag(OutputStream out, Tag tag) throws IOException {
        writeTag(out, tag, false);
    }

    /**
     * Writes an NBT tag.
     *
     * @param out          Output stream to write to.
     * @param tag          Tag to write.
     * @param littleEndian Whether to write little endian NBT.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeTag(OutputStream out, Tag tag, boolean littleEndian) throws IOException {
        writeTag((DataOutput) (littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out)), tag);
    }

    /**
     * Writes an NBT tag.
     *
     * @param out Data output to write to.
     * @param tag Tag to write.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeTag(DataOutput out, Tag tag) throws IOException {
        if(tag != null) {
            out.writeByte(tag.getTagId());
            out.writeUTF(tag.getName());
            tag.write(out);
        } else {
            out.writeByte(0);
        }
    }

    /**
     * Writes an NBT tag in big endian.
     *
     * @param out Output stream to write to.
     * @param tag Tag to write.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeAnyTag(OutputStream out, Tag tag) throws IOException {
        writeAnyTag(out, tag, false);
    }

    /**
     * Writes an NBT tag.
     *
     * @param out          Output stream to write to.
     * @param tag          Tag to write.
     * @param littleEndian Whether to write little endian NBT.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static void writeAnyTag(OutputStream out, Tag tag, boolean littleEndian) throws IOException {
        writeAnyTag((DataOutput) (littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out)), tag);
    }

    public static void writeAnyTag(DataOutput out, Tag tag) throws IOException {
        if (tag != null) {
            out.writeByte(tag.getTagId());
            tag.write(out);
        } else {
            out.writeByte(0);
        }
    }
}
