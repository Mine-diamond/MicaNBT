package tech.minediamond.micanbt.basic;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.tag.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ValueGetTest {

    @Test
    public void testCompoundTagGetKeyByValue() {
        CompoundTag compoundTag = getCompoundTag();
        assertNotNull(compoundTag);
        assertEquals("FloatTag", compoundTag.findKey(tag -> tag.getRawValue().equals(1.0f)));
        assertEquals("ByteArrayTag", compoundTag.findKey(tag -> Objects.deepEquals(tag.getRawValue(), new byte[]{1, 0, 3})));
        assertEquals("subCompoundTag", compoundTag.findKey(tag -> tag.getRawValue().equals(getSubCompoundTag().getRawValue())));

        assertNull(compoundTag.findKey(tag -> tag.getRawValue().equals(-1)));
    }

    @Test
    public void testCompoundTagGetTagByValue() {
        CompoundTag compoundTag = getCompoundTag();
        assertNotNull(compoundTag);
        assertEquals(new FloatTag("FloatTag", 1.0f), compoundTag.find(tag -> tag.getRawValue().equals(1.0f)));
        assertEquals(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}),
                compoundTag.find(tag -> Objects.deepEquals(tag.getRawValue(), new byte[]{1, 0, 3})));
        assertEquals(getSubCompoundTag(), compoundTag.find(tag -> tag.getRawValue().equals(getSubCompoundTag().getRawValue())));

        assertNull(compoundTag.find(tag -> tag.getRawValue().equals(-1)));
    }

    @Test
    public void testReorderableCompoundTagGetIndexByValue() {
        ReorderableCompoundTag reorderableCompoundTag = getReorderableCompoundTag();
        assertNotNull(reorderableCompoundTag);
        assertEquals(3, reorderableCompoundTag.indexOf(tag -> tag.getRawValue().equals(1.0f)));
        assertEquals(0, reorderableCompoundTag.indexOf(tag -> Objects.deepEquals(tag.getRawValue(), new byte[]{1, 0, 3})));
        assertEquals(11, reorderableCompoundTag.indexOf(tag -> tag.getRawValue().equals(getSubCompoundTag().getRawValue())));

        assertEquals(-1, reorderableCompoundTag.indexOf(tag -> tag.getRawValue().equals("ValueNotExist")));
    }

    @Test
    public void testListTagGetIndexByValue() {
        ListTag<StringTag> listTag = getListTag();
        assertNotNull(listTag);
        assertEquals(0, listTag.indexOf(tag -> tag.getRawValue().equals("str")));
        assertEquals(2, listTag.indexOf(tag -> tag.getRawValue().equals("str2")));
        assertEquals(4, listTag.indexOf(tag -> tag.getRawValue().equals("str4")));

        assertEquals(-1, listTag.indexOf(tag -> tag.getRawValue().equals("strNotExist")));
    }

    @Test
    public void testListTagGetTagByValue() {
        ListTag<StringTag> listTag = getListTag();
        assertNotNull(listTag);
        assertEquals(new StringTag("", "str"), listTag.find(tag -> tag.getRawValue().equals("str")));
        assertEquals(new StringTag("", "str2"), listTag.find(tag -> tag.getRawValue().equals("str2")));
        assertEquals(new StringTag("", "str4"), listTag.find(tag -> tag.getRawValue().equals("str4")));

        assertNull(listTag.find(tag -> tag.getRawValue().equals("strNotExist")));
    }

    public static CommonCompoundTag getCompoundTag() {
        CommonCompoundTag tag = new CommonCompoundTag("tag");
        addCompoundTagValue(tag);
        return tag;
    }

    public static ReorderableCompoundTag getReorderableCompoundTag() {
        ReorderableCompoundTag tag = new ReorderableCompoundTag("tag");
        addCompoundTagValue(tag);
        return tag;
    }

    public static void addCompoundTagValue(CompoundTag tag) {
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
    }

    public static CommonCompoundTag getSubCompoundTag() {
        CommonCompoundTag subCompoundTag = new CommonCompoundTag("subCompoundTag");
        subCompoundTag.put(new ByteArrayTag("ByteArrayTag", new byte[]{1, 0, 3}));
        subCompoundTag.put(new ByteTag("ByteTag", (byte) 2));

        return subCompoundTag;
    }

    public static ListTag<StringTag> getListTag() {
        ListTag<StringTag> listWithItem = new ListTag<>("ListWithItem");
        listWithItem.add(new StringTag("", "str"));
        listWithItem.add(new StringTag("", "str1"));
        listWithItem.add(new StringTag("", "str2"));
        listWithItem.add(new StringTag("", "str3"));
        listWithItem.add(new StringTag("", "str4"));

        return listWithItem;
    }
}
