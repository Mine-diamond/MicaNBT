package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {
    boolean chunkInitialized = false;

    CompoundTag chunkTag;
    int timestamp;
    ChunkPos chunkPos;

    public Chunk(DataInput dataInput, int timestamp, ChunkPos chunkPos, boolean chunkInitialized) throws IOException {
        this.timestamp = timestamp;
        this.chunkPos = chunkPos;
        this.chunkInitialized = chunkInitialized;
        Tag parsed = NBT.parse(dataInput);
        if (parsed instanceof CompoundTag compoundTag) {
            chunkTag = compoundTag;
        } else {
            throw new IOException("chunk tag must be a compound tag, but was: " + parsed.getClass());
        }
    }

    private Chunk(CompoundTag compoundTag, boolean chunkInitialized) {
        this.chunkTag = compoundTag;
    }

    public static Chunk ofEmptyChunk() {
        return new Chunk(new CommonCompoundTag(""), false);
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
