package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.DataInput;
import java.io.IOException;

public class Chunk {
    CompoundTag chunkTag;

    Chunk(DataInput dataInput) throws IOException {
        Tag parsed = NBT.parse(dataInput);
        if (parsed instanceof CompoundTag compoundTag) {
            chunkTag = compoundTag;
        } else {
            throw new IOException("chunk tag must be a compound tag, but was: " + parsed.getClass());
        }
    }

    private Chunk(CompoundTag compoundTag) {
        this.chunkTag = compoundTag;
    }

    public static Chunk ofEmptyChunk () {
        return new Chunk(new CommonCompoundTag("") {
        });
    }

    public CompoundTag getChunkTag() {
        return chunkTag;
    }
}
