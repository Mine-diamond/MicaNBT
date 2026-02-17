package tech.minediamond.micanbt.chunk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.core.CompoundSelection;
import tech.minediamond.micanbt.tag.CompoundTag;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class ChunkParseTest {
    @Test
    public void testChunkParse() throws IOException, ExecutionException, InterruptedException {
        try (InputStream resource = this.getClass().getResourceAsStream("/r.0.0.mca")) {
            Assertions.assertNotNull(resource);
            Region region = new Region(resource, true, CompoundSelection.COMMON_MAP);
            Assertions.assertNotNull(region);

            Chunk chunk = region.getChunk(0, 0);
            assertFalse(chunk.isCorrupt());
            assertTrue(chunk.isChunkInitialized());

            CompoundTag chunkTag = chunk.getChunkTag();
            assertEquals(-4, chunkTag.get("yPos").getRawValue());
            assertEquals(35520L, chunkTag.get("LastUpdate").getRawValue());
        }

    }
}
