package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

    private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

    @Test
    public void testEndOfYearDates() {
        LocalDateTime date = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
        assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatInstant(
                date.atZone(ZoneId.of("UTC")).toInstant(), "UTC", DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testFormatDateTimeForDisplay() {
        String zoneId = "UTC";
        Instant instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        zoneId = "Asia/Singapore";
        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 PM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

        instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).atZone(ZoneId.of(zoneId)).toInstant();
        assertEquals("Mon, 30 Nov 2015, 04:00 AM SGT", TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));
    }


    @Test
    public void testGetMidnightAdjustedInstantBasedOnZoneMCDC() {
        String zoneId = "UTC";
        // Tests for forward adjustment
        Instant notBeforeMidnightByHour = LocalDateTime.of(2015, Month.NOVEMBER, 30, 22, 59).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedNotBeforeMidnightByHour = TimeHelper.getMidnightAdjustedInstantBasedOnZone(notBeforeMidnightByHour, zoneId, false);
        assertEquals("Mon, 30 Nov 2015, 10:59 PM UTC",
                TimeHelper.formatInstant(notAdjustedNotBeforeMidnightByHour, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant notBeforeMidnightByMinute = LocalDateTime.of(2015, Month.NOVEMBER, 30, 23, 1).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedNotBeforeMidnightByMinute = TimeHelper.getMidnightAdjustedInstantBasedOnZone(notBeforeMidnightByMinute, zoneId, false);
        assertEquals("Mon, 30 Nov 2015, 11:01 PM UTC",
                TimeHelper.formatInstant(notAdjustedNotBeforeMidnightByMinute, zoneId, DATETIME_DISPLAY_FORMAT));
        
        Instant beforeMidnight = LocalDateTime.of(2015, Month.NOVEMBER, 30, 23, 59).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedBeforeMidnight = TimeHelper.getMidnightAdjustedInstantBasedOnZone(beforeMidnight, zoneId, false);
        assertEquals("Mon, 30 Nov 2015, 11:59 PM UTC",
                TimeHelper.formatInstant(notAdjustedBeforeMidnight, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant adjustedBeforeMidnight = TimeHelper.getMidnightAdjustedInstantBasedOnZone(beforeMidnight, zoneId, true);
        assertEquals("Tue, 01 Dec 2015, 12:00 AM UTC",
                TimeHelper.formatInstant(adjustedBeforeMidnight, zoneId, DATETIME_DISPLAY_FORMAT));

        // Tests for backward adjustment

        Instant notMidnightByHour = LocalDateTime.of(2015, Month.NOVEMBER, 30, 1, 0).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedMidnightByHour = TimeHelper.getMidnightAdjustedInstantBasedOnZone(notMidnightByHour, zoneId, false);
        assertEquals("Mon, 30 Nov 2015, 01:00 AM UTC",
                TimeHelper.formatInstant(notAdjustedMidnightByHour, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant notMidnightByMinute = LocalDateTime.of(2015, Month.NOVEMBER, 30, 0, 1).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedMidnightByMinute = TimeHelper.getMidnightAdjustedInstantBasedOnZone(notMidnightByMinute, zoneId, false);
        assertEquals("Mon, 30 Nov 2015, 12:01 AM UTC",
                TimeHelper.formatInstant(notAdjustedMidnightByMinute, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant midnight = LocalDateTime.of(2015, Month.NOVEMBER, 30, 0, 0).atZone(ZoneId.of(zoneId)).toInstant();
        Instant notAdjustedMidnight = TimeHelper.getMidnightAdjustedInstantBasedOnZone(midnight, zoneId, true);
        assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
                TimeHelper.formatInstant(notAdjustedMidnight, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant adjustedMidnight = TimeHelper.getMidnightAdjustedInstantBasedOnZone(midnight, zoneId, false);
        assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
                TimeHelper.formatInstant(adjustedMidnight, zoneId, DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testGetMidnightAdjustedInstantBasedOnZone() {
        String zoneId = "UTC";
        Instant instantAt0000 = LocalDateTime.of(2015, Month.NOVEMBER, 30, 0, 0).atZone(ZoneId.of(zoneId)).toInstant();

        Instant backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, zoneId, false);
        assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
                TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, zoneId, true);
        assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
                TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        Instant instantAt2359 = LocalDateTime.of(2015, Month.NOVEMBER, 29, 23, 59).atZone(ZoneId.of(zoneId)).toInstant();

        backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, zoneId, false);
        assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
                TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, zoneId, true);
        assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
                TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        String wrongTimeZone = "Asia/Singapore";

        backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt0000, wrongTimeZone, false);
        assertEquals("Mon, 30 Nov 2015, 12:00 AM UTC",
                TimeHelper.formatInstant(backwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));

        forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instantAt2359, wrongTimeZone, true);
        assertEquals("Sun, 29 Nov 2015, 11:59 PM UTC",
                TimeHelper.formatInstant(forwardAdjusted, zoneId, DATETIME_DISPLAY_FORMAT));
    }

    @Test
    public void testGetInstantNearestHourBefore() {
        Instant expected = Instant.parse("2020-12-31T16:00:00Z");
        Instant actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:00:00Z"));

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:10:00Z"));

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(OffsetDateTime.parse("2021-01-01T00:30:00+08:00").toInstant());

        assertEquals(expected, actual);

        actual = TimeHelper.getInstantNearestHourBefore(OffsetDateTime.parse("2020-12-31T12:59:00-04:00").toInstant());

        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantDaysOffsetFromNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantDaysOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().plus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantDaysOffsetFromNow(365).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantDaysOffsetBeforeNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantDaysOffsetBeforeNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().minus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantDaysOffsetBeforeNow(365).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantHoursOffsetFromNow() {
        // Comparison using second precision is sufficient
        Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant actual = TimeHelper.getInstantHoursOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);

        expected = Instant.now().plus(Duration.ofHours(60)).truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantHoursOffsetFromNow(60).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetInstantMonthsOffsetFromNow() {
        Instant expected = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant actual = TimeHelper.getInstantMonthsOffsetFromNow(0, Const.DEFAULT_TIME_ZONE)
                .truncatedTo(ChronoUnit.DAYS);
        assertEquals(expected, actual);

        Instant now = Instant.now();
        ZonedDateTime zdt = now.atZone(ZoneId.of(Const.DEFAULT_TIME_ZONE));
        ZonedDateTime offsetZdt = zdt.plusMonths(12);
        expected = offsetZdt.toInstant().truncatedTo(ChronoUnit.SECONDS);
        actual = TimeHelper.getInstantMonthsOffsetFromNow(12, Const.DEFAULT_TIME_ZONE).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(expected, actual);
    }

    @Test
    public void getInstantNearestQuarterHourBefore() {
        Instant expectedQ1 = Instant.parse("2020-12-31T16:00:00Z");
        Instant actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:00:00Z"));

        assertEquals(expectedQ1, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:09:30Z"));

        assertEquals(expectedQ1, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:14:59Z"));

        assertEquals(expectedQ1, actual);

        actual = TimeHelper
                .getInstantNearestQuarterHourBefore(OffsetDateTime.parse("2021-01-01T00:10:00+08:00").toInstant());

        assertEquals(expectedQ1, actual);

        actual = TimeHelper
                .getInstantNearestQuarterHourBefore(OffsetDateTime.parse("2020-12-31T12:09:00-04:00").toInstant());

        assertEquals(expectedQ1, actual);

        Instant expectedQ2 = Instant.parse("2020-12-31T16:15:00Z");
        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:15:00Z"));

        assertEquals(expectedQ2, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:19:30Z"));

        assertEquals(expectedQ2, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:29:59Z"));

        assertEquals(expectedQ2, actual);

        Instant expectedQ3 = Instant.parse("2020-12-31T16:30:00Z");
        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:30:00Z"));

        assertEquals(expectedQ3, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:39:30Z"));

        assertEquals(expectedQ3, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:44:59Z"));

        assertEquals(expectedQ3, actual);

        Instant expectedQ4 = Instant.parse("2020-12-31T16:45:00Z");
        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:45:00Z"));

        assertEquals(expectedQ4, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:49:30Z"));

        assertEquals(expectedQ4, actual);

        actual = TimeHelper.getInstantNearestQuarterHourBefore(Instant.parse("2020-12-31T16:59:59Z"));

        assertEquals(expectedQ4, actual);
    }
}
