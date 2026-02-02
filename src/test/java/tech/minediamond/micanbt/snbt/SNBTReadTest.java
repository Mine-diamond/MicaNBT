package tech.minediamond.micanbt.snbt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.tag.CompoundTag;
import tech.minediamond.micanbt.tag.Tag;

import static org.junit.jupiter.api.Assertions.*;

public class SNBTReadTest {

    static String snbtData;

    @BeforeAll
    public static void init() {
        snbtData = initSNBTData();
    }

    @Test
    public void testSNBTRead() {
        Tag tag = SNBT.parse(snbtData);
        assertInstanceOf(CompoundTag.class, tag);
        CompoundTag root = (CompoundTag) tag;
        assertAll("字段校验",
                // --- String Tags ---
                () -> assertEquals("str", root.get("str").getRawValue()),
                () -> assertEquals("\n\t\"sf\\", root.get("string with escape").getRawValue()),
                () -> assertEquals("Key is just a dot", root.get(".").getRawValue()),
                () -> assertEquals("Key is an empty string", root.get("").getRawValue()),
                () -> assertEquals("look! A Balloon🎈", root.get("StringWithEmoji").getRawValue()),
                () -> assertEquals("Can use single quotes", root.get("singleQuoted").getRawValue()),
                () -> assertEquals("u00A7cRed Text", root.get("unicode").getRawValue()),

                // --- Byte Tags ---
                () -> assertEquals((byte) -3, root.get("ByteTag").getRawValue()),
                () -> assertEquals((byte) -1, root.get("unsignedByte").getRawValue()),
                () -> assertEquals((byte) 1, root.get("isTrue").getRawValue()),
                () -> assertEquals((byte) 0, root.get("isFalse").getRawValue()),

                // --- Short Tags ---
                () -> assertEquals((short) 10, root.get("ShortTag").getRawValue()),
                () -> assertEquals((short) -15536, root.get("unsigned short").getRawValue()),

                // --- Int Tags ---
                () -> assertEquals(1, root.get("IntTag").getRawValue()),
                () -> assertEquals(1, root.get("intTagWithSuffix").getRawValue()),
                () -> assertEquals(2147483647, root.get("MaxInt").getRawValue()),

                // --- Long Tags ---
                () -> assertEquals(-1L, root.get("LongTag").getRawValue()),

                // --- Floating Point Tags ---
                () -> assertEquals(1.0f, root.get("FloatTag").getRawValue()),
                () -> assertEquals(11.22334455667788d, root.get("DoubleTag").getRawValue()),
                () -> assertEquals(-2.3355E13d, root.get("ScientificNotation").getRawValue()),
                () -> assertEquals(-0.0f, root.get("fNegZero").getRawValue()),
                () -> assertEquals(1.0E-3d, root.get("dSci").getRawValue())
        );
    }

    public static String initSNBTData() {
        return """
                "test edge case": {
                    str: "str",
                    "string with escape": "\\n\\t\\"sf\\\\",
                    ".": "Key is just a dot",
                    "": "Key is an empty string",
                    StringWithEmoji: "look! A Balloon🎈",
                    'singleQuoted': 'Can use single quotes',
                    "unicode": "\\u00A7cRed Text",
                
                	ByteTag: -3b,
                    unsignedByte: 255ub,
                    isTrue: true,
                    isFalse: false,
                
                    ShortTag: 10s,
                    "unsigned short": 50000us,
                
                    IntTag: 1,
                    intTagWithSuffix: 1i,
                    MaxInt: 2147483647,
                
                    LongTag: -1L,
                
                	FloatTag: 1.0f,
                    DoubleTag: 11.22334455667788,
                    ScientificNotation: -2.3355E13d,
                    fNegZero: -0.0f,
                    dSci: 1.0E-3d,
                
                    ByteArrayTag: [B; 1b, -100b, -56b],
                	IntArrayTag: [I; -1231, 0xFFFFFFi, 16750882],
                	LongArrayTag: [L; 1L, 65450L, -305L],
                	ListWithItem: [
                		{},
                		{}
                	],
                
                    emptyCompound: {},
                    emptyList: [],
                    emptyIntArray: [I;],
                    emptyString: "",
                
                    items: [
                        {id: "minecraft:stone", Count: 64b, tag: {}},
                        {id: "minecraft:written_book", Count: 1b, tag: {title: "A \\"Book\\"", pages: ["p1", "p2\\nwith newline"]}}
                    ],
                
                    deeply_nested: {
                        level1: {
                            level2: {
                                answer: 42,
                                "yet another": {
                                    final: "You made it!"
                                }
                            }
                        }
                    }
                }
                """;
    }
}
