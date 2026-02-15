package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.NBT.NBTParseException;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {

    private final CompoundTag chunkTag;
    private int timestamp;
    private ChunkPos chunkPos;
    private boolean isChunkInitialized = false;

    private final boolean isCorrupt;
    private Throwable error;

    private final Region region;

    private Chunk(CompoundTag compoundTag, int timestamp, ChunkPos chunkPos, Region region) {
        this.chunkTag = compoundTag;
        this.timestamp = timestamp;
        this.chunkPos = chunkPos;
        this.isChunkInitialized = true;
        this.isCorrupt = false;
        this.region = region;
    }

    private Chunk(CompoundTag compoundTag, boolean isChunkInitialized, Region region) {
        this.chunkTag = compoundTag;
        this.isChunkInitialized = isChunkInitialized;
        this.isCorrupt = false;
        this.region = region;
    }

    private Chunk(ChunkPos chunkPos, Throwable cause, Region region) {
        this.chunkTag = null;
        this.chunkPos = chunkPos;
        this.isCorrupt = true;
        this.error = cause;
        this.region = region;
    }

    public static Chunk of(DataInput dataInput, int timestamp, ChunkPos chunkPos, Region region) {
        Tag parsed;
        try {
            parsed = NBT.parse(dataInput);
        } catch (IOException e) {
            return ofCorrupt(chunkPos, e, region);
        }
        if (parsed instanceof CompoundTag compoundTag) {
            return new Chunk(compoundTag, timestamp, chunkPos, region);
        } else {
            return ofCorrupt(chunkPos, new NBTParseException("Invalid Chunk Tag"), region);
        }
    }

    public static Chunk ofUninitialized(Region region) {
        return new Chunk(new CommonCompoundTag(""), false, region);
    }

    public static Chunk ofCorrupt(ChunkPos chunkPos, Throwable cause, Region region) {
        return new Chunk(chunkPos, cause, region);
    }

    public boolean isChunkInitialized() {
        return isChunkInitialized;
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

    public Region getRegion() {
        return region;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }

    public record ChunkPos(int x, int z) {
    }
}
