/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.standalone.calendar;

import static org.assertj.core.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.activiti.engine.impl.calendar.DurationHelper;
import org.activiti.engine.impl.util.DefaultClockImpl;
import org.activiti.engine.runtime.Clock;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

public class DurationHelperTest {

    @Test
    public void shouldNotExceedNumber() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentTime(new Date(0));
        DurationHelper dh = new DurationHelper("R2/PT10S", testingClock);

        testingClock.setCurrentTime(new Date(15000));
        assertThat(dh.getDateAfter().getTime()).isEqualTo(20000);

        testingClock.setCurrentTime(new Date(30000));
        assertThat(dh.getDateAfter().getTime()).isEqualTo(30000);
    }

    @Test
    public void shouldNotExceedNumberPeriods() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentTime(parse("19700101-00:00:00"));
        DurationHelper dh = new DurationHelper("R2/1970-01-01T00:00:00/1970-01-01T00:00:10", testingClock);

        testingClock.setCurrentTime(parse("19700101-00:00:15"));
        assertThat(parse("19700101-00:00:20")).isEqualTo(dh.getDateAfter());

        testingClock.setCurrentTime(parse("19700101-00:00:30"));
        assertThat(parse("19700101-00:00:30")).isEqualTo(dh.getDateAfter());
    }

    @Test
    public void shouldNotExceedNumberNegative() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentTime(parse("19700101-00:00:00"));
        DurationHelper dh = new DurationHelper("R2/PT10S/1970-01-01T00:00:50", testingClock);

        testingClock.setCurrentTime(parse("19700101-00:00:20"));
        assertThat(parse("19700101-00:00:30")).isEqualTo(dh.getDateAfter());

        testingClock.setCurrentTime(parse("19700101-00:00:35"));

        assertThat(parse("19700101-00:00:35")).isEqualTo(dh.getDateAfter());
    }

    @Test
    public void daylightSavingFall() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-04:45:00", TimeZone.getTimeZone("UTC")));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T00:45:00-04:00/PT1H", testingClock);

        assertThat(parseCalendar("20131103-05:45:00", TimeZone.getTimeZone("UTC"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar(TimeZone.getTimeZone("US/Eastern"))));

        testingClock.setCurrentCalendar(parseCalendar("20131103-05:45:00", TimeZone.getTimeZone("UTC")));

        assertThat(parseCalendar("20131103-06:45:00", TimeZone.getTimeZone("UTC"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar(TimeZone.getTimeZone("US/Eastern"))));
    }

    @Test
    public void daylightSavingFallFirstHour() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-05:45:00", TimeZone.getTimeZone("UTC")));
        Calendar easternTime = testingClock.getCurrentCalendar(TimeZone.getTimeZone("US/Eastern"));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T01:45:00-04:00/PT1H", testingClock);

        assertThat(parseCalendar("20131103-06:45:00", TimeZone.getTimeZone("UTC"))).isEqualTo(dh.getCalendarAfter(easternTime));
    }

    @Test
    public void daylightSavingFallSecondHour() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-06:45:00", TimeZone.getTimeZone("UTC")));
        Calendar easternTime = testingClock.getCurrentCalendar(TimeZone.getTimeZone("US/Eastern"));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T01:45:00-05:00/PT1H", testingClock);

        assertThat(parseCalendar("20131103-07:45:00", TimeZone.getTimeZone("UTC"))).isEqualTo(dh.getCalendarAfter(easternTime));
    }

    @Test
    public void daylightSavingFallObservedFirstHour() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-00:45:00", TimeZone.getTimeZone("US/Eastern")));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T00:45:00-04:00/PT1H", testingClock);
        Calendar expected = parseCalendarWithOffset("20131103-01:45:00 -04:00");

        assertThat(expected.compareTo(dh.getCalendarAfter())).isEqualTo(0);
    }

    @Test
    public void daylightSavingFallObservedSecondHour() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-00:45:00", TimeZone.getTimeZone("US/Eastern")));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T00:45:00-04:00/PT2H", testingClock);
        Calendar expected = parseCalendarWithOffset("20131103-01:45:00 -05:00");

        assertThat(expected.compareTo(dh.getCalendarAfter())).isEqualTo(0);
    }

    @Test
    public void daylightSavingSpring() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20140309-05:45:00", TimeZone.getTimeZone("UTC")));

        DurationHelper dh = new DurationHelper("R2/2014-03-09T00:45:00-05:00/PT1H", testingClock);

        assertThat(parseCalendar("20140309-06:45:00", TimeZone.getTimeZone("UTC"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar(TimeZone.getTimeZone("US/Eastern"))));
    }

    @Test
    public void daylightSavingSpringObserved() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20140309-01:45:00", TimeZone.getTimeZone("US/Eastern")));

        DurationHelper dh = new DurationHelper("R2/2014-03-09T01:45:00/PT1H", testingClock);
        Calendar expected = parseCalendar("20140309-03:45:00", TimeZone.getTimeZone("US/Eastern"));

        assertThat(expected).isEqualTo(dh.getCalendarAfter());
    }

    @Test
    public void daylightSaving25HourDay() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131103-00:00:00", TimeZone.getTimeZone("US/Eastern")));

        DurationHelper dh = new DurationHelper("R2/2013-11-03T00:00:00/P1D", testingClock);

        assertThat(parseCalendar("20131104-00:00:00", TimeZone.getTimeZone("US/Eastern"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar()));
    }

    @Test
    public void daylightSaving23HourDay() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20140309-00:00:00", TimeZone.getTimeZone("US/Eastern")));

        DurationHelper dh = new DurationHelper("R2/2014-03-09T00:00:00/P1D", testingClock);

        assertThat(parseCalendar("20140310-00:00:00", TimeZone.getTimeZone("US/Eastern"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar()));
    }

    @Test
    public void daylightSaving25HourDayEurope() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20131027-00:00:00", TimeZone.getTimeZone("Europe/Amsterdam")));

        DurationHelper dh = new DurationHelper("R2/2013-10-27T00:00:00/P1D", testingClock);

        assertThat(parseCalendar("20131028-00:00:00", TimeZone.getTimeZone("Europe/Amsterdam"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar()));
    }

    @Test
    public void daylightSaving23HourDayEurope() throws Exception {
        Clock testingClock = new DefaultClockImpl();
        testingClock.setCurrentCalendar(parseCalendar("20140330-00:00:00", TimeZone.getTimeZone("Europe/Amsterdam")));

        DurationHelper dh = new DurationHelper("R2/2014-03-30T00:00:00/P1D", testingClock);

        assertThat(parseCalendar("20140331-00:00:00", TimeZone.getTimeZone("Europe/Amsterdam"))).isEqualTo(dh.getCalendarAfter(testingClock.getCurrentCalendar()));
    }

    private Date parse(String str) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        return simpleDateFormat.parse(str);
    }

    private Calendar parseCalendarWithOffset(String str) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.parseDate(str, "yyyyMMdd-HH:mm:ss ZZ"));
        return cal;
    }

    private Calendar parseCalendar(String str, TimeZone timeZone) throws Exception {
        return parseCalendar(str, timeZone, "yyyyMMdd-HH:mm:ss");
    }

    private Calendar parseCalendar(String str, TimeZone timeZone, String format) throws Exception {
        Calendar date = new GregorianCalendar(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);
        date.setTime(simpleDateFormat.parse(str));
        return date;
    }
}
