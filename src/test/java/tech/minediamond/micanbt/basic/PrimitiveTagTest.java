package tech.minediamond.micanbt.basic;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.minediamond.micanbt.tag.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitiveTagTest {

    static abstract class AbstractTagTest {
        protected void testTag(Tag tag, int expectedTagId) {
            //test tag id
            assertEquals(expectedTagId, tag.getTagId());

            //test copy
            Tag copy = tag.copy();
            assertEquals(tag, copy);
            assertNotSame(tag, copy);
            assertEquals(tag.hashCode(), copy.hashCode());
        }
    }

    @Nested
    class ByteTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(bytes = {Byte.MIN_VALUE, -15, 0, 15, Byte.MAX_VALUE})
        void testCommonLogic(byte value) {
            testTag(new ByteTag("TestTag", value), 1);
        }

        @ParameterizedTest
        @ValueSource(bytes = {Byte.MIN_VALUE, -15, 0, 15, Byte.MAX_VALUE})
        void testSetValue(byte value) {
            ByteTag tag = new ByteTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class ShortTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(shorts = {Short.MIN_VALUE, -100, 0, 100, Short.MAX_VALUE})
        void testCommonLogic(short value) {
            testTag(new ShortTag("TestTag", value), 2);
        }

        @ParameterizedTest
        @ValueSource(shorts = {Short.MIN_VALUE, -100, 0, 100, Short.MAX_VALUE})
        void testSetValue(short value) {
            ShortTag tag = new ShortTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class IntTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, -10000, 0, 10000, Integer.MAX_VALUE})
        void testCommonLogic(int value) {
            testTag(new IntTag("TestTag", value), 3);
        }

        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, -100, 0, 100, Integer.MAX_VALUE})
        void testSetValue(int value) {
            IntTag tag = new IntTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class LongTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, -1000000, 0, 1000000, Long.MAX_VALUE})
        void testCommonLogic(long value) {
            testTag(new LongTag("TestTag", value), 4);
        }

        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, -100, 0, 100, Integer.MAX_VALUE})
        void testSetValue(long value) {
            LongTag tag = new LongTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class FloatTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(floats = {Float.MIN_VALUE, -10000.0f, 0.0f, 10000.0f, 11.223344556f, Float.MAX_VALUE})
        void testCommonLogic(float value) {
            testTag(new FloatTag("TestTag", value), 5);
        }

        @ParameterizedTest
        @ValueSource(floats = {Float.MIN_VALUE, -10000.0f, 0.0f, 10000.0f, 11.223344556f, Float.MAX_VALUE})
        void testSetValue(float value) {
            FloatTag tag = new FloatTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class DoubleTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(doubles = {Double.MIN_VALUE, -10000.0, 0.0, 10000.0, 11.22334455634, Double.MAX_VALUE})
        void testCommonLogic(double value) {
            testTag(new DoubleTag("TestTag", value), 6);
        }

        @ParameterizedTest
        @ValueSource(doubles = {Double.MIN_VALUE, -10000.0, 0.0, 10000.0, 11.22334455634, Double.MAX_VALUE})
        void testSetValue(double value) {
            DoubleTag tag = new DoubleTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class StringTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(strings = {"str", "value", "😀", "值"})
        void testCommonLogic(String value) {
            testTag(new StringTag("TestTag", value), 8);
        }

        @ParameterizedTest
        @ValueSource(strings = {"str", "value", "😀", "值"})
        void testSetValue(String value) {
            StringTag tag = new StringTag("TestTag");
            tag.setValue(value);
            assertEquals(value, tag.getRawValue());
        }
    }

    @Nested
    class ByteArrayTagTest extends AbstractTagTest {
        static Stream<Arguments> provideByteArrayTagTestTestSource() {
            return Stream.of(
                    Arguments.of(new byte[]{1, 2, 3}),
                    Arguments.of(new byte[]{-15, 0, 15}),
                    Arguments.of(new byte[]{Byte.MIN_VALUE, 2, Byte.MAX_VALUE})
            );
        }

        @ParameterizedTest
        @MethodSource("provideByteArrayTagTestTestSource")
        void testCommonLogic(byte[] value) {
            testTag(new ByteArrayTag("TestTag", value), 7);
        }

        @ParameterizedTest
        @MethodSource("provideByteArrayTagTestTestSource")
        void testSetValue(byte[] value) {
            ByteArrayTag tag = new ByteArrayTag("TestTag");
            tag.setValue(value);
            assertArrayEquals(value, tag.getRawValue());

            for (int i = 0; i < value.length; i++) {
                assertEquals(value[i], tag.getValue(i));
            }
        }
    }

    @Nested
    class IntArrayTagTest extends AbstractTagTest {
        static Stream<Arguments> provideIntArrayTagTestTestSource() {
            return Stream.of(
                    Arguments.of(new int[]{1, 2, 3}),
                    Arguments.of(new int[]{-100, 0, 100}),
                    Arguments.of(new int[]{Integer.MIN_VALUE, 2, Integer.MAX_VALUE})
            );
        }

        @ParameterizedTest
        @MethodSource("provideIntArrayTagTestTestSource")
        void testCommonLogic(int[] value) {
            testTag(new IntArrayTag("TestTag", value), 11);
        }

        @ParameterizedTest
        @MethodSource("provideIntArrayTagTestTestSource")
        void testSetValue(int[] value) {
            IntArrayTag tag = new IntArrayTag("TestTag");
            tag.setValue(value);
            assertArrayEquals(value, tag.getRawValue());

            for (int i = 0; i < value.length; i++) {
                assertEquals(value[i], tag.getValue(i));
            }
        }
    }

    @Nested
    class LongArrayTagTest extends AbstractTagTest {
        static Stream<Arguments> provideLongArrayTagTestTestSource() {
            return Stream.of(
                    Arguments.of(new long[]{1, 2, 3}),
                    Arguments.of(new long[]{-1000000, 0, 1000000}),
                    Arguments.of(new long[]{Long.MIN_VALUE, 2, Long.MAX_VALUE})
            );
        }

        @ParameterizedTest
        @MethodSource("provideLongArrayTagTestTestSource")
        void testCommonLogic(long[] value) {
            testTag(new LongArrayTag("TestTag", value), 12);
        }

        @ParameterizedTest
        @MethodSource("provideLongArrayTagTestTestSource")
        void testSetValue(long[] value) {
            LongArrayTag tag = new LongArrayTag("TestTag");
            tag.setValue(value);
            assertArrayEquals(value, tag.getRawValue());

            for (int i = 0; i < value.length; i++) {
                assertEquals(value[i], tag.getValue(i));
            }
        }
    }

    @Test
    public void testEquals() {
        assertEquals(new ByteTag("name", (byte) 5), new ByteTag("name", (byte) 5));
        assertNotEquals(new ByteTag("name", (byte) 5), new ByteTag("name", (byte) 6));
        assertNotEquals(new ByteTag("name", (byte) 5), new ByteTag("newName", (byte) 5));

        assertEquals(new ShortTag("name", (short) 5), new ShortTag("name", (short) 5));
        assertNotEquals(new ShortTag("name", (short) 5), new ShortTag("name", (short) 6));
        assertNotEquals(new ShortTag("name", (short) 5), new ShortTag("newName", (short) 5));

        assertEquals(new IntTag("name", 5), new IntTag("name", 5));
        assertNotEquals(new IntTag("name", 5), new IntTag("name", 6));
        assertNotEquals(new IntTag("name", 5), new IntTag("newName", 5));

        assertEquals(new LongTag("name", 5), new LongTag("name", 5));
        assertNotEquals(new LongTag("name", 5), new LongTag("name", 6));
        assertNotEquals(new LongTag("name", 5), new LongTag("newName", 5));

        assertEquals(new FloatTag("name", 5), new FloatTag("name", 5));
        assertNotEquals(new FloatTag("name", 5), new FloatTag("name", 6));
        assertNotEquals(new FloatTag("name", 5), new FloatTag("newName", 5));

        assertEquals(new DoubleTag("name", 5), new DoubleTag("name", 5));
        assertNotEquals(new DoubleTag("name", 5), new DoubleTag("name", 6));
        assertNotEquals(new DoubleTag("name", 5), new DoubleTag("newName", 5));

        assertEquals(new ByteArrayTag("name", new byte[]{5, 6, 7}), new ByteArrayTag("name", new byte[]{5, 6, 7}));
        assertNotEquals(new ByteArrayTag("name", new byte[]{5, 6, 7}), new ByteArrayTag("name", new byte[]{1, 2, 3}));
        assertNotEquals(new ByteArrayTag("name", new byte[]{5, 6, 7}), new ByteArrayTag("newName", new byte[]{5, 6, 7}));

        assertEquals(new IntArrayTag("name", new int[]{5, 6, 7}), new IntArrayTag("name", new int[]{5, 6, 7}));
        assertNotEquals(new IntArrayTag("name", new int[]{5, 6, 7}), new IntArrayTag("name", new int[]{1, 2, 3}));
        assertNotEquals(new IntArrayTag("name", new int[]{5, 6, 7}), new IntArrayTag("newName", new int[]{5, 6, 7}));

        assertEquals(new LongArrayTag("name", new long[]{5, 6, 7}), new LongArrayTag("name", new long[]{5, 6, 7}));
        assertNotEquals(new LongArrayTag("name", new long[]{5, 6, 7}), new LongArrayTag("name", new long[]{1, 2, 3}));
        assertNotEquals(new LongArrayTag("name", new long[]{5, 6, 7}), new LongArrayTag("newName", new long[]{5, 6, 7}));

        assertEquals(new StringTag("name", "value"), new StringTag("name", "value"));
        assertNotEquals(new StringTag("name", "value"), new StringTag("name", "newValue"));
        assertNotEquals(new StringTag("name", "value"), new StringTag("newName", "value"));
    }
}
