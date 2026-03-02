package tech.minediamond.micanbt.path.nbtpathtoken;

import org.jetbrains.annotations.Nullable;
import tech.minediamond.micanbt.tag.Tag;

public interface PathToken {
    @Nullable Tag navigate(Tag container);
    Object getAccessor(Tag container);
    default boolean isModifier() {
        return false;
    }
}
