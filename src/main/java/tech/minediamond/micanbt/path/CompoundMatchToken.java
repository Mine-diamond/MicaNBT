package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public record CompoundMatchToken(CompoundTag pattern) implements PathToken {
    public Tag navigate(Tag container) {
        int idx = findIndex(container);
        return idx != -1 ? ((ListTag<?>) container).get(idx) : null;
    }

    public Object getAccessor(Tag container) {
        return findIndex(container);
    }

    private int findIndex(Tag container) {
        if (container instanceof ListTag<?> list && list.getElementTypeId() == CompoundTag.ID) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof CompoundTag target && target.equals(pattern)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public String asString() {
        return "[" + pattern.toString(false, SNBTStyle.COMPACT) + "]";
    }
}
