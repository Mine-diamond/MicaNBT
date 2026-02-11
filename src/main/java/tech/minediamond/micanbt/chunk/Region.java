package tech.minediamond.micanbt.chunk;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;


public class Region {
    public static final int SECTOR_LENGTH = 4 * 1024;

    Path path;
    byte[] data;
    ChunkLocation[] chunkLocations;
    int[] timestamps;
    Chunk[] chunks = new Chunk[1024];

    public Region(Path path, boolean preLoadChunk) throws IOException {
        this.path = path;
        data = Files.readAllBytes(path);
        chunkLocations = getChunkLocations(Arrays.copyOfRange(data, 0, SECTOR_LENGTH));
        timestamps = getTimestamps(Arrays.copyOfRange(data, SECTOR_LENGTH, SECTOR_LENGTH * 2));
        if (preLoadChunk) {

            int threads = Runtime.getRuntime().availableProcessors() / 2;
            ForkJoinPool customPool = new ForkJoinPool(threads);
            try {
                customPool.submit(() -> {
                    IntStream.range(0, 1024).parallel().forEach(i -> {
                        try {
                            chunks[i] = parseChunk(i);
                        } catch (IOException e) {
                            throw new RuntimeException(e); // 之后再改
                        }
                    });
                }).get();
            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                customPool.shutdown();
            }
        }
        System.out.println("Region initialled");
    }

    private ChunkLocation[] getChunkLocations(byte[] header) {
        ChunkLocation[] chunkLocations = new ChunkLocation[1024];
        for (int i = 0; i < 4 * 1024; i += 4) {
            int offset = ((header[i] & 0xFF) << 16) + ((header[i + 1] & 0xFF) << 8) + (header[i + 2] & 0xFF);
            int size = header[i + 3];
            chunkLocations[i >> 2] = new ChunkLocation(offset, size);
        }
        return chunkLocations;
    }

    private int[] getTimestamps(byte[] TimestampsData) {
        int[] timestamps = new int[1024];
        for (int i = 0; i < 4 * 1024; i += 4) {
            int timestamp = ((TimestampsData[i] & 0xFF) << 24) + ((TimestampsData[i + 1] & 0xFF) << 16) + ((TimestampsData[i + 2] & 0xFF) << 8) + (TimestampsData[i + 3] & 0xFF);
            timestamps[i >> 2] = timestamp;
        }
        return timestamps;
    }

    public Chunk getChunk(int x, int z) {
        int index = ((z & 31) << 5) | (x & 31);
        if (chunks[index] == null) {
            try {
                chunks[index] = parseChunk(index);
            } catch (IOException e) {
                throw new RuntimeException(e); // 之后再改
            }
        }
        return chunks[index];
    }

    private Chunk parseChunk(int i) throws IOException {
        //System.out.println("processing chunk now: " + i);
        int offset = chunkLocations[i].offset * SECTOR_LENGTH;
        if (offset == 0) {
            return Chunk.ofEmptyChunk();
        }
        byte[] chunkHeader;
        chunkHeader = Arrays.copyOfRange(data, offset, offset + 5);
        int chunkLength = ((chunkHeader[0] & 0xff) << 24)
                + ((chunkHeader[1] & 0xff) << 16)
                + ((chunkHeader[2] & 0xff) << 8)
                + (chunkHeader[3] & 0xff);
        //System.out.println("offset: " + offset + ", offset sector: " + offset/SECTOR_LENGTH + ", chunkLength: " + chunkLength + ", (offset + chunkLength - 1): " + (offset + chunkLength - 1));
        int payloadOffset = offset + 5;
        int payloadLength = chunkLength - 1;
        InputStream input = new ByteArrayInputStream(data, payloadOffset, payloadLength);
        switch (chunkHeader[4]) {
            case 0x01 -> // GZip
                    input = new GZIPInputStream(input);
            case 0x02 -> { // Zlib
                input = new InflaterInputStream(input);
            }
            case 0x03 -> { // Uncompressed
            }
            default ->
                    throw new IOException("Unsupported compression method: " + Integer.toHexString(chunkHeader[4] & 0xff));
        }
        try (DataInputStream dis = new DataInputStream(input)) {
            return new Chunk(dis);
        }
    }

    public record ChunkLocation(int offset, int size) {
    }

}
