package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

public record KeyToken(String key) implements PathToken {
    public Tag navigate(Tag container) {
        return (container instanceof CompoundTag c) ? c.get(key) : null;
    }
    public Object getAccessor(Tag container) { return key; }
    public String asString() { return key; }
}
