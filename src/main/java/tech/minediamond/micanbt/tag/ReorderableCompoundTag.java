package tech.minediamond.micanbt.tag;

import org.jetbrains.annotations.NotNull;
import tech.minediamond.micanbt.util.map.OrderedListMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/// Extended [CompoundTag] supporting indexed access and manual reordering.
///
/// Unlike standard compounds, this implementation treats the tag collection as both
/// a map and a list.
///
/// This class provides [#set(int,Tag)] and [#replace(Tag,Tag)] to perform
/// **in-place updates**, allowing a tag's name to be changed without altering its
/// position. Some position-based operations such as [#swap], [#moveTo],
/// [#sort(Comparator)] are also provided.
public class ReorderableCompoundTag extends CompoundTag {
    OrderedListMap<String, Tag> value;

    /// Creates a tag with blank name.
    public ReorderableCompoundTag() {
        this("");
    }

    /// Creates a tag with the specified name.
    ///
    /// @param name The name of the tag.
    public ReorderableCompoundTag(@NotNull String name) {
        this(name, new OrderedListMap<>());
    }

    /// Creates a tag with the specified name and value.
    ///
    /// @param name  The name of the tag.
    /// @param value The value of the tag.
    public ReorderableCompoundTag(@NotNull String name, @NotNull OrderedListMap<String, Tag> value) {
        super(name);
        this.value = value;
    }

    public ReorderableCompoundTag(@NotNull String name, @NotNull Map<String, Tag> map) {
        super(name);
        this.value = new OrderedListMap<>(map);
    }

    @Override
    public void setValue(@NotNull Map<String, Tag> map) {
        this.value = new OrderedListMap<>(map);
    }

    /// Sets the internal map to the provided [OrderedListMap].
    ///
    /// @param map The new ordered map.
    public void setValue(@NotNull OrderedListMap<String, Tag> map) {
        this.value = map;
    }

    @Override
    public void put(@NotNull Tag tag) {
        this.value.put(tag.getName(), tag);
    }

    /// Inserts a tag at a specific index.
    ///
    /// @param index The target position.
    /// @param tag   The tag to insert.
    public void put(int index, @NotNull Tag tag) {
        this.value.put(tag.getName(), tag, index);
    }

    /// Adds all tags from another reorderable compound tag.
    ///
    /// @param other The source tag.
    public void putAll(@NotNull ReorderableCompoundTag other) {
        this.value.putAll(other.getRawValue());
    }

    /// Replaces the tag at a specific index with a new tag.
    ///
    /// @param index The index to replace.
    /// @param tag   The new tag.
    /// @return The old tag that was replaced.
    public Tag set(int index, @NotNull Tag tag) {
        return this.value.replaceAt(index, tag.getName(), tag);
    }

    /// Replaces an existing tag with a new one. The tag to be replaced is identified by the name of oldTag. The position of the tag remains unchanged.
    ///
    /// @param oldTag The tag to be replaced.
    /// @param newTag The new tag.
    /// @return The old tag that was replaced.
    public Tag replace(@NotNull Tag oldTag, @NotNull Tag newTag) {
        Objects.requireNonNull(oldTag, "tag to replace is null");
        return this.value.replaceAt(oldTag.getName(), newTag.getName(), newTag);
    }

    @Override
    public Tag get(@NotNull String tagName) {
        return this.value.get(tagName);
    }

    /// Retrieves the tag at the specified index.
    ///
    /// @param index The index of the tag.
    /// @return The tag at that index.
    public Tag get(int index) {
        return this.value.get(index);
    }

    @Override
    public @NotNull Tag getOrDefault(@NotNull String tagName, @NotNull Tag defaultTag) {
        return this.value.getOrDefault(tagName, defaultTag);
    }

    @Override
    public @NotNull Tag computeIfAbsent(@NotNull String key, @NotNull Function<? super String, ? extends Tag> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    public int indexOf(Predicate<? super Tag> predicate) {
        for (int i = 0; i < value.size(); i++) {
            if (predicate.test(value.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Tag remove(@NotNull String tagName) {
        return this.value.remove(tagName);
    }

    @Override
    public boolean contains(@NotNull String tagName) {
        return this.value.containsKey(tagName);
    }

    @Override
    public boolean contains(@NotNull Tag tag) {
        return this.value.containsValue(tag);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    /// Swaps the positions of two tags within the compound.
    ///
    /// @param fromIndex The first index.
    /// @param toIndex   The second index.
    public void swap(int fromIndex, int toIndex) {
        this.value.swap(fromIndex, toIndex);
    }

    /// Moves a tag from one index to another.
    ///
    /// @param fromIndex The current index.
    /// @param toIndex   The target index.
    public void moveTo(int fromIndex, int toIndex) {
        this.value.moveTo(fromIndex, toIndex);
    }

    /// Moves a tag with a specific name to a new index.
    ///
    /// @param tagName The name of the tag to move.
    /// @param toIndex The target index.
    public void moveTo(String tagName, int toIndex) {
        this.value.moveTo(tagName, toIndex);
    }

    /// Sorts the tags in this compound using the provided comparator for the tag names.
    ///
    /// @param comparator The comparator to define the order.
    public void sort(Comparator<String> comparator) {
        this.value.sort(comparator);
    }

    /// Finds the index of a tag by its name.
    ///
    /// @param tagName The name of the tag.
    /// @return The index, or -1 if not found.
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
    public @NotNull OrderedListMap<String, Tag> getClonedValue() {
        OrderedListMap<String, Tag> copy = new OrderedListMap<>();
        for (Tag tag : value) {
            copy.put(tag.getName(), tag);
        }
        return copy;
    }

    @Override
    public @NotNull OrderedListMap<String, Tag> getRawValue() {
        return value;
    }

    @Override
    public @NotNull Iterator<Tag> iterator() {
        return this.value.iterator();
    }

    @Override
    public @NotNull ReorderableCompoundTag copy() {
        return new ReorderableCompoundTag(getName(), getClonedValue());
    }
}
