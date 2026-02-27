package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.path.nbtpathtoken.PathToken;

import java.util.Arrays;
import java.util.List;

public class NBTPath {
    private final PathToken[] tokens;

    private NBTPath(PathToken[] tokens) {
        this.tokens = tokens;
    }

    public static NBTPath of(String path) {
        return new NBTPath(NBTPathReader.read(path));
    }

    public static NBTPath fromParts(PathToken... paths) {
        if (paths == null || paths.length == 0) return new NBTPath(new PathToken[0]);
        return new NBTPath(paths.clone());
    }

    public static NBTPath fromParts(List<Object> path) {
        if (path == null || path.isEmpty()) return new NBTPath(new PathToken[0]);
        return new NBTPath(path.toArray(new PathToken[0]));
    }

    public NBTPath resolve(NBTPath path) {
        return mergeToken(this.tokens, path.tokens);
    }

    public NBTPath resolve(String path) {
        return mergeToken(this.tokens, NBTPath.of(path).tokens);
    }

    public NBTPath resolveFromParts(PathToken... parts) {
        return mergeToken(this.tokens, parts);
    }

    private static NBTPath mergeToken(PathToken[] first, PathToken[] second) {
        PathToken[] mergedTokens = new PathToken[first.length + second.length];
        System.arraycopy(first, 0, mergedTokens, 0, first.length);
        System.arraycopy(second, 0, mergedTokens, first.length, second.length);
        return new NBTPath(mergedTokens);
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
        NBTPath that = (NBTPath) o;
        return Arrays.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tokens);
    }

    @Override
    public String toString() {
        return NBTPathWriter.getNBTPathString(this);
    }
}
