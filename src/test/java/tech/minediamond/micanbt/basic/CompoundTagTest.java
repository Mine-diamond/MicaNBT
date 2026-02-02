package tech.minediamond.micanbt.basic;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.tag.*;

import static org.junit.jupiter.api.Assertions.*;

public class CompoundTagTest {

    @Test
    public void testValue() {
        CompoundTag tag = buildTag();
        assertEquals((byte) 2, tag.get("ByteTag").getRawValue());
    }

    public static CompoundTag buildTag() {
        CompoundTag tag = new CompoundTag("tag");
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

        CompoundTag subCompoundTag = new CompoundTag("subCompoundTag");
        subCompoundTag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        subCompoundTag.put(new ByteTag("ByteTag", (byte) 2));

        ListTag<StringTag> listWithItem = new ListTag<>("ListWithItem");
        listWithItem.add(new StringTag("StringTagInListTag", "str"));
        listWithItem.add(new StringTag("StringTagInListTag", "str1"));
        listWithItem.add(new StringTag("StringTagInListTag", "str2"));

        tag.put(new CompoundTag("subEmptyCompoundTag"));
        tag.put(subCompoundTag);
        tag.put(listWithItem);
        tag.put(new StringTag("Name With Space", "add a \" here "));
        tag.put(new IntArrayTag("EmptyIntArrayTag", new int[]{}));

        return tag;
    }
}
