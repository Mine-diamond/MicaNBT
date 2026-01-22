package tech.minediamond.lusternbt.SNBT;

final class Tokens {
    private Tokens() {
    }

    // Compounds
    static final char COMPOUND_BEGIN = '{';
    static final char COMPOUND_END = '}';
    static final char COMPOUND_KEY_VALUE_SEPARATOR = ':';

    // Arrays
    static final char ARRAY_BEGIN = '[';
    static final char ARRAY_END = ']';
    static final char ARRAY_SIGNATURE_SEPARATOR = ';';

    static final char VALUE_SEPARATOR = ',';

    static final char SINGLE_QUOTE = '\'';
    static final char DOUBLE_QUOTE = '"';
    static final char ESCAPE_MARKER = '\\';

    //Type suffix
    static final char TYPE_BYTE = 'b';
    static final char TYPE_SHORT = 's';
    static final char TYPE_INT = 'i'; // array only
    static final char TYPE_LONG = 'l';
    static final char TYPE_FLOAT = 'f';
    static final char TYPE_DOUBLE = 'd';

    static final char TYPE_BYTE_UPPER = 'B';
    static final char TYPE_SHORT_UPPER = 'S';
    static final char TYPE_INT_UPPER = 'I'; // array only
    static final char TYPE_LONG_UPPER = 'L';
    static final char TYPE_FLOAT_UPPER = 'F';
    static final char TYPE_DOUBLE_UPPER = 'D';

    static final char TYPE_SIGNED = 's';
    static final char TYPE_UNSIGNED = 'u';

    static final String LITERAL_TRUE = "true";
    static final String LITERAL_FALSE = "false";

    static final char NEWLINE = '\n';
    static final char CARRIAGE_RETURN = '\r';
    static final char SPACE = ' ';
    static final char TAB = '\t';
    static final char EOF = '\0';

    static boolean isAllowedInUnquotedString(final char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '-' || c == '_'
                || c == '.' || c == '+';
    }

    static boolean needQuotation(String s) {
        for (char c : s.toCharArray()) {
            if (!isAllowedInUnquotedString(c)) {
                return true;
            }
        }
        return false;
    }

    static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    static boolean mayNumber(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!isDigit(c) && c != '.' && c != '-' && c != 'e' && c != '+') {
                return false;
            }
        }
        return true;
    }

    static boolean isFormatChar(final char c) {
        return c == TAB || c == SPACE || c == CARRIAGE_RETURN || c == NEWLINE || c == EOF;
    }

    /**
     * Return whether a character is a numeric type identifier.
     *
     * @param c character to check
     * @return if a numeric type identifier
     */
    static boolean numericType(char c) {
        c = Character.toLowerCase(c);
        return c == TYPE_BYTE || c == TYPE_BYTE_UPPER
                || c == TYPE_SHORT || c == TYPE_SHORT_UPPER
                || c == TYPE_INT || c == TYPE_INT_UPPER
                || c == TYPE_LONG || c == TYPE_LONG_UPPER
                || c == TYPE_FLOAT || c == TYPE_FLOAT_UPPER
                || c == TYPE_DOUBLE || c == TYPE_DOUBLE_UPPER;
    }
}