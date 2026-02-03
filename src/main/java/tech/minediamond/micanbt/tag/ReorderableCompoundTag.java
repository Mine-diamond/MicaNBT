package tech.minediamond.micanbt.tag;

import tech.minediamond.micanbt.tag.map.OrderedListMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ReorderableCompoundTag extends CompoundTag {
    OrderedListMap<String, Tag> value;

    public ReorderableCompoundTag(String name) {
        this(name, new OrderedListMap<>());
    }

    public ReorderableCompoundTag(String name, OrderedListMap<String, Tag> map) {
        super(name);
        this.value = map;
    }

    public ReorderableCompoundTag(String name, Map<String, Tag> map) {
        super(name);
        this.value = new OrderedListMap<>(map);
    }

    @Override
    public void setValue(Map<String, Tag> map) {
        this.value = new OrderedListMap<>(map);
    }

    public void setValue(OrderedListMap<String, Tag> map) {
        this.value = map;
    }

    @Override
    public void put(Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    public void put(int index, Tag tag) {
        this.value.put(tag.getName(), tag, index);
    }

    public void putAll(ReorderableCompoundTag other) {
        this.value.putAll(other.getRawValue());
    }

    @Override
    public Tag get(String tagName) {
        return this.value.get(tagName);
    }

    public Tag get(int index) {
        return this.value.get(index);
    }

    @Override
    public Tag getOrDefault(String tagName, Tag defaultTag) {
        return this.value.getOrDefault(tagName, defaultTag);
    }

    @Override
    public Tag computeIfAbsent(String key, Function<? super String, ? extends Tag> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    public Tag replaceAt(int index, Tag tag) {
        return this.value.replaceAt(index, tag.getName(), tag);
    }

    public Tag replaceAt(Tag oldTag, Tag newTag) {
        Objects.requireNonNull(oldTag, "tag to replace is null");
        return this.value.replaceAt(oldTag.getName(), newTag.getName(), newTag);
    }

    @Override
    public Tag remove(String tagName) {
        return this.value.remove(tagName);
    }

    @Override
    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public void swap(int fromIndex, int toIndex) {
        this.value.swap(fromIndex, toIndex);
    }

    public void moveTo(int fromIndex, int toIndex) {
        this.value.moveTo(fromIndex, toIndex);
    }

    public void moveTo(String tagName, int toIndex) {
        this.value.moveTo(tagName, toIndex);
    }

    public void sort(Comparator<String> comparator) {
        this.value.sort(comparator);
    }

    public int indexOf(String tagName) {
        return this.value.indexOf(tagName);
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
    public OrderedListMap<String, Tag> getClonedValue() {
        OrderedListMap<String, Tag> copy = new OrderedListMap<>();
        for (Tag tag : value) {
            copy.put(tag.getName(), tag);
        }
        return copy;
    }

    @Override
    public OrderedListMap<String, Tag> getRawValue() {
        return value;
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.value.iterator();
    }

    @Override
    public Tag copy() {
        return new ReorderableCompoundTag(getName(), getClonedValue());
    }
}
