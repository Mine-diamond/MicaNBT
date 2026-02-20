package tech.minediamond.micanbt.NBT;

import tech.minediamond.micanbt.tag.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

public class NBTWriter {
    CompoundTag tag;
    NBTCompressType compressType;
    boolean littleEndian;

    DataOutput dataOutput;

    private NBTWriter(Builder builder) throws IOException {
        this.tag = builder.tag;
        this.compressType = builder.compressType;
        this.littleEndian = builder.littleEndian;

        if (builder.path != null) {
            writePath(builder.path);
        } else if (builder.stream != null) {
            writeStream(builder.stream);
        } else if (builder.dataOutput != null) {
            writeDirect(builder.dataOutput);
        } else {
            throw new IOException("No path, stream or dataOutput provided");
        }
    }

    public static Builder builder(CompoundTag tag, Path path) {
        return new Builder(tag, path);
    }

    public static Builder builder(CompoundTag tag, OutputStream stream) {
        return new Builder(tag, stream);
    }

    public static Builder builder(CompoundTag tag, DataOutput dataOutput) {
        return new Builder(tag, dataOutput);
    }

    public static Builder builder(CompoundTag tag) {
        return new Builder(tag);
    }

    private void writePath(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }

        try (OutputStream fos = Files.newOutputStream(path); BufferedOutputStream bos = new BufferedOutputStream(fos); OutputStream out = switch (compressType) {
            case GZIP -> new GZIPOutputStream(bos);
            case ZLIB -> new DeflaterOutputStream(bos);
            case UNCOMPRESSED -> bos;
        }; FilterOutputStream dos = littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out)) {
            this.dataOutput = (DataOutput) dos;
            writeNamedTag(this.tag);
        }
    }

    private void writeStream(OutputStream ops) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(ops); OutputStream out = switch (compressType) {
            case GZIP -> new GZIPOutputStream(bos);
            case ZLIB -> new DeflaterOutputStream(bos);
            case UNCOMPRESSED -> bos;
        }; FilterOutputStream dos = littleEndian ? new LittleEndianDataOutputStream(out) : new DataOutputStream(out)) {
            this.dataOutput = (DataOutput) dos;
            writeNamedTag(this.tag);
        }
    }

    private void writeDirect(DataOutput dataOutput) throws IOException {
        this.dataOutput = dataOutput;
        writeNamedTag(this.tag);
    }

    private void writeNamedTag(Tag tag) throws IOException {
        dataOutput.writeByte(tag.getTagId());
        dataOutput.writeUTF(tag.getName());
        writeTagValue(tag);
    }

    private void writeAnonymousTag(Tag tag) throws IOException {
        writeTagValue(tag);
    }

    private void writeTagValue(Tag tag) throws IOException {
        if (tag instanceof CompoundTag compoundTag) {
            writeCompoundTag(compoundTag);
        } else if (tag instanceof ListTag<?> listTag) {
            writeListTag(listTag);
        } else if (tag instanceof StringTag stringTag) {
            dataOutput.writeUTF(stringTag.getRawValue());
        } else if (tag instanceof ByteArrayTag byteArrayTag) {
            writeByteArrayTag(byteArrayTag);
        } else if (tag instanceof IntArrayTag intArrayTag) {
            writeIntArray(intArrayTag);
        } else if (tag instanceof LongArrayTag longArrayTag) {
            writeLongArray(longArrayTag);
        } else if (tag instanceof ByteTag byteTag) {
            dataOutput.writeByte(byteTag.getRawValue());
        } else if (tag instanceof ShortTag shortTag) {
            dataOutput.writeShort(shortTag.getRawValue());
        } else if (tag instanceof IntTag intTag) {
            dataOutput.writeInt(intTag.getRawValue());
        } else if (tag instanceof LongTag longTag) {
            dataOutput.writeLong(longTag.getRawValue());
        } else if (tag instanceof FloatTag floatTag) {
            dataOutput.writeFloat(floatTag.getRawValue());
        } else if (tag instanceof DoubleTag doubleTag) {
            dataOutput.writeDouble(doubleTag.getRawValue());
        } else {
            throw new IOException("Unsupported tag type: " + tag.getClass().getName());
        }
    }

    private void writeCompoundTag(CompoundTag tag) throws IOException {
        for (Tag subTag : tag) {
            writeNamedTag(subTag);
        }

        dataOutput.writeByte(0);
    }

    private void writeListTag(ListTag<?> listTag) throws IOException {
        dataOutput.writeByte(listTag.getElementTypeId());
        dataOutput.writeInt(listTag.size());
        for (Tag tag : listTag.getRawValue()) {
            writeAnonymousTag(tag);
        }
    }

    private void writeByteArrayTag(ByteArrayTag byteArrayTag) throws IOException {
        dataOutput.writeInt(byteArrayTag.size());
        dataOutput.write(byteArrayTag.getRawValue());
    }

    private void writeIntArray(IntArrayTag intArrayTag) throws IOException {
        dataOutput.writeInt(intArrayTag.size());
        for (int i : intArrayTag.getRawValue()) {
            dataOutput.writeInt(i);
        }
    }

    private void writeLongArray(LongArrayTag longArrayTag) throws IOException {
        dataOutput.writeInt(longArrayTag.size());
        for (long l : longArrayTag.getRawValue()) {
            dataOutput.writeLong(l);
        }
    }

    public static class Builder {
        private Path path = null;
        private OutputStream stream = null;
        private DataOutput dataOutput = null;

        private final CompoundTag tag;
        private NBTCompressType compressType = NBTCompressType.GZIP;
        private boolean littleEndian = false;

        private Builder(CompoundTag tag) {
            this.tag = tag;
        }

        private Builder(CompoundTag tag, Path path) {
            this.tag = tag;
            this.path = path;
        }

        private Builder(CompoundTag tag, OutputStream stream) {
            this.tag = tag;
            this.stream = stream;
        }

        private Builder(CompoundTag tag, DataOutput dataOutput) {
            this.tag = tag;
            this.dataOutput = dataOutput;
        }

        public Builder compressType(NBTCompressType compressType) {
            this.compressType = compressType;
            return this;
        }

        public Builder littleEndian(boolean littleEndian) {
            this.littleEndian = littleEndian;
            return this;
        }

        public void write() throws IOException {
            new NBTWriter(this);
        }

        public byte[] toByteArray() throws IOException {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                this.stream = bos;
                new NBTWriter(this);
                return bos.toByteArray();
            }
        }
    }
}
