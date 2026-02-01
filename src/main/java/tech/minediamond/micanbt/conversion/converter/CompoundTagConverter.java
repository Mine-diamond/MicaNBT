package tech.minediamond.micanbt.conversion.converter;

import tech.minediamond.micanbt.conversion.ConverterRegistry;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * A converter that converts between CompoundTag and Map.
 */
public class CompoundTagConverter implements TagConverter<CompoundTag, Map> {
    @Override
    public Map convert(CompoundTag tag) {
        Map<String, Object> ret = new HashMap<>();
        Map<String, Tag> tags = tag.getClonedValue();
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            ret.put(entry.getKey(), ConverterRegistry.convertToValue(entry.getValue()));
        }
        return ret;
    }

    @Override
    public CompoundTag convert(String name, Map value) {
        Map<String, Tag> tags = new HashMap<String, Tag>();
        for(Object na : value.keySet()) {
            String n = (String) na;
            tags.put(n, ConverterRegistry.convertToTag(n, value.get(n)));
        }

        return new CompoundTag(name, tags);
    }
}
