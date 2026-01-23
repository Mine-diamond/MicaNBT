package tech.minediamond.lusternbt.SNBT;

/**
 * Defines the formatting styles for Stringified NBT (SNBT) generation.
 * <p>
 * This enum determines how {@link SNBTWriter} handles white space,
 * newlines, and indentation when converting NBT tags to strings.
 */
public enum SNBTStyle {

    /**
     * Minimalist format with no extra spaces or newlines.
     * <p>
     * Example: {@code {key1:1b,key2:[1,2,3]}}
     * Use this for network transmission or storage where space efficiency is a priority.
     */
    COMPACT,

    /**
     * Single-line format that includes spaces after separators for better readability.
     * <p>
     * Example: {@code {key1: 1b, key2: [1, 2, 3]}}
     * Adds a space after the key-value colon (:) and the value separator (,).
     */
    SPACED,

    /**
     * Multi-line "pretty-print" format using newlines and tabs.
     * <p>
     * Example:
     * <pre>{@code
     * {
     *     key1: 1b,
     *     key2: [
     *         1,
     *         2,
     *         3
     *     ]
     * }
     * }</pre>
     * Visually represents the hierarchy of the NBT structure, making it the best choice for configuration files.
     */
    INDENTED;
}
