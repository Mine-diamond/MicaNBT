package tech.minediamond.micanbt.path;

import java.util.Arrays;
import java.util.List;

public class NBTPath2 {
    private final PathToken[] tokens;

    private NBTPath2(PathToken[] tokens) {
        this.tokens = tokens;
    }

    public static NBTPath2 of(String path) {
        return new NBTPath2(NBTPathReader2.read(path));
    }

    public static NBTPath2 fromParts(PathToken... paths) {
        if (paths == null || paths.length == 0) return new NBTPath2(new PathToken[0]);
        return new NBTPath2(paths.clone());
    }

    public static NBTPath2 fromParts(List<Object> path) {
        if (path == null || path.isEmpty()) return new NBTPath2(new PathToken[0]);
        return new NBTPath2(path.toArray(new PathToken[0]));
    }

    public NBTPath2 resolve(NBTPath2 path) {
        return mergeToken(this.tokens, path.tokens);
    }

    public NBTPath2 resolve(String path) {
        return mergeToken(this.tokens, NBTPath2.of(path).tokens);
    }

    public NBTPath2 resolveFromParts(PathToken... parts) {
        return mergeToken(this.tokens, parts);
    }

    private static NBTPath2 mergeToken(PathToken[] first, PathToken[] second) {
        PathToken[] mergedTokens = new PathToken[first.length + second.length];
        System.arraycopy(first, 0, mergedTokens, 0, first.length);
        System.arraycopy(second, 0, mergedTokens, first.length, second.length);
        return new NBTPath2(mergedTokens);
    }

    /**
     * Returns a copy of the tokens that make up this path.
     *
     * @return An array of tokens representing the path segments.
     */
    public PathToken[] getTokens() {
        return tokens.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBTPath2 that = (NBTPath2) o;
        return Arrays.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tokens);
    }
}
