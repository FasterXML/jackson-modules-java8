package com.fasterxml.jackson.datatype.jsr310;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

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
        assertEquals("The returned decimal is not correct.", "19827342231.192837465", decimal);

        decimal = DecimalUtils.toDecimal(19827342231L, 0);
        assertEquals("The returned decimal is not correct.",
                "19827342231"+NO_NANOSECS_SUFFIX, decimal);

        decimal = DecimalUtils.toDecimal(19827342231L, 999888000);
        assertEquals("The returned decimal is not correct.",
                "19827342231.999888000", decimal);
    }



    private void checkExtractNanos(long expectedSeconds, int expectedNanos, BigDecimal decimal)
    {
        long seconds = decimal.longValue();
        assertEquals("The second part is not correct.", expectedSeconds, seconds);

        int nanoseconds = DecimalUtils.extractNanosecondDecimal(decimal,  seconds);
        assertEquals("The nanosecond part is not correct.", expectedNanos, nanoseconds);
    }

    @Test
    public void testExtractNanosecondDecimal01()
    {
        BigDecimal value = new BigDecimal("0");
        checkExtractNanos(0L, 0, value);
    }

    @Test
    public void testExtractNanosecondDecimal02()
    {
        BigDecimal value = new BigDecimal("15.000000072");
        checkExtractNanos(15L, 72, value);
    }

    @Test
    public void testExtractNanosecondDecimal03()
    {
        BigDecimal value = new BigDecimal("15.72");
        checkExtractNanos(15L, 720000000, value);
    }

    @Test
    public void testExtractNanosecondDecimal04()
    {
        BigDecimal value = new BigDecimal("19827342231.192837465");
        checkExtractNanos(19827342231L, 192837465, value);
    }

    @Test
    public void testExtractNanosecondDecimal05()
    {
        BigDecimal value = new BigDecimal("19827342231");
        checkExtractNanos(19827342231L, 0, value);
    }

    @Test
    public void testExtractNanosecondDecimal06()
    {
        BigDecimal value = new BigDecimal("19827342231.999999999");
        checkExtractNanos(19827342231L, 999999999, value);
    }


    private void checkExtractSecondsAndNanos(long expectedSeconds, int expectedNanos, BigDecimal decimal)
    {
        DecimalUtils.extractSecondsAndNanos(decimal, (Long s, Integer ns) -> {
            assertEquals("The second part is not correct.", expectedSeconds, s.longValue());
            assertEquals("The nanosecond part is not correct.", expectedNanos, ns.intValue());
            return null;
        });
    }

    @Test
    public void testExtractSecondsAndNanos01()
    {
        BigDecimal value = new BigDecimal("0");
        checkExtractSecondsAndNanos(0L, 0, value);
    }

    @Test
    public void testExtractSecondsAndNanos02()
    {
        BigDecimal value = new BigDecimal("15.000000072");
        checkExtractSecondsAndNanos(15L, 72, value);
    }

    @Test
    public void testExtractSecondsAndNanos03()
    {
        BigDecimal value = new BigDecimal("15.72");
        checkExtractSecondsAndNanos(15L, 720000000, value);
    }

    @Test
    public void testExtractSecondsAndNanos04()
    {
        BigDecimal value = new BigDecimal("19827342231.192837465");
        checkExtractSecondsAndNanos(19827342231L, 192837465, value);
    }

    @Test
    public void testExtractSecondsAndNanos05()
    {
        BigDecimal value = new BigDecimal("19827342231");
        checkExtractSecondsAndNanos(19827342231L, 0, value);
    }

    @Test
    public void testExtractSecondsAndNanos06()
    {
        BigDecimal value = new BigDecimal("19827342231.999999999");
        checkExtractSecondsAndNanos(19827342231L, 999999999, value);
    }

    @Test(timeout = 100)
    public void testExtractSecondsAndNanos07()
    {
        BigDecimal value = new BigDecimal("1e10000000");
        checkExtractSecondsAndNanos(0L, 0, value);
    }
}
