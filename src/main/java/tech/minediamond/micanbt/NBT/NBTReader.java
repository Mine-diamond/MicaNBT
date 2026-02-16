package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class NBTReader {
    private final Path path;
    private final byte[] data;
    private DataInput in;
    private NBTCompressType compressType;
    private final boolean littleEndian;
    private final CompoundSelection compoundSelection;

    private final Tag tag;

    private NBTReader(Builder builder) throws IOException {
        if (builder.path == null && builder.data == null && builder.dataInput == null) {
            throw new IllegalArgumentException("No input source provided to NBTReader");
        }
        this.path = builder.path;
        this.data = builder.data;
        this.compressType = builder.compressType;
        this.littleEndian = builder.littleEndian;
        this.compoundSelection = builder.compoundSelection;
        this.in = builder.dataInput;
        if (in != null) {
            tag = readNamedTag();
        } else {
            tag = inferenceAndRead();
        }
        if (tag == null) {
            throw new NBTParseException("Root tag is end tag");
        }
    }

    public static Builder builder(Path path) {
        return new Builder(path);
    }

    public static Builder builder(DataInput dataInput) {
        return new Builder(dataInput);
    }

    public static Builder builder(byte[] data) {
        return new Builder(data);
    }

    public Tag getTag() {
        return tag;
    }

    private Tag inferenceAndRead() throws IOException {
        try (InputStream is = path != null ? new BufferedInputStream(Files.newInputStream(path)) : new ByteArrayInputStream(data)) {
            return inferenceCompressed(is);
        }
    }

    private Tag inferenceCompressed(InputStream is) throws IOException {
        if (compressType == null) {
            is.mark(3);
            byte[] header = new byte[3];
            if (is.read(header) < 3) {
                throw new IOException("File is too small");
            }
            is.reset();

            if (header[0] == 0x1f && header[1] == (byte) 0x8b && header[2] == 0x08) {
                compressType = NBTCompressType.GZIP;
            } else if ((header[0] & 0x0F) == 8 && (header[0] >>> 4) <= 7 && (header[0] * 256 + header[1]) % 31 == 0) {
                compressType = NBTCompressType.ZLIB;
            } else {
                compressType = NBTCompressType.UNCOMPRESSED;
            }
        }

        try (InputStream in = switch (compressType) {
            case UNCOMPRESSED -> is;
            case GZIP -> new GZIPInputStream(is);
            case ZLIB -> new InflaterInputStream(is);
        }) {
            return inferenceLittleEndian(in);
        }
    }

    private Tag inferenceLittleEndian(InputStream in) throws IOException {
        this.in = littleEndian ? new LittleEndianDataInputStream(in) : new DataInputStream(in);
        return readNamedTag();
    }

    private Tag readNamedTag() throws IOException {
        int id = in.readUnsignedByte();
        if (id == 0) {
            return null;
        }
        String name = in.readUTF();
        return createTag(id, name);
    }

    private Tag readAnonymousTag(int id) throws IOException {
        return createTag(id, "");
    }

    private Tag createTag(int id, String name) throws IOException {
        return switch (id) {
            case 1 -> new ByteTag(name, in.readByte());
            case 2 -> new ShortTag(name, in.readShort());
            case 3 -> new IntTag(name, in.readInt());
            case 4 -> new LongTag(name, in.readLong());
            case 5 -> new FloatTag(name, in.readFloat());
            case 6 -> new DoubleTag(name, in.readDouble());
            case 7 -> readByteArrayTag(name);
            case 8 -> new StringTag(name, in.readUTF());
            case 9 -> readListTag(name);
            case 10 -> readCompoundTag(name);
            case 11 -> readIntArrayTag(name);
            case 12 -> readLongArrayTag(name);
            default -> throw new NBTParseException("Could not find tag with ID \"" + id + "\".");
        };
    }

    private CompoundTag readCompoundTag(String name) throws IOException {
        CompoundTag compoundTag = switch (compoundSelection) {
            case COMMON_MAP -> new CommonCompoundTag(name);
            case REORDERABLE_MAP -> new ReorderableCompoundTag(name);
        };
        try {
            Tag tag;
            while ((tag = readNamedTag()) != null) {
                compoundTag.put(tag);
            }
        } catch (EOFException e) {
            throw new NBTParseException("Closing EndTag was not found!");
        }

        return compoundTag;
    }

    private ListTag<Tag> readListTag(String name) throws IOException {
        int typeId = in.readUnsignedByte();
        ListTag<Tag> listTag = new ListTag<>(name, typeId);
        int count = in.readInt();

        if (count > 0 && typeId == 0) {
            throw new NBTParseException("ListTag type is TAG_End but count is > 0");
        }
        for (int index = 0; index < count; index++) {
            listTag.add(readAnonymousTag(typeId));
        }
        return listTag;
    }

    private ByteArrayTag readByteArrayTag(String name) throws IOException {
        byte[] value = new byte[in.readInt()];
        in.readFully(value);
        return new ByteArrayTag(name, value);
    }

    private IntArrayTag readIntArrayTag(String name) throws IOException {
        int[] value = new int[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = in.readInt();
        }
        return new IntArrayTag(name, value);
    }

    private LongArrayTag readLongArrayTag(String name) throws IOException {
        long[] value = new long[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = in.readLong();
        }
        return new LongArrayTag(name, value);
    }

    public static class Builder {
        // one of path, dataInput or data must be provided, and this is ensured through the constructor.
        private Path path;
        private DataInput dataInput;
        private byte[] data;
        // compressed and littleEndian are only required when using path, not needed when using dataInput
        private NBTCompressType compressType; // When not specified, it is automatically inferred; otherwise, the specified value is used.
        private boolean littleEndian = false;
        private CompoundSelection compoundSelection = CompoundSelection.COMMON_MAP;

        private Builder(Path path) {
            this.path = path;
        }

        private Builder(DataInput dataInput) {
            this.dataInput = dataInput;
        }

        private Builder(byte[] data) {
            this.data = data;
        }

        public Builder compressType(NBTCompressType compressType) {
            this.compressType = compressType;
            return this;
        }

        public Builder littleEndian(boolean littleEndian) {
            this.littleEndian = littleEndian;
            return this;
        }

        public Builder compoundSelection(CompoundSelection selection) {
            this.compoundSelection = selection;
            return this;
        }

        public NBTReader build() throws IOException {
            return new NBTReader(this);
        }

        public Tag getTag() throws IOException {
            return new NBTReader(this).getTag();
        }
    }
}
