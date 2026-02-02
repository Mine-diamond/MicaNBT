package tech.minediamond.micanbt.basic;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.minediamond.micanbt.tag.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

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
    }

    @Nested
    class ShortTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(shorts = {Short.MIN_VALUE, -100, 0, 100, Short.MAX_VALUE})
        void testCommonLogic(short value) {
            testTag(new ShortTag("TestTag", value), 2);
        }
    }

    @Nested
    class IntTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, -10000, 0, 10000, Integer.MAX_VALUE})
        void testCommonLogic(int value) {
            testTag(new IntTag("TestTag", value), 3);
        }
    }

    @Nested
    class LongTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, -1000000, 0, 1000000, Long.MAX_VALUE})
        void testCommonLogic(long value) {
            testTag(new LongTag("TestTag", value), 4);
        }
    }

    @Nested
    class FloatTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(floats = {Float.MIN_VALUE, -10000.0f, 0.0f, 10000.0f, 11.223344556f, Float.MAX_VALUE})
        void testCommonLogic(float value) {
            testTag(new FloatTag("TestTag", value), 5);
        }
    }

    @Nested
    class DoubleTagTest extends AbstractTagTest {
        @ParameterizedTest
        @ValueSource(doubles = {Double.MIN_VALUE, -10000.0, 0.0, 10000.0, 11.22334455634, Double.MAX_VALUE})
        void testCommonLogic(double value) {
            testTag(new DoubleTag("TestTag", value), 6);
        }
    }
}
