package tech.minediamond.micanbt.roundtrip;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SNBTRoundTripTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSNBTRoundTrip() {
        Tag tag = Util.getBasicTag();
        Tag newTag = SNBT.parse(SNBT.stringify(tag, true, SNBTStyle.COMPACT));
        assertInstanceOf(CommonCompoundTag.class, newTag);

        assertEquals(tag, newTag);
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
}
