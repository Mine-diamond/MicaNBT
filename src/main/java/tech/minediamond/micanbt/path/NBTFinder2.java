package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.Tag;

public class NBTFinder2 {
    private NBTFinder2() {
    }

    public static Tag get(Tag root, PathToken[] tokens) {
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
