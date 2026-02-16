package tech.minediamond.micanbt.chunk;

import tech.minediamond.micanbt.core.CompoundSelection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


public class Region {
    public static final int CHUNKS_PER_REGION = 1024;
    public static final int SECTOR_LENGTH = 4 * 1024;

    Path path;
    byte[] data;
    ChunkLocation[] chunkLocations;
    int[] timestamps;
    Chunk[] chunks = new Chunk[CHUNKS_PER_REGION];

    CompoundSelection compoundSelection;

    private static final ThreadLocal<Inflater> INFLATER_HOLDER =
            ThreadLocal.withInitial(Inflater::new);
    private static final ForkJoinPool CHUNK_PARSER_EXECUTOR = new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 2);
    private final Object[] locks = new Object[64];

    public Region(byte[] data, boolean preLoadChunk, CompoundSelection compoundSelection) throws ExecutionException, InterruptedException {
        this.data = data;
        this.compoundSelection = compoundSelection;
        initialize(preLoadChunk);
    }

    public Region(Path path, boolean preLoadChunk, CompoundSelection compoundSelection) throws IOException, InterruptedException, ExecutionException {
        this.path = path;
        this.compoundSelection = compoundSelection;
        data = Files.readAllBytes(path);
        initialize(preLoadChunk);
    }

    public Region(InputStream stream, boolean preLoadChunk, CompoundSelection compoundSelection) throws IOException, InterruptedException, ExecutionException {
        data = stream.readAllBytes();
        this.compoundSelection = compoundSelection;
        initialize(preLoadChunk);
    }

    private void initialize(boolean preLoadChunk) throws ExecutionException, InterruptedException {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
        chunkLocations = getChunkLocations();
        timestamps = getTimestamps();
        if (preLoadChunk) {
            CHUNK_PARSER_EXECUTOR.submit(() -> IntStream.range(0, CHUNKS_PER_REGION).parallel()
                    .forEach(i -> chunks[i] = parseChunk(i))).get();
        }
    }

    private ChunkLocation[] getChunkLocations() {
        ChunkLocation[] chunkLocations = new ChunkLocation[CHUNKS_PER_REGION];
        for (int i = 0; i < SECTOR_LENGTH; i += 4) {
            int offset = ((data[i] & 0xFF) << 16) | ((data[i + 1] & 0xFF) << 8) | (data[i + 2] & 0xFF);
            byte size = data[i + 3];
            chunkLocations[i >> 2] = new ChunkLocation(offset, size);
        }
        return chunkLocations;
    }

    private int[] getTimestamps() {
        int[] timestamps = new int[CHUNKS_PER_REGION];
        for (int i = SECTOR_LENGTH; i < 2 * SECTOR_LENGTH; i += 4) {
            int timestamp = ((data[i] & 0xFF) << 24) | ((data[i + 1] & 0xFF) << 16) | ((data[i + 2] & 0xFF) << 8) | (data[i + 3] & 0xFF);
            timestamps[(i - SECTOR_LENGTH) >> 2] = timestamp;
        }
        return timestamps;
    }

    public Chunk getChunk(int x, int z) {
        int index = ((z & 31) << 5) | (x & 31);
        if (chunks[index] == null) {
            synchronized (locks[index % locks.length]) {
                if (chunks[index] == null) {
                    chunks[index] = parseChunk(index);
                }
            }
        }
        return chunks[index];
    }

    private Chunk parseChunk(int i) {
        int offset = chunkLocations[i].offset * SECTOR_LENGTH;
        Chunk.ChunkPos chunkPos = new Chunk.ChunkPos((i & 31), ((i >> 5) & 31));
        if (offset == 0) {
            return Chunk.ofUninitialized(chunkPos, this);
        }
        try {
            InputStream input = getByteArrayInputStream(offset);
            switch (data[offset + 4]) {
                case 0x01 -> // GZip
                        input = new GZIPInputStream(input);
                case 0x02 -> { // Zlib
                    Inflater inflater = INFLATER_HOLDER.get();
                    inflater.reset();
                    input = new InflaterInputStream(input, inflater);
                }
                case 0x03 -> { // Uncompressed
                }
                default ->
                        throw new IOException("Unsupported compression method: " + Integer.toHexString(data[offset + 4] & 0xff));
            }
            try (DataInputStream dis = new DataInputStream(input)) {
                return Chunk.of(dis, compoundSelection, timestamps[i], chunkPos, this);
            }
        } catch (IOException e) {
            return Chunk.ofCorrupt(timestamps[i], chunkPos, e, this);
        }
    }

    private InputStream getByteArrayInputStream(int offset) {
        int chunkLength = ((data[offset] & 0xff) << 24)
                + ((data[offset + 1] & 0xff) << 16)
                + ((data[offset + 2] & 0xff) << 8)
                + (data[offset + 3] & 0xff);
        int payloadOffset = offset + 5;
        int payloadLength = chunkLength - 1;
        return new ByteArrayInputStream(data, payloadOffset, payloadLength);
    }

    public record ChunkLocation(int offset, byte size) {
    }
}
