package com.fasterxml.jackson.datatype.jsr310.old;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import org.junit.Test;

public class TestDecimalUtils extends ModuleTestBase
{
    @Test
    public void testToDecimal01()
    {
        String decimal = DecimalUtils.toDecimal(0, 0);
        assertEquals("The returned decimal is not correct.", NO_NANOSECS_SER, decimal);

        decimal = DecimalUtils.toDecimal(15, 72);
        assertEquals("The returned decimal is not correct.", "15.000000072", decimal);

        decimal = DecimalUtils.toDecimal(19827342231L, 192837465);
        assertEquals("The returned decimal is not correct.", "19827342231.192837465",
                decimal);

        decimal = DecimalUtils.toDecimal(19827342231L, 0);
        assertEquals("The returned decimal is not correct.",
                "19827342231"+NO_NANOSECS_SUFFIX, decimal);

        decimal = DecimalUtils.toDecimal(19827342231L, 987654321);
        assertEquals("The returned decimal is not correct.",
                "19827342231.987654321", decimal);
    }

    @Test
    public void testExtractNanosecondDecimal01()
    {
        BigDecimal value = new BigDecimal("0");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 0L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 0, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal02()
    {
        BigDecimal value = new BigDecimal("15.000000072");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 15L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 72, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal03()
    {
        BigDecimal value = new BigDecimal("15.72");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 15L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 720000000, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal04()
    {
        BigDecimal value = new BigDecimal("19827342231.192837465");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 192837465, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal05()
    {
        BigDecimal value = new BigDecimal("19827342231");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 0, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal06()
    {
        BigDecimal value = new BigDecimal("19827342231.999999999");

        long seconds = value.longValue();
        assertEquals("The second part is not correct.", 19827342231L, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        assertEquals("The nanosecond part is not correct.", 999999999, nanoseconds);
    }
}
