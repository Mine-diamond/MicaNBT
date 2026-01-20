package tech.minediamond.lusternbt.conversion.builtin;

import tech.minediamond.lusternbt.conversion.TagConverter;
import tech.minediamond.lusternbt.tag.builtin.LongArrayTag;

/**
 * A converter that converts between LongArrayTag and long[].
 */
public class LongArrayTagConverter implements TagConverter<LongArrayTag, long[]> {
    @Override
    public long[] convert(LongArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public LongArrayTag convert(String name, long[] value) {
        return new LongArrayTag(name, value);
    }
}
