package tech.minediamond.micanbt.path;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.path.nbtpathtoken.*;
import tech.minediamond.micanbt.snbt.SNBT;
import tech.minediamond.micanbt.tag.CommonCompoundTag;
import tech.minediamond.micanbt.tag.StringTag;
import tech.minediamond.micanbt.tag.Tag;

import static org.junit.jupiter.api.Assertions.*;

public class PathTokenTest {

    @Test
    public void testKeyToken() {
        PathToken token = getFirstToken("name", KeyToken.class);

        verifyToken(token, SNBT.parse("{name: \"value\"}"), "name", "value");
        verifyToken(token, SNBT.parse("{newName: \"value\"}"), "name", null);
    }

    @Test
    public void testIndexToken() {
        PathToken token1 = getFirstToken("[1]", IndexToken.class);
        PathToken token2 = getFirstToken("[-2]", IndexToken.class);

        Tag longListTag = SNBT.parse("name: [1, 2, 3]");
        Tag shortListTag = SNBT.parse("name: [1]");
        Tag nonListTag = SNBT.parse("name: \"str\"");

        verifyToken(token1, longListTag, 1, 2);
        verifyToken(token2, longListTag, 1, 2);

        verifyToken(token1, shortListTag, -1, null);
        verifyToken(token2, shortListTag, -1, null);

        verifyToken(token1, nonListTag, -1, null);
        verifyToken(token2, nonListTag, -1, null);
    }

    @Test
    public void testMatchToken() {
        PathToken token1 = getFirstToken("[{str: \"value\"}]", MatchToken.class);
        PathToken token2 = getFirstToken("[\"str\"]", MatchToken.class);

        Tag compoundListTag = SNBT.parse("[{int:2},{str:\"value\"},{bytes:[B;1b,2b,3b]}]");
        Tag stringListTag = SNBT.parse("[\"str\",\"str1\",\"str2\"]");
        Tag nonListTag = SNBT.parse("{str:\"hello\",int:2}");

        verifyToken(token1, compoundListTag, 0, SNBT.parse("{str:\"value\"}"));
        verifyToken(token2, compoundListTag, -1, null);

        verifyToken(token1, stringListTag, -1, null);
        verifyToken(token2, stringListTag, 0, SNBT.parse("\"str\""));

        verifyToken(token1, nonListTag, -1, null);
        verifyToken(token2, nonListTag, -1, null);
    }

    @Test
    public void testFilterToken() {
        PathToken token1 = getFirstToken("{str: \"value\"}", FilterToken.class);
        PathToken token2 = getFirstToken("{str: \"value\", int: 1}", FilterToken.class);

        Tag compound1Tag = SNBT.parse("{str:\"value\",int:1,byte:2}");
        Tag compound2Tag = SNBT.parse("{str:\"value\",byte:2}");
        Tag stringTag = SNBT.parse("\"value\"");

        verifyToken(token1, compound1Tag, compound1Tag, compound1Tag);
        verifyToken(token2, compound1Tag, compound1Tag, compound1Tag);

        verifyToken(token1, compound2Tag, compound2Tag, compound2Tag);
        verifyToken(token2, compound2Tag, compound2Tag, null);

        verifyToken(token1, stringTag, stringTag, null);
        verifyToken(token2, stringTag, stringTag, null);
    }

    @Test
    public void testIsModifier() {
        assertFalse(new KeyToken("key").isModifier());
        assertFalse(new IndexToken(1).isModifier());
        assertFalse(new MatchToken(new StringTag("")).isModifier());
        assertTrue(new FilterToken(new CommonCompoundTag("")).isModifier());
    }

    private <T extends PathToken> T getFirstToken(String pathStr, Class<T> expectedType) {
        NBTPath path = NBTPath.of(pathStr);
        assertEquals(1, path.getTokens().length, "Token length should be 1");
        assertInstanceOf(expectedType, path.getTokens()[0]);
        return expectedType.cast(path.getTokens()[0]);
    }

    private void verifyToken(PathToken token, Tag tag, Object expectedAccessor, Object expectedValue) {

        assertAll(
                () -> assertEquals(expectedAccessor, token.getAccessor(tag), "Accessor not match"),
                () -> {
                    Tag result = token.navigate(tag);
                    if (expectedValue == null) {
                        assertNull(result, "Navigate result should be null");
                    } else if (expectedValue instanceof Tag) {
                        assertEquals(expectedValue, result, "The returned Tag from Navigate does not match");
                    } else {
                        assertNotNull(result, "Navigate results should not be null");
                        assertEquals(expectedValue, result.getRawValue(), "The original value returned by Navigate does not match");
                    }
                }
        );
    }
}
