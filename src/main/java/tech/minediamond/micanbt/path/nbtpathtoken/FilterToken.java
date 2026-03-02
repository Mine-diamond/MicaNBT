package tech.minediamond.micanbt.path.nbtpathtoken;

import org.jetbrains.annotations.Nullable;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

public record FilterToken(CompoundTag pattern) implements PathToken {
    @Override
    public @Nullable Tag navigate(Tag container) {
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
    public boolean isModifier() {
        return true;
    }
}
