package tech.minediamond.micanbt.path;

import tech.minediamond.micanbt.tag.Tag;

import java.util.List;

/**
 * Represents a logical path used to navigate and locate specific tags within an NBT structure.
 * <p>
 * This class breaks down a hierarchical path into discrete tokens (segments).
 * For example, a path represented by {@code Player.Inventory[0].id} is parsed into
 * the tokens {@code ["Player", "Inventory", 0, "id"]}.
 * <p>
 * To use {@code NBTPath}, you can use {@link NBTFinder} Through method {@link NBTFinder#get(Tag, NBTPath)}, or Directly use {@link Tag#at(String)} or {@link Tag#at(NBTPath)} in {@link Tag}
 *
 * @see NBTFinder
 */
public class NBTPath {
    private final Object[] tokens;

    private NBTPath(Object[] tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses a standard NBT path string into an {@code NBTPath} instance.
     * <p>
     * <b>Constraints:</b> This method only supports <b>static paths</b>.
     * Complex selectors, filters, or predicates within brackets are not supported.
     * <ul>
     *     <li>Supported: {@code Player.Inventory[0].id}</li>
     *     <li>Not Supported: {@code Player.Inventory[{Slot:0b}].id}</li>
     * </ul>
     *
     * @param path The string representation of the NBT path.
     * @return A new {@code NBTPath} instance representing the parsed string.
     */
    public static NBTPath of(String path) {
        return new NBTPath(NBTPathReader.read(path));
    }

    /**
     * Creates an {@code NBTPath} from raw path segments without performing any escaping or parsing.
     * <p>
     * The provided strings are treated as literal tokens. For example, passing
     * {@code "Inventory", "0"} will target a key named "Inventory" followed by an index or key "0".
     *
     * @param paths The array of raw path tokens.
     * @return A new {@code NBTPath} instance.
     */
    public static NBTPath fromParts(Object... paths) {
        if (paths == null || paths.length == 0) return new NBTPath(new String[0]);
        return new NBTPath(paths.clone());
    }

    /**
     * Creates an {@code NBTPath} from a list of raw path segments.
     * <p>
     * This is a collection-based alternative to {@link #fromParts(Object...)}.
     *
     * @param path A list containing the raw path tokens.
     * @return A new {@code NBTPath} instance.
     */
    public static NBTPath fromParts(List<Object> path) {
        if (path == null || path.isEmpty()) return new NBTPath(new String[0]);
        return new NBTPath(path.toArray(new Object[0]));
    }

    /**
     * Returns a copy of the tokens that make up this path.
     *
     * @return An array of strings representing the path segments.
     */
    public Object[] getTokens() {
        return tokens.clone();
    }
}
