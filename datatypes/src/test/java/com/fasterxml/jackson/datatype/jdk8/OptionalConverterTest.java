package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.*;

// [modules-java#294]: Optional + converters
public class OptionalConverterTest extends ModuleTestBase
{
    static class Point
    {
        public int x, y;

        protected Point() { }
        public Point(int v1, int v2) {
            x = v1;
            y = v2;
        }
    }

    static class PointDeserConverter extends StdConverter<int[], Point>
    {
        @Override
        public Point convert(int[] value) {
            return new Point(value[0], value[1]);
        }
    }

    static class PointSerConverter extends StdConverter<Point, int[]>
    {
        @Override public int[] convert(Point value) {
            return new int[] { value.x, value.y };
        }
    }

    static class PointReferenceBean {
        @JsonDeserialize(contentConverter=PointDeserConverter.class)
        @JsonSerialize(contentConverter=PointSerConverter.class)
        public Optional<Point> opt;

        protected PointReferenceBean() { }
        public PointReferenceBean(int x, int y) {
            opt = Optional.of(new Point(x, y));
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    // [modules-java#294]: Optional + converters, deser
    @Test
    public void testDeserializeOptionalConverting() throws Exception {
        PointReferenceBean w = MAPPER.readerFor(PointReferenceBean.class)
                .readValue("{\"opt\": [1,2]}");
        assertNotNull(w);
        assertNotNull(w.opt);
        Point p = w.opt.get();
        assertNotNull(p);
        assertEquals(1, p.x);
        assertEquals(2, p.y);
    }

    // [modules-java#294]: Optional + converters, ser
    @Test
    public void testSerializeOptionalConverting() throws Exception {
        assertEquals("{\"opt\":[3,4]}",
                MAPPER.writeValueAsString(new PointReferenceBean(3, 4)));
    }
}
