package tech.minediamond.micanbt.path.nbtpathtoken;

import tech.minediamond.micanbt.tag.Tag;

public interface PathToken {
    Tag navigate(Tag container);
    Object getAccessor(Tag container);
    default boolean isModifier() {
        return false;
    }
}
