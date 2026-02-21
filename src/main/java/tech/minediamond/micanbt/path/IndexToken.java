package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public record IndexToken(int index) implements PathToken {
    public Tag navigate(Tag container) {
        return (container instanceof ListTag<?> l && index < l.size()) ? l.get(index) : null;
    }
    public Object getAccessor(Tag container) { return index; }
    public String asString() { return "[" + index + "]"; }
}
