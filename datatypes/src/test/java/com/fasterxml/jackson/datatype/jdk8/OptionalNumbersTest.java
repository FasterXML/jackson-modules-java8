package com.fasterxml.jackson.datatype.jdk8;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalNumbersTest extends ModuleTestBase
{
    static class OptionalIntBean {
        public OptionalInt value;

        public OptionalIntBean() { value = OptionalInt.empty(); }
        OptionalIntBean(int v) {
            value = OptionalInt.of(v);
        }
    }
    
    static class OptionalLongBean {
        public OptionalLong value;

        public OptionalLongBean() { value = OptionalLong.empty(); }
        OptionalLongBean(long v) {
            value = OptionalLong.of(v);
        }
    }

    static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() { value = OptionalDouble.empty(); }
        OptionalDoubleBean(double v) {
            value = OptionalDouble.of(v);
        }
    }

    private ObjectMapper MAPPER = mapperWithModule();
    private ObjectMapper MAPPER_WITHOUT_COERCION = mapperBuilderWithModule()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();

    /*
    /**********************************************************
    /* Test methods, OptionalInt
    /**********************************************************
     */

    @Test
    public void testOptionalIntAbsent() throws Exception
    {
        String json = MAPPER.writeValueAsString(OptionalInt.empty());
        assertFalse(MAPPER.readValue(json, OptionalInt.class).isPresent());
    }

    @Test
    public void testOptionalIntInArrayAbsent() throws Exception
    {
        OptionalInt[] ints = MAPPER.readValue("[null]", OptionalInt[].class);
        assertEquals(1, ints.length);
        assertNotNull(ints[0]);
        assertFalse(ints[0].isPresent());
    }

    @Test
    public void testOptionalIntPresent() throws Exception
    {
        assertEquals(5, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalInt.of(5)), OptionalInt.class).getAsInt());
    }

    @Test
    public void testOptionalIntCoerceFromString() throws Exception
    {
        OptionalInt opt = MAPPER.readValue(q("123"), OptionalInt.class);
        assertEquals(123, opt.getAsInt());
        opt = MAPPER.readValue("\"\"", OptionalInt.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());

        OptionalIntBean bean = MAPPER.readValue(a2q("{'value':null}"),
                OptionalIntBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(a2q("{'value':'-37'}"), OptionalIntBean.class);
        assertNotNull(bean.value);
        assertEquals(-37L, bean.value.getAsInt());
    }

    /*
    /**********************************************************
    /* Test methods, OptionalLong
    /**********************************************************
     */

    @Test
    public void testOptionalLongAbsent() throws Exception
    {
        assertFalse(MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalLong.empty()), OptionalLong.class).isPresent());
    }

    @Test
    public void testOptionalLongInArrayAbsent() throws Exception
    {
        OptionalLong[] arr = MAPPER.readValue("[null]", OptionalLong[].class);
        assertEquals(1, arr.length);
        assertNotNull(arr[0]);
        assertFalse(arr[0].isPresent());
    }

    @Test
    public void testOptionalLongPresent() throws Exception
    {
        assertEquals(Long.MAX_VALUE, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalLong.of(Long.MAX_VALUE)), OptionalLong.class).getAsLong());
    }

    @Test
    public void testOptionalLongCoerceFromString() throws Exception
    {
        OptionalLong opt = MAPPER.readValue(q("123"), OptionalLong.class);
        assertEquals(123L, opt.getAsLong());

        // should coerce from empty String too (by default)
        opt = MAPPER.readValue("\"\"", OptionalLong.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());
        
        OptionalLongBean bean = MAPPER.readValue(a2q("{'value':null}"),
                OptionalLongBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(a2q("{'value':'19'}"), OptionalLongBean.class);
        assertNotNull(bean.value);
        assertEquals(19L, bean.value.getAsLong());
    }

    @Test
    public void testOptionalLongSerializeFilter() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        assertEquals(a2q("{'value':123}"),
                mapper.writeValueAsString(new OptionalLongBean(123L)));
        // absent is not strictly null so
        assertEquals(a2q("{'value':null}"),
                mapper.writeValueAsString(new OptionalLongBean()));

        // however:
        mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        assertEquals(a2q("{'value':456}"),
                mapper.writeValueAsString(new OptionalLongBean(456L)));
        assertEquals(a2q("{}"),
                mapper.writeValueAsString(new OptionalLongBean()));
    }

    /*
    /**********************************************************
    /* Test methods, OptionalDouble
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleAbsent() throws Exception
    {
        assertFalse(MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalInt.empty()), OptionalInt.class).isPresent());
    }

    @Test
    public void testOptionalDoubleInArrayAbsent() throws Exception
    {
        OptionalDouble[] arr = MAPPER.readValue("[null]", OptionalDouble[].class);
        assertEquals(1, arr.length);
        assertNotNull(arr[0]);
        assertFalse(arr[0].isPresent());
    }

    @Test
    public void testOptionalDoublePresent() throws Exception
    {
        assertEquals(Double.MIN_VALUE, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalDouble.of(Double.MIN_VALUE)), OptionalDouble.class).getAsDouble());
    }

    @Test
    public void testOptionalDoubleCoerceFromString() throws Exception
    {
        OptionalDouble opt = MAPPER.readValue(q("0.25"), OptionalDouble.class);
        assertEquals(0.25, opt.getAsDouble());

        // should coerce from empty String too (by default)
        opt = MAPPER.readValue("\"\"", OptionalDouble.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());
        
        OptionalDoubleBean bean = MAPPER.readValue(a2q("{'value':null}"),
                OptionalDoubleBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(a2q("{'value':'0.5'}"), OptionalDoubleBean.class);
        assertNotNull(bean.value);
        assertEquals(0.5, bean.value.getAsDouble());
    }

    @Test
    public void testOptionalDoubleInArraySpecialValues() throws Exception
    {
        OptionalDouble[] actual = MAPPER.readValue(
                "[null,\"NaN\",\"Infinity\",\"-Infinity\",1,\"2\"]",
                OptionalDouble[].class);
        OptionalDouble[] expected = new OptionalDouble[] {
                OptionalDouble.empty(),
                OptionalDouble.of(Double.NaN),
                OptionalDouble.of(Double.POSITIVE_INFINITY),
                OptionalDouble.of(Double.NEGATIVE_INFINITY),
                OptionalDouble.of(1D),
                OptionalDouble.of(2D)
        };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testOptionalDoubleInArraySpecialValuesWithoutCoercion() throws Exception
    {
        OptionalDouble[] actual = MAPPER_WITHOUT_COERCION.readValue(
                a2q("[null,'NaN','Infinity','-Infinity',1]"),
                OptionalDouble[].class);
        OptionalDouble[] expected = new OptionalDouble[] {
                OptionalDouble.empty(),
                OptionalDouble.of(Double.NaN),
                OptionalDouble.of(Double.POSITIVE_INFINITY),
                OptionalDouble.of(Double.NEGATIVE_INFINITY),
                OptionalDouble.of(1D)
        };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testQuotedOptionalDoubleWithoutCoercion()
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER_WITHOUT_COERCION.readValue(a2q("['1']"), OptionalDouble[].class));
    }

    @Test
    public void testOptionalDoubleBeanSpecialValuesWithoutCoercion_null() throws Exception
    {
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(
                a2q("{'value':null}"), OptionalDoubleBean.class);
        assertEquals(OptionalDouble.empty(), bean.value);
    }

    @Test
    public void testOptionalDoubleBeanSpecialValuesWithoutCoercion_nan() throws Exception
    {
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(
                a2q("{'value':'NaN'}"), OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.NaN), bean.value);
    }

    @Test
    public void testOptionalDoubleBeanSpecialValuesWithoutCoercion_positiveInfinity() throws Exception
    {
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(
                a2q("{'value':'Infinity'}"), OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.POSITIVE_INFINITY), bean.value);
    }

    @Test
    public void testOptionalDoubleBeanSpecialValuesWithoutCoercion_negativeInfinity() throws Exception
    {
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(
                a2q("{'value':'-Infinity'}"), OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.NEGATIVE_INFINITY), bean.value);
    }
}
