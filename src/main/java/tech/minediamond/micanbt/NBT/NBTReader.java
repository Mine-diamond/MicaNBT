package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public class NBTReader {
    private final Path path;
    private final Boolean compressed;
    private final boolean littleEndian;
    private final CompoundSelection compoundSelection;
    private DataInput in;

    private final Tag tag;

    private NBTReader(Builder builder) throws IOException {
        this.path = builder.path;
        this.compressed = builder.compressed;
        this.littleEndian = builder.littleEndian;
        this.compoundSelection = builder.compoundSelection;

        tag = read();
    }

    public NBTReader(DataInput in, CompoundSelection compoundSelection) throws IOException {
        this.path = null;
        this.compressed = false;
        this.littleEndian = false;
        this.compoundSelection = compoundSelection;
        this.in = in;

        tag = readNamedTag();
    }

    public static Builder builder(Path path) {
        return new Builder(path);
    }

    public static Tag getTag(Path path) throws IOException {
        return builder(path).getTag();
    }

    public static Tag getTag(Path path, boolean compressed, boolean littleEndian, CompoundSelection compoundSelection) throws IOException {
        return builder(path).compressed(compressed).littleEndian(littleEndian).compoundSelection(compoundSelection).getTag();
    }

    public Tag getTag() {
        return tag;
    }

    private Tag read() throws IOException {
        try (InputStream fis = Files.newInputStream(path);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            return inferenceCompressed(bis);
        }
    }

    private Tag inferenceCompressed(InputStream bis) throws IOException {
        if (compressed == null) {
            bis.mark(3);
            byte[] header = new byte[3];
            if (bis.read(header) < 3) {
                throw new IOException("File is too small");
            }
            bis.reset();

            try (InputStream in = (header[0] == 0x1f && header[1] == (byte) 0x8b && header[2] == 0x08)
                    ? new GZIPInputStream(bis) : bis) {
                return inferenceLittleEndian(in, littleEndian);
            }
        } else {
            try (InputStream in = compressed ? new GZIPInputStream(bis) : bis) {
                return inferenceLittleEndian(in, littleEndian);
            }
        }
    }

    private Tag inferenceLittleEndian(InputStream in, boolean littleEndian) throws IOException {
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
            default -> throw new IOException("Could not find tag with ID \"" + id + "\".");
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
            throw new IOException("Closing EndTag was not found!");
        }

        return compoundTag;
    }

    private ListTag<Tag> readListTag(String name) throws IOException {
        int typeId = in.readUnsignedByte();
        ListTag<Tag> listTag = new ListTag<>(name, typeId);
        int count = in.readInt();

        if (count > 0 && typeId == 0) {
            throw new IOException("ListTag type is TAG_End but count is > 0");
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
        private final Path path;
        private Boolean compressed;
        private boolean littleEndian = false;
        private CompoundSelection compoundSelection = CompoundSelection.COMMON_MAP;

        private Builder(Path path) {
            this.path = path;
        }

        public Builder compressed(boolean compressed) {
            this.compressed = compressed;
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
