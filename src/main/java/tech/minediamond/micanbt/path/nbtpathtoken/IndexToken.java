package tech.minediamond.micanbt.path.nbtpathtoken;

import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public record IndexToken(int index) implements PathToken {
    public Tag navigate(Tag container) {
        if (container instanceof ListTag<?> listTag) {
            int listIndex = index >= 0 ? index : listTag.size() + index;
            return listIndex < listTag.size() ? listTag.get(listIndex) : null;
        }
        return null;
    }

    public Object getAccessor(Tag container) {
        return index;
    }

    public String asString() {
        return "[" + index + "]";
    }
}
