package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public final class NBTFinder {

    private NBTFinder() {
    }

    public static Tag get(Tag root, NBTPath path) {
        if (root == null || path == null) return null;

        Tag current = root;
        Object[] tokens = path.getTokens();

        for (Object token : tokens) {
            if (current instanceof CompoundTag compoundTag) {
                current = compoundTag.get((String) token);
            } else if (current instanceof ListTag<?> list) {
                Integer index = (Integer) token;
                if (index < 0) {
                    index = list.size() + index;
                }
                if (index >= 0 && index < list.size()) {
                    current = list.get(index);
                } else {
                    return null;
                }
            } else {
                return null;
            }

            if (current == null) return null;
        }

        return current;
    }
}
