package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.NBT.NBTParseException;
import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {

    private final CompoundTag chunkTag;
    private final int timestamp;
    private final ChunkPos chunkPos;
    private final boolean isChunkInitialized;

    private final boolean isCorrupt;
    private final Throwable error;

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

    public static Chunk of(DataInput dataInput, CompoundSelection compoundSelection, int timestamp, ChunkPos chunkPos, Region region) {
        Tag parsed;
        try {
            parsed = NBT.read(dataInput, compoundSelection);
        } catch (IOException e) {
            return ofCorrupt(timestamp, chunkPos, e, region);
        }
        if (parsed instanceof CompoundTag compoundTag) {
            return new Chunk(compoundTag, timestamp, chunkPos, true, region);
        } else {
            return ofCorrupt(timestamp, chunkPos, new NBTParseException("Invalid Chunk Tag"), region);
        }
    }

    public static Chunk ofUninitialized(ChunkPos chunkPos, Region region) {
        return new Chunk(new CommonCompoundTag(""), 0, chunkPos, false, region);
    }

    public static Chunk ofCorrupt(int timestamp, ChunkPos chunkPos, Throwable cause, Region region) {
        return new Chunk(timestamp, chunkPos, cause, region);
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

    public boolean isChunkInitialized() {
        return isChunkInitialized;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }

    public Throwable getError() {
        return error;
    }

    public Region getRegion() {
        return region;
    }

    public record ChunkPos(int x, int z) {
    }
}
