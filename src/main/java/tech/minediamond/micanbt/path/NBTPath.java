package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.path.nbtpathtoken.PathToken;
import tech.minediamond.micanbt.tag.Tag;

import java.util.Arrays;
import java.util.List;

/// Represents a logical path used to navigate and locate specific tags within an NBT structure.
///
/// This class breaks down a hierarchical path into discrete tokens (segments).
/// For example, a path represented by `Player.Inventory[0].id` is parsed into
/// the tokens `["Player", "Inventory", 0, "id"]`.
///
/// To use `NBTPath`, you can use [NBTFinder] Through method [#get(Tag,NBTPath)], or Directly use [#at(String)] or [#at(NBTPath)] in [Tag]
///
/// @see NBTFinder
public class NBTPath {
    private final PathToken[] tokens;

    private NBTPath(PathToken[] tokens) {
        this.tokens = tokens;
    }

    /// Parses a standard NBT path string into an `NBTPath` instance.
    ///
    /// @param path The string representation of the NBT path.
    /// @return A new `NBTPath` instance representing the parsed string.
    public static NBTPath of(String path) {
        return new NBTPath(NBTPathReader.read(path));
    }

    public static NBTPath fromParts(PathToken... paths) {
        if (paths == null || paths.length == 0) return new NBTPath(new PathToken[0]);
        return new NBTPath(paths.clone());
    }

    public static NBTPath fromParts(List<PathToken> path) {
        if (path == null || path.isEmpty()) return new NBTPath(new PathToken[0]);
        return new NBTPath(path.toArray(new PathToken[0]));
    }

    /// Resolves the given `NBTPath` against this path, effectively appending
    /// the second path to the end of the current one.
    ///
    /// @param path The path to be appended.
    /// @return A new `NBTPath` representing the combined sequence of tokens.
    public NBTPath resolve(NBTPath path) {
        return mergeToken(this.tokens, path.tokens);
    }

    /// Parses the provided path string and resolves it against this path.
    ///
    /// @param path The string representation of the path to append.
    /// @return A new `NBTPath` representing the combined path.
    /// @see #of(String)
    public NBTPath resolve(String path) {
        return mergeToken(this.tokens, NBTPath.of(path).tokens);
    }

    /// Resolves the given raw tokens against this path.
    ///
    /// @param parts The raw path segments (e.g., String for keys, Integer for indices) to append.
    /// @return A new `NBTPath` containing the merged tokens.
    public NBTPath resolveFromParts(PathToken... parts) {
        return mergeToken(this.tokens, parts);
    }

    /// Get the `NBTPath` pointing to the parent of this `NBTPath`, or return null if there is no parent node
    ///
    /// @return A new `NBTPath` that is the father of this `NBTPath`
    public NBTPath getParent() {
        int i = this.tokens.length - 1;
        while (i >= 0 && tokens[i].isModifier()) {
            i--;
        }
        i--;
        int length = i + 1;
        if (length <= 0) {
            return null;
        }
        PathToken[] newTokens = new PathToken[length];
        System.arraycopy(tokens, 0, newTokens, 0, length);
        return new NBTPath(newTokens);
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
