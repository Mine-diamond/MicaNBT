package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.IntArrayTag;

/**
 * A converter that converts between IntArrayTag and int[].
 */
public class IntArrayTagConverter implements TagConverter<IntArrayTag, int[]> {
    @Override
    public int[] convert(IntArrayTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public IntArrayTag convert(String name, int[] value) {
        return new IntArrayTag(name, value);
    }
}
