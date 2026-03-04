package tech.minediamond.micanbt.chunk;

import org.jetbrains.annotations.Nullable;
import tech.minediamond.micanbt.nbt.NBT;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.ReorderableCompoundTag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {

    private final @Nullable CompoundTag chunkTag;
    private final int timestamp;
    private final ChunkPos chunkPos;
    private final boolean isChunkInitialized;

    private final boolean isCorrupt;
    private final @Nullable Throwable error;

    private final Region region;

    private Chunk(CompoundTag compoundTag, int timestamp, ChunkPos chunkPos, boolean isChunkInitialized, Region region) {
        this.chunkTag = compoundTag;
        this.timestamp = timestamp;
        this.chunkPos = chunkPos;
        this.isChunkInitialized = isChunkInitialized;

        this.isCorrupt = false;
        this.error = null;

        this.region = region;
    }

    private Chunk(int timestamp, ChunkPos chunkPos, Throwable cause, Region region) {
        this.chunkTag = null;
        this.timestamp = timestamp;
        this.chunkPos = chunkPos;
        this.isChunkInitialized = false;

        this.isCorrupt = true;
        this.error = cause;

        this.region = region;
    }

    public static Chunk of(DataInput dataInput, int timestamp, ChunkPos chunkPos, Region region) {
        CompoundTag parsed;
        try {
            parsed = NBT.read(dataInput);
        } catch (IOException e) {
            return ofCorrupt(timestamp, chunkPos, e, region);
        }
        return new Chunk(parsed, timestamp, chunkPos, true, region);
    }

    public static Chunk ofUninitialized(ChunkPos chunkPos, Region region) {
        return new Chunk(new ReorderableCompoundTag(""), 0, chunkPos, false, region);
    }

    public static Chunk ofCorrupt(int timestamp, ChunkPos chunkPos, Throwable cause, Region region) {
        return new Chunk(timestamp, chunkPos, cause, region);
    }

    public @Nullable CompoundTag getChunkTag() {
        return chunkTag;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public boolean isChunkInitialized() {
        return isChunkInitialized;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }

    public @Nullable Throwable getError() {
        return error;
    }

    public Region getRegion() {
        return region;
    }

    public record ChunkPos(int x, int z) {
    }
}
