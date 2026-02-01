package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.LongArrayTag;

/**
 * A converter that converts between LongArrayTag and long[].
 */
public class LongArrayTagConverter implements TagConverter<LongArrayTag, long[]> {
    @Override
    public long[] convert(LongArrayTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public LongArrayTag convert(String name, long[] value) {
        return new LongArrayTag(name, value);
    }
}
