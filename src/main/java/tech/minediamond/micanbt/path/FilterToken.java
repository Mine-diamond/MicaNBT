package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.SNBT.SNBTStyle;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

public record FilterToken(CompoundTag pattern) implements PathToken {
    @Override
    public Tag navigate(Tag container) {
        if (container instanceof CompoundTag compoundTag) {
            for (Tag tag : pattern) {
                if (!compoundTag.contains(tag)) {
                    return null;
                }
            }
            return compoundTag;
        }
        return null;
    }

    @Override
    public Object getAccessor(Tag container) {
        return container;
    }

    @Override
    public String asString() {
        return pattern.toString(false, SNBTStyle.COMPACT);
    }
}
