package tech.minediamond.micanbt.path;

import java.util.Arrays;
import java.util.List;

public class NBTPath {
    private final String[] tokens;

    private NBTPath(String[] tokens) {
        this.tokens = tokens;
    }

    public static NBTPath of(String path) {
        if (path == null || path.isEmpty()) return new NBTPath(new String[0]);
        if (path.startsWith("/")) path = path.substring(1);
        String[] parts = path.split("/", -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace("~1", "/").replace("~0", "~");
        }
        return new NBTPath(parts);
    }

    public static NBTPath at(String... paths) {
        if (paths == null || paths.length == 0) return new NBTPath(new String[0]);
        return new NBTPath(paths);
    }

    public static NBTPath at(List<String> path) {
        if (path == null || path.isEmpty()) return new NBTPath(new String[0]);
        return new NBTPath(path.toArray(new String[0]));
    }

    public String[] getTokens() {
        return tokens;
    }
}
