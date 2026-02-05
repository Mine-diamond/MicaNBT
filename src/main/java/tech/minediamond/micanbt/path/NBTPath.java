package tech.minediamond.micanbt.path;

import java.util.List;

public class NBTPath {
    private final String[] tokens;

    private NBTPath(String[] tokens) {
        this.tokens = tokens;
    }

    public static NBTPath of(String path) {
        return new NBTPath(NBTPathReader.read(path));
    }

    public static NBTPath fromParts(String... paths) {
        if (paths == null || paths.length == 0) return new NBTPath(new String[0]);
        return new NBTPath(paths.clone());
    }

    public static NBTPath fromParts(List<String> path) {
        if (path == null || path.isEmpty()) return new NBTPath(new String[0]);
        return new NBTPath(path.toArray(new String[0]));
    }

    public String[] getTokens() {
        return tokens.clone();
    }
}
