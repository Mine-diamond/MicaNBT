package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.ShortTag;

/**
 * A converter that converts between ShortTag and short.
 */
public class ShortTagConverter implements TagConverter<ShortTag, Short> {
    @Override
    public Short convert(ShortTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public ShortTag convert(String name, Short value) {
        return new ShortTag(name, value);
    }
}
