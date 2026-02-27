package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.path.nbtpathtoken.PathToken;
import tech.minediamond.micanbt.tag.Tag;

public class NBTFinder2 {
    private NBTFinder2() {
    }

    public static Tag get(Tag root, String path) {
        return get(root, NBTPath2.of(path));
    }

    public static Tag findFirst(Tag root, String... paths) {
        for (String path : paths) {
            Tag tag = get(root, path);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }

    public static Tag findFirst(Tag root, NBTPath2... paths) {
        for (NBTPath2 path : paths) {
            Tag tag = get(root, path);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }

    public static Tag get(Tag root, NBTPath2 path) {
        PathToken[] tokens = path.getTokens();
        if (root == null ||  tokens == null) {
            return null;
        }
        Tag current = root;

        for (PathToken token : tokens) {
            current = token.navigate(current);
            if (current == null) {
                return null;
            }
        }

        return current;
    }

}
