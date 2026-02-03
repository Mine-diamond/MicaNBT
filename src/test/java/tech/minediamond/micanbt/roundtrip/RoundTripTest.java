package tech.minediamond.micanbt.roundtrip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.minediamond.micanbt.NBT.NBT;
import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class RoundTripTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSNBTRoundTrip() {
        Tag tag = buildTag();
        Tag newTag = SNBT.parse(SNBT.stringify(tag, true, SNBTStyle.COMPACT));
        assertInstanceOf(CommonCompoundTag.class, newTag);

        assertEquals(tag, newTag);
    }

    @Test
    public void testNBTRoundTrip() throws IOException {
        Path filePath = tempDir.resolve("test.nbt");

        CompoundTag tag = buildTag();
        NBT.write(tag, filePath);
        CompoundTag read = NBT.read(filePath);
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
             ; GZIPInputStream gzipInputStream = new GZIPInputStream(buffer)) {
            parsed = NBT.parse(gzipInputStream);
        }
        NBT.write((CompoundTag) parsed, filePath, false, false);
        parsedBytes = Files.readAllBytes(filePath);
        assertArrayEquals(originalBytes, parsedBytes);
    }

    @Test
    public void testSNBTFileRoundTrip() throws IOException {
        Path filePath = tempDir.resolve("leveldat.snbt");
        String originalString;
        String parsedString;

        try (InputStream inputStream = this.getClass().getResourceAsStream("/leveldat.snbt")) {
            originalString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        SNBT.write(SNBT.parse(originalString), false, filePath, SNBTStyle.COMPACT);
        parsedString = Files.readString(filePath, StandardCharsets.UTF_8);

        assertEquals(originalString, parsedString);
    }

    public static CommonCompoundTag buildTag() {
        CommonCompoundTag tag = new CommonCompoundTag("tag");
        tag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        tag.put(new ByteTag("ByteTag", (byte) 2));
        tag.put(new DoubleTag("DoubleTag", 1.0));
        tag.put(new FloatTag("FloatTag", 1.0f));
        tag.put(new IntArrayTag("IntArrayTag", new int[]{1, 2, 3}));
        tag.put(new IntTag("IntTag", 1));
        tag.put(new LongArrayTag("LongArrayTag", new long[]{1, 2, 3}));
        tag.put(new LongTag("LongTag", 1L));
        tag.put(new ShortTag("ShortTag", (short) 2));
        tag.put(new StringTag("StringTag", "str"));

        CommonCompoundTag subCompoundTag = new CommonCompoundTag("subCompoundTag");
        subCompoundTag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        subCompoundTag.put(new ByteTag("ByteTag", (byte) 2));

        ListTag<StringTag> listWithItem = new ListTag<>("ListWithItem");
        listWithItem.add(new StringTag("", "str"));
        listWithItem.add(new StringTag("", "str1"));
        listWithItem.add(new StringTag("", "str2"));

        tag.put(new CommonCompoundTag("subEmptyCompoundTag"));
        tag.put(subCompoundTag);
        tag.put(listWithItem);
        tag.put(new StringTag("Name With Space", "add a \" here "));
        tag.put(new IntArrayTag("EmptyIntArrayTag", new int[]{}));

        return tag;
    }
}
