package tech.minediamond.micanbt.roundtrip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.NBT.NBTCompressType;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class NBTRoundTripTest {

    @TempDir
    Path tempDir;

    @Test
    public void testNBTRoundTrip() throws IOException {
        Path filePath = tempDir.resolve("test.nbt");

        CompoundTag tag = Util.getBasicTag();
        NBT.write(tag, filePath);
        CompoundTag read = (CompoundTag) NBT.fromPath(filePath).getTag();
        assertInstanceOf(CompoundTag.class, read);

        assertEquals(tag, read);
    }

    @Test
    public void testNBTFileRoundTrip() throws IOException {
        Path filePath = tempDir.resolve("level.dat");
        byte[] originalBytes;
        Tag parsed;
        byte[] parsedBytes;
        try (InputStream inputStream = this.getClass().getResourceAsStream("/level.dat")
             ; InputStream buffer = new BufferedInputStream(inputStream)
             ; GZIPInputStream gzipInputStream = new GZIPInputStream(buffer)) {
            originalBytes = gzipInputStream.readAllBytes();
        }

        try (InputStream inputStream = this.getClass().getResourceAsStream("/level.dat")
             ; InputStream buffer = new BufferedInputStream(inputStream)
             ; GZIPInputStream gzipInputStream = new GZIPInputStream(buffer)
             ; DataInputStream dataInput = new DataInputStream(gzipInputStream)) {
            parsed = NBT.fromDataInput(dataInput).getTag();
        }
        NBT.write((CompoundTag) parsed, filePath, NBTCompressType.UNCOMPRESSED, false);
        parsedBytes = Files.readAllBytes(filePath);
        assertArrayEquals(originalBytes, parsedBytes);
    }

}
