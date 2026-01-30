package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.ByteTag;

/**
 * A converter that converts between ByteTag and byte.
 */
public class ByteTagConverter implements TagConverter<ByteTag, Byte> {
    @Override
    public Byte convert(ByteTag tag) {
        return tag.getValue();
    }

    @Override
    public ByteTag convert(String name, Byte value) {
        return new ByteTag(name, value);
    }
}
