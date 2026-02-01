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
        String[] tokens = path.getTokens();

        for (String token : tokens) {
            if (current instanceof CompoundTag compound) {
                current = compound.get(token);
            }
            else if (current instanceof ListTag<?> list) {
                Integer index = tryParseInt(token);
                if (index != null && index >= 0 && index < list.size()) {
                    current = list.get(index);
                } else {
                    return null;
                }
            }
            else {
                return null;
            }

            if (current == null) return null;
        }

        return current;
    }

    private static Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
