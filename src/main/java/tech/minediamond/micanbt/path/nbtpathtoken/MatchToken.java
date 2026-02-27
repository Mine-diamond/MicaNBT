package tech.minediamond.micanbt.path.nbtpathtoken;

import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public record MatchToken(Tag pattern) implements PathToken{

    @Override
    public Tag navigate(Tag container) {
        int idx = findIndex(container);
        return idx != -1 ? ((ListTag<?>) container).get(idx) : null;
    }

    @Override
    public Object getAccessor(Tag container) {
        return findIndex(container);
    }

    private int findIndex(Tag container) {
        if (container instanceof ListTag<?> list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(pattern)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String asString() {
        return "[" + pattern.toString(false, SNBTStyle.COMPACT) + "]";
    }
}
