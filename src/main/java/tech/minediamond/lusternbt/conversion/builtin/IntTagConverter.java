package tech.minediamond.lusternbt.conversion.builtin;

import tech.minediamond.lusternbt.conversion.TagConverter;
import tech.minediamond.lusternbt.tag.builtin.IntTag;

/**
 * A converter that converts between IntTag and int.
 */
public class IntTagConverter implements TagConverter<IntTag, Integer> {
    @Override
    public Integer convert(IntTag tag) {
        return tag.getValue();
    }

    @Override
    public IntTag convert(String name, Integer value) {
        return new IntTag(name, value);
    }
}
