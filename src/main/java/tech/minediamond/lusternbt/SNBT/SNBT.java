package tech.minediamond.lusternbt.SNBT;

import tech.minediamond.lusternbt.tag.builtin.Tag;

import java.io.IOException;
import java.nio.file.Path;

public class SNBT {
    private SNBT() {
    }

    public static String serialize(Tag tag, boolean linebreak) {
        return new SNBTWriter(tag, linebreak).getSNBTString();
    }

    public static void writeAsSNBT(Tag tag, Path path, boolean linebreak) throws IOException {
        new SNBTWriter(tag, linebreak).writeSNBT(path);
    }

    public static Tag deserialize(String SNBTText) {
        return new SNBTReader(SNBTText).getTag();
    }

    public static Tag readSNBT(Path path) throws IOException {
        return new SNBTReader(path).getTag();
    }
}
