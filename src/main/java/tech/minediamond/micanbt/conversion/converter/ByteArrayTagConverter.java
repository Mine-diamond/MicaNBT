package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.ByteArrayTag;

/**
 * A converter that converts between ByteArrayTag and byte[].
 */
public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
    @Override
    public byte[] convert(ByteArrayTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public ByteArrayTag convert(String name, byte[] value) {
        return new ByteArrayTag(name, value);
    }
}
