package org.cmdbuild.services.soap.test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;
import static org.cmdbuild.common.Constants.SOAP_ALL_DATES_FORMAT;
import static org.cmdbuild.common.Constants.SOAP_ALL_DATES_PRINTING_PATTERN;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DateConversionTest {

    @Test
    public void testDateConversion1() {
        Date date = toJavaDate(LocalDate.of(2009, 3, 30));
        assertEquals("2009-03-30", toIsoDate(date));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SOAP_ALL_DATES_PRINTING_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        String value = simpleDateFormat.format(date);
        assertEquals("2009-03-30T00:00:00", value);
    }
    
    @Test
    public void testDateConversion2(){
        Date date = toJavaDate("2012-11-15T18:00:00");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SOAP_ALL_DATES_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String value = simpleDateFormat.format(date);
        assertEquals("2012-11-15T18:00:00", value);
    }

}
