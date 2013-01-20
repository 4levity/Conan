package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.text.SimpleDateFormat;

/**
 * Automatic precision date+time object with iso 8601 support.  No time zone.
 * 
 * Precision controls output format and available fields and is defined by 
 * first un-set field.  e.g. if it represents 2004-04-04 the precision is DAY.
 * 
 * If you have set year=2004 and time=02:45, the time value will be retained
 * privately, but the object will act like it has precision YEAR until you 
 * fill in the day and month, and then precision will become MINUTE
 * 
 * @author ivan
 */
public class Iso8601Date {
    public enum Precision {
        SECONDS ("yyyy-MM-dd'T'HH:mm:ss"),
        MINUTES ("yyyy-MM-dd'T'HH:mm"),
        DAY ("yyyy-MM-dd"),
        MONTH ("yyyy-MM"),
        YEAR ("yyyy"),
        NONE ("'N'");
        public SimpleDateFormat format;
        Precision(String fmt) {
            format=new SimpleDateFormat(fmt);
        }
    }
    protected Precision precision=Precision.NONE;
    public Iso8601Date(String iso8601date) {
        // set date from string, dur
    }
    public Iso8601Date() {
        precision=Precision.NONE;
    }
    public void set_MMDD(String MMDD) {
        
    }    
    public void set_hhmm(String hhmm) {
        
    }
    public void set_Year(int year) {
        
    }
    public void set_Year(String year) {
        
    }
}
