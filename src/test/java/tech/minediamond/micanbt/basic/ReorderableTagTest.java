package tech.minediamond.micanbt.basic;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.tag.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReorderableTagTest {

    @Test
    public void testChangeName() {
        ReorderableCompoundTag compoundTag = buildTag();

        DoubleTag tag = (DoubleTag) compoundTag.get("DoubleTag");
        DoubleTag tag2 = new DoubleTag("NewDoubleTagName", tag.getRawValue());
        compoundTag.replace(tag, tag2);

        assertEquals(1.0, compoundTag.get(2).getRawValue());
        assertEquals(1.0, compoundTag.get("NewDoubleTagName").getRawValue());

        Tag tag3 = new FloatTag("NewFloatTagName", -1.0f);
        compoundTag.set(3, tag3);
        assertEquals(-1.0f, compoundTag.get(3).getRawValue());
        assertEquals(-1.0f, compoundTag.get("NewFloatTagName").getRawValue());
    }

    @Test
    public void testSwapValue() {
        ReorderableCompoundTag compoundTag = buildTag();

        compoundTag.swap(2, 4);
        assertArrayEquals(new int[]{1, 2, 3}, ((IntArrayTag) compoundTag.get(2)).getRawValue());
        assertEquals(1.0, compoundTag.get(4).getRawValue());
    }

    @Test
    public void testMoveValue() {
        ReorderableCompoundTag compoundTag = buildTag();

        compoundTag.moveTo(2, 4);
        assertEquals(1.0f, compoundTag.get(2).getRawValue());
        assertArrayEquals(new int[]{1, 2, 3}, ((IntArrayTag) compoundTag.get(3)).getRawValue());
        assertEquals(1.0, compoundTag.get(4).getRawValue());

        compoundTag.moveTo("LongArrayTag", 8);
        assertEquals(1L, compoundTag.get(6).getRawValue());
        assertEquals((short) 2, compoundTag.get(7).getRawValue());
        assertArrayEquals(new long[]{1, 2, 3}, ((LongArrayTag) compoundTag.get(8)).getRawValue());
    }

    public static ReorderableCompoundTag buildTag() {
        ReorderableCompoundTag tag = new ReorderableCompoundTag("tag");
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

        return tag;
    }
}
