package tech.minediamond.micanbt.roundtrip;

import tech.minediamond.micanbt.tag.*;

public class Util {

    public static CommonCompoundTag getBasicTag() {
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
