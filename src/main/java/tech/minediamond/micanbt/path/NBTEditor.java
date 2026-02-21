package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.ListTag;
import tech.minediamond.micanbt.tag.Tag;

public class NBTEditor {
    /**
     * 将标签从 sourcePath 移动到 targetPath。
     * 如果 targetPath 指向的是一个 ListTag，则追加到末尾；
     * 如果 targetPath 是一个 CompoundTag 下的新 Key，则执行重命名并放入。
     */
    public static boolean move(Tag root, NBTPath sourcePath, NBTPath targetPath) {
        TagToMove info = detach(root, sourcePath);
        if (info == null) return false;
        return attach(root, targetPath, info.tag);
    }

    private record TagToMove(Tag tag, Object lastToken) {}

    private static TagToMove detach(Tag root, NBTPath path) {
        Object[] tokens = path.getTokens();
        if (tokens.length == 0) return null;

        Object[] parentTokens = new Object[tokens.length - 1];
        System.arraycopy(tokens, 0, parentTokens, 0, tokens.length - 1);
        Tag parent = NBTFinder.get(root, NBTPath.fromParts(parentTokens));
        Object lastToken = tokens[tokens.length - 1];

        Tag removed = null;
        if (parent instanceof CompoundTag compound && lastToken instanceof String key) {
            removed = compound.remove(key);
        } else if (parent instanceof ListTag<?> list && lastToken instanceof Integer index) {
            removed = list.remove(index);
        }
        return removed != null ? new TagToMove(removed, lastToken) : null;
    }

    private static boolean attach(Tag root, NBTPath path, Tag tag) {
        Tag parent = NBTFinder.get(root, NBTPath.fromParts(path.getTokens()));
        if (parent == null) return false;
        if (parent instanceof CompoundTag compound) {
            compound.put(tag);
        } else if (parent instanceof ListTag<?> list) {
            ((ListTag<Tag>) list).add(tag);
        }
        return true;
    }
}
