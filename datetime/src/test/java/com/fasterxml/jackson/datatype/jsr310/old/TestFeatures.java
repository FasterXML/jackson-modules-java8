/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.old;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.*;

public class TestFeatures
{
    @Test
    public void testWriteDateTimestampsAsNanosecondsSettingEnabledByDefault()
    {
        assertTrue(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS.enabledByDefault(),
                "Write date timestamps as nanoseconds setting should be enabled by default.");
    }

    @Test
    public void testReadDateTimestampsAsNanosecondsSettingEnabledByDefault()
    {
        assertTrue(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS.enabledByDefault(),
                "Read date timestamps as nanoseconds setting should be enabled by default.");
    }

    @Test
    public void testAdjustDatesToContextTimeZoneSettingEnabledByDefault()
    {
        assertTrue(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE.enabledByDefault(),
                "Adjust dates to context time zone setting should be enabled by default.");
    }
}
