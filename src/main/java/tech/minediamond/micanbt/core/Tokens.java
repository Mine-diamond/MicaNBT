package tech.minediamond.micanbt.core;

public final class Tokens {
    private Tokens() {
    }

    // Constraints
    public static final int MAX_NESTING_DEPTH = 512;

    // Structural Delimiters
    public static final char COMPOUND_BEGIN = '{';
    public static final char COMPOUND_END = '}';
    public static final char COMPOUND_KEY_VALUE_SEPARATOR = ':';
    public static final char ARRAY_BEGIN = '[';
    public static final char ARRAY_END = ']';
    public static final char ARRAY_SIGNATURE_SEPARATOR = ';';
    public static final char VALUE_SEPARATOR = ',';

    // Strings & Escaping
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';
    public static final char ESCAPE_MARKER = '\\';

    // Type suffix
    public static final char TYPE_BYTE = 'b';
    public static final char TYPE_SHORT = 's';
    public static final char TYPE_INT = 'i';
    public static final char TYPE_LONG = 'l';
    public static final char TYPE_FLOAT = 'f';
    public static final char TYPE_DOUBLE = 'd';

    // Type suffix (Uppercase)
    public static final char TYPE_BYTE_UPPER = 'B';
    public static final char TYPE_SHORT_UPPER = 'S';
    public static final char TYPE_INT_UPPER = 'I';
    public static final char TYPE_LONG_UPPER = 'L';
    public static final char TYPE_FLOAT_UPPER = 'F';
    public static final char TYPE_DOUBLE_UPPER = 'D';

    // Numeric Prefixes
    public static final String BINARY_PREFIX = "0b";
    public static final String BINARY_PREFIX_UPPER = "0B";
    public static final String HEX_PREFIX = "0x";
    public static final String HEX_PREFIX_UPPER = "0X";

    // Signedness
    public static final char TYPE_SIGNED = 's';
    public static final char TYPE_UNSIGNED = 'u';
    public static final char TYPE_SIGNED_UPPER = 'S';
    public static final char TYPE_UNSIGNED_UPPER = 'U';

    // Literals
    public static final String LITERAL_TRUE = "true";
    public static final String LITERAL_FALSE = "false";

    // Formatting Characters
    public static final char SPACE = ' ';
    public static final char NEWLINE = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    public static final char EOF = '\0'; // End of File

    public static final char DOT = '.';

    // Internal Lookup Tables & Bitmasks
    private static final int FLAG_IS_DIGIT_EXT = 1;  // 0-9, -, .
    private static final int FLAG_MAY_NUMBER = 2;   // 0-9, -, ., e, E, +

    private static final boolean[] ALLOWED_CHARS = new boolean[128];
    private static final byte[] CHAR_FLAGS = new byte[128];
    private static final long FORMAT_CHARS_MASK = (1L << EOF) | (1L << TAB) | (1L << NEWLINE) | (1L << CARRIAGE_RETURN) | (1L << SPACE);

    static {
        for (char c = 'a'; c <= 'z'; c++) ALLOWED_CHARS[c] = true;
        for (char c = 'A'; c <= 'Z'; c++) ALLOWED_CHARS[c] = true;
        for (char c = '0'; c <= '9'; c++) ALLOWED_CHARS[c] = true;
        ALLOWED_CHARS['-'] = true;
        ALLOWED_CHARS['_'] = true;
        ALLOWED_CHARS['.'] = true;
        ALLOWED_CHARS['+'] = true;

        for (char c = '0'; c <= '9'; c++) {
            CHAR_FLAGS[c] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        }

        CHAR_FLAGS['-'] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        CHAR_FLAGS['.'] |= FLAG_IS_DIGIT_EXT | FLAG_MAY_NUMBER;
        CHAR_FLAGS['+'] |= FLAG_MAY_NUMBER;
        CHAR_FLAGS['e'] |= FLAG_MAY_NUMBER;
        CHAR_FLAGS['E'] |= FLAG_MAY_NUMBER;
    }

    /**
     * Determines if a character is valid for a string that doesn't require quotes.
     *
     * @param c The character to check.
     * @return True if the character is allowed without quotes.
     */
    public static boolean isAllowedInUnquotedString(final char c) {
        return c < 128 && ALLOWED_CHARS[c];
    }

    /**
     * Checks if the given string needs to be wrapped in quotes for SNBT.
     *
     * @param s The string to evaluate.
     * @return True if the string is empty or contains characters that require quoting.
     */
    public static boolean needQuotation(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!isAllowedInUnquotedString(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a character is a digit or a digit-related symbol <code>0-9 - .</code>.
     */
    public static boolean isDigit(final char c) {
        return c < 128 && (CHAR_FLAGS[c] & FLAG_IS_DIGIT_EXT) != 0;
    }

    /**
     * Performs a fast check to see if a string potentially represents a number.
     * This is used to decide whether to attempt numeric parsing.
     *
     * @param s The string to check.
     * @return True if all characters are valid for a numeric literal.
     */
    public static boolean mayNumber(String s) {
        if (s == null || s.isEmpty()) return false;

        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if (c >= 128 || (CHAR_FLAGS[c] & FLAG_MAY_NUMBER) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character is a formatting character
     * include <code>\0 \t \n \r space</code>
     */
    public static boolean isFormatChar(final char c) {
        return c <= 32 && (FORMAT_CHARS_MASK & (1L << c)) != 0;
    }
}
