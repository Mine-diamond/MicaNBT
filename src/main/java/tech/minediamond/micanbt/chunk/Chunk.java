package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.NBT.NBTParseException;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {
    boolean isCorrupt = false;
    private Throwable error;
    boolean chunkInitialized = false;

    CompoundTag chunkTag;
    int timestamp;
    ChunkPos chunkPos;

    public Chunk(DataInput dataInput, int timestamp, ChunkPos chunkPos, boolean chunkInitialized) {
        this.timestamp = timestamp;
        this.chunkPos = chunkPos;
        this.chunkInitialized = chunkInitialized;
        Tag parsed = null;
        try {
            parsed = NBT.parse(dataInput);
        } catch (IOException e) {
            this.isCorrupt = true;
            this.error = e;
        }
        if (parsed instanceof CompoundTag compoundTag) {
            chunkTag = compoundTag;
        } else {
            this.isCorrupt = true;
            this.error = new NBTParseException("Invalid Chunk Tag");
        }
    }

    private Chunk(CompoundTag compoundTag, boolean chunkInitialized) {
        this.chunkTag = compoundTag;
    }

    private Chunk(ChunkPos chunkPos, Throwable cause) {
        this.chunkPos = chunkPos;
        this.isCorrupt = true;
        this.error = cause;
    }

    public static Chunk ofEmptyChunk() {
        return new Chunk(new CommonCompoundTag(""), false);
    }

    public static Chunk ofCorrupt(ChunkPos chunkPos, Throwable cause) {
        return new Chunk(chunkPos, cause);
    }

    public boolean isChunkInitialized() {
        return chunkInitialized;
    }

    public CompoundTag getChunkTag() {
        return chunkTag;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public record ChunkPos(int x, int z) {
    }
}
