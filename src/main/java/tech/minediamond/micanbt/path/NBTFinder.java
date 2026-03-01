package tech.minediamond.micanbt.path;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.minediamond.micanbt.path.nbtpathtoken.PathToken;
import tech.minediamond.micanbt.tag.Tag;

public class NBTFinder {
    private NBTFinder() {
    }

    @CheckReturnValue
    @Contract("null, _ -> null")
    public static @Nullable Tag get(@Nullable Tag root, @NotNull String path) {
        return get(root, NBTPath.of(path));
    }

    @CheckReturnValue
    @Contract("null, _ -> null")
    public static @Nullable Tag findFirst(@Nullable Tag root, @NotNull String... paths) {
        for (String path : paths) {
            Tag tag = get(root, path);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }

    @CheckReturnValue
    @Contract("null, _ -> null")
    public static @Nullable Tag findFirst(@Nullable Tag root, @NotNull NBTPath... paths) {
        for (NBTPath path : paths) {
            Tag tag = get(root, path);
            if (tag != null) {
                return tag;
            }
        }
        return null;
    }

    @CheckReturnValue
    @Contract("null, _ -> null")
    public static @Nullable Tag get(@Nullable Tag root, @NotNull NBTPath path) {
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
