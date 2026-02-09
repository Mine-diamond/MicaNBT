package tech.minediamond.micanbt.tag;

import java.util.*;
import java.util.Map.Entry;

/**
 * A standard implementation of {@link CompoundTag} backed by a {@link LinkedHashMap}.
 * This implementation preserves the order in which tags are added.
 */
public class CommonCompoundTag extends CompoundTag {
    private Map<String, Tag> value;

    public CommonCompoundTag(String name) {
        this(name, new LinkedHashMap<>());
    }

    public CommonCompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = new LinkedHashMap<>(value);
    }

    @Override
    public void setValue(Map<String, Tag> value) {
        this.value = new LinkedHashMap<>(value);
    }

    @Override
    public void put(Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    /**
     * Adds all tags from another compound tag into this one.
     * Existing tags with the same name will be overwritten.
     *
     * @param other The compound tag to copy data from.
     */
    public void putAll(CommonCompoundTag other) {
        this.value.putAll(other.getRawValue());
    }

    @Override
    public Tag get(String tagName) {
        return this.value.get(tagName);
    }

    @Override
    public Tag getOrDefault(String key, Tag defaultValue) {
        return this.value.getOrDefault(key, defaultValue);
    }

    @Override
    public Tag computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Tag> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Tag remove(String tagName) {
        return this.value.remove(tagName);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    /**
     * Returns a view of all tags contained in this compound.
     *
     * @return A collection of tags.
     */
    public Collection<Tag> values() {
        return this.value.values();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.values().iterator();
    }

    @Override
    public Map<String, Tag> getClonedValue() {
        Map<String, Tag> clonedMap = new LinkedHashMap<>(Math.max((int) (this.value.size() / .75f) + 1, 16));
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().copy());
        }
        return clonedMap;
    }

    @Override
    public Map<String, Tag> getRawValue() {
        return this.value;
    }

    @Override
    public CommonCompoundTag copy() {
        Map<String, Tag> newMap = new LinkedHashMap<>(Math.max((int) (this.value.size() / .75f) + 1, 16));
        for (Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().copy());
        }

        return new CommonCompoundTag(this.getName(), newMap);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && value.equals(((CommonCompoundTag) o).value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(value);
    }
}
