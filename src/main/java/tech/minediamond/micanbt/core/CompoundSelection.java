package tech.minediamond.micanbt.core;

import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.ReorderableCompoundTag;

/**
 * Defines the implementation strategy for {@link tech.minediamond.micanbt.tag.CompoundTag}
 * when parsing SNBT data.
 */
public enum CompoundSelection {
    /**
     * Uses a standard implementation (typically {@code CommonCompoundTag})
     * which maintains insertion order via {@code LinkedHashMap}.
     *
     * @see CommonCompoundTag
     */
    COMMON_MAP,

    /**
     * Uses a reorderable implementation (typically {@code ReorderableCompoundTag})
     * which allows for manual index-based manipulation and sorting of entries.
     *
     * @see ReorderableCompoundTag
     */
    REORDERABLE_MAP
}