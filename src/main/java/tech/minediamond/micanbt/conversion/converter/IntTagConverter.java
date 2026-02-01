package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.IntTag;

/**
 * A converter that converts between IntTag and int.
 */
public class IntTagConverter implements TagConverter<IntTag, Integer> {
    @Override
    public Integer convert(IntTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public IntTag convert(String name, Integer value) {
        return new IntTag(name, value);
    }
}
