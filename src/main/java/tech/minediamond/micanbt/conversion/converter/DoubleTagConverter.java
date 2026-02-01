package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.tag.DoubleTag;

/**
 * A converter that converts between DoubleTag and double.
 */
public class DoubleTagConverter implements TagConverter<DoubleTag, Double> {
    @Override
    public Double convert(DoubleTag tag) {
        return tag.getClonedValue();
    }

    @Override
    public DoubleTag convert(String name, Double value) {
        return new DoubleTag(name, value);
    }
}
