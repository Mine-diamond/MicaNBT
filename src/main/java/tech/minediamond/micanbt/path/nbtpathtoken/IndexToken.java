package tech.minediamond.micanbt.path.nbtpathtoken;

import org.jetbrains.annotations.Nullable;
import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public record IndexToken(int index) implements PathToken {
    public @Nullable Tag navigate(Tag container) {
        if (container instanceof ListTag<?> listTag) {
            int listIndex = index >= 0 ? index : listTag.size() + index;
            return listIndex < listTag.size() && listIndex >= 0 ? listTag.get(listIndex) : null;
        }
        return null;
    }

    public Integer getAccessor(Tag container) {
        if (container instanceof ListTag<?> listTag) {
            int listIndex = index >= 0 ? index : listTag.size() + index;
            return listIndex < listTag.size() && listIndex >= 0 ? listIndex : -1;
        }
        return -1;
    }
}
