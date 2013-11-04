package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.stringutils.SettableFromString;

import java.text.SimpleDateFormat;

/**
 * Automatic precision date+time object with iso 8601 support.  No time zone.
 * 
 * Precision controls output format and available fields and is defined by 
 * the first un-set field.  e.g. if obj represents 2004-04-04 the precision is DAY.
 * 
 * If you have set year=2004 and time=02:45, the time value will be retained
 * privately, but the object will act like it has precision YEAR until you 
 * fill in the day and month, and then precision will become MINUTE
 * 
 * @author ivan
 */
public class VariablePrecisionTime implements SettableFromString {	
    public enum Precision {
        SECOND ("yyyy-MM-dd'T'HH:mm:ss","\\d{4}-\\d{2}-\\d{2}[Tt:]\\d{2}:\\d{2}:\\d{2}",19),
        MINUTE ("yyyy-MM-dd'T'HH:mm","\\d{4}-\\d{2}-\\d{2}[Tt:]\\d{2}:\\d{2}",16),
        DAY ("yyyy-MM-dd","\\d{4}-\\d{2}-\\d{2}",10),
        MONTH ("yyyy-MM","\\d{4}-\\d{2}",7),
        YEAR ("yyyy","\\d{4}",4),
        NONE ("'N'","",0);
        ;
        protected SimpleDateFormat formatter;
        protected String regex;
        protected int expectedStringLength;
        Precision(String fmt,String regex,int expectedStringLength) {
            this.formatter=new SimpleDateFormat(fmt);
            this.regex=regex;
            this.expectedStringLength=expectedStringLength;
        }
	public SimpleDateFormat getFormatter() {
		return formatter;
	}
	public String getRegex() {
		return regex;
	}
	public int getExpectedStringLength() {
		return expectedStringLength;
	}
    }
    protected int year;
    protected byte month;
    protected byte day;
    protected byte hour;
    protected byte minute;
    protected byte second;
    protected String extraData;
    
    /**
     * Create object from ISO 8601 string 
     * @param date formatted with variable precision in ISO 8601 style
     * @return 	
     */
    public VariablePrecisionTime(String iso8601date) {
    	reset();
    	setIso8601Date(iso8601date);
    }
    /**
     * Create empty date object (precision=none) 
     * @return 	
     */
    public VariablePrecisionTime() {
    	reset();
    }
    /**
     * copy constructor
     * @param object to copy
     * @return 	
     */
    public VariablePrecisionTime(VariablePrecisionTime copy) {
    	this.year=copy.year;
    	this.month=copy.month;
    	this.day=copy.day;
    	this.hour=copy.hour;
    	this.minute=copy.minute;
    	this.second=copy.second;
   		this.extraData=copy.extraData;
	}
    /**
     * sets the value of this object from an ISO 8601 variable precision date string in the
     * format YYYY or YYYY-DD or YYYY-DD-MM or YYYY-DD-MMTHH:MM or YYYY-DD-MMTHH:MM:SS
     * 
     * any extra string data after the ISO time string will be placed in extraData. one 
     * white space separating the time and extra data will be skipped if present. if no
     * extra data exists the value of extraData will be null.
     * 
     * if a pattern match occurs, the value of this object will be reset. if no match occurs
     * the value will stay the same.
     * 
     * @param date/time stamp string in ISO8601 or partial ISO8601 format
     * @returns true if a pattern match occured on one of the above time formats, false if not.
     */
    private boolean setIso8601Date(String s) {
    	for(Precision p : Precision.values()) {
    		// depending on the iteration order of the enum here (should be declaration order)
    		if(s.length()>=p.expectedStringLength) {
            	if(s.substring(0, p.expectedStringLength).matches(p.regex)) {
            		if(s.length()>( p.expectedStringLength+1 )) {
            			if(s.substring(p.expectedStringLength, p.expectedStringLength+1 ).equals(" ")) { 
            				this.extraData = s.substring(p.expectedStringLength+1);
            			}
            		}
            		return setPrecision(s,p);
            	}
    		}
    	}
    	return false;
	}
	private boolean setPrecision(String s, Precision p) {
		// caller first validates string length 
		// and that numeric digits are in correct position

		reset();
		if(p.compareTo(Precision.YEAR)<=0) {
			int y=Integer.parseInt(s.substring(0,4));
			if(y<0 || y>10000) {
				return false;
			}
			this.year=y;
		} 
		if(p.compareTo(Precision.MONTH)<=0) {
			int m=(byte)Integer.parseInt(s.substring(5,7));
			if(m<1 || m>12) {
				return false;
			}
			this.month=(byte)m;
		} 
		if(p.compareTo(Precision.DAY)<=0) { 
			int d=(byte)Integer.parseInt(s.substring(8,10));
			if(d<1 || d>31) {
				return false;
			}
			this.day=(byte)d;
		} 
		if(p.compareTo(Precision.MINUTE)<=0) { 
			int h=(byte)Integer.parseInt(s.substring(11,13));	
			int m=(byte)Integer.parseInt(s.substring(14,16));
			if(h<0 || h>23 || m<0 || m>59) {
				return false;
			}
			this.hour=(byte)h;
			this.minute=(byte)m;
		} 
		if(p.compareTo(Precision.SECOND)==0) { 
			int sec=(byte)Integer.parseInt(s.substring(17,19));
			if(sec<0 || sec>60) {
				return false;
			}
			this.second=(byte) sec;
		}
		return true;
	}
    /**
     * remove all date/time data and set precision to "none"
     * @return 	
     */
    public void reset() {
    	this.year=-1;
    	this.month=-1;
    	this.day=-1;
    	this.hour=-1;
    	this.minute=-1;
    	this.second=-1;
    	this.extraData=null;
    }
    /**
     * Set just the month and the day from a numeric string. 
     * @param month/day as 4 character string
     * @return true if successful
     */
    public boolean set_MMDD(String MMDD) {
    	if(MMDD.matches("\\d\\d\\d\\d")) {
    		int m = (byte)Integer.parseInt(MMDD.substring(0, 2));
    		int d = (byte)Integer.parseInt(MMDD.substring(2, 4));
    		if(!(m<1 || m>12 || d<1 || d>31)) {
    			this.month=(byte) m;
    			// TODO: validate day of month
    			this.day=(byte) d;
    			return true;
    		}
    	}
    	return false;
    }    
    /**
     * Set just the hour and minute from a numeric string. 
     * @param hour/minute as 4 character string
     * @return true if successful
     */
    public boolean set_hhmm(String hhmm) {
    	if(hhmm.matches("\\d\\d\\d\\d")) {
    		int h= (byte)Integer.parseInt(hhmm.substring(0, 2));
    		int m= (byte)Integer.parseInt(hhmm.substring(2, 4));
			if(h>=0 && h<24 && m>=0 && m<60) {
				this.hour=(byte) h;
				this.minute=(byte) m;
				return true;
			}
    	}
    	return false;
    }
    /**
     * Set just the year from a numeric string. 
     * @param year as 4 character string
     * @return true if successful
     */
    public boolean set_Year(String year) {    	
    	if(year.matches("\\d\\d\\d\\d")) {
    		int y=Integer.parseInt(year);
    		if(y>0 && y<10000) {
    			this.year=y;
    			return true;
    		}
    	}
    	return false;
    }


    /**
     * Generate an ISO8601 date/time string with variable precision depending on what data we have
     * @param 
     * @return string representation of date
     */
	@Override
	public String toString() {
		String s="";
		Precision p=getPrecision();
		if(p.compareTo(Precision.YEAR)<=0) {
			s+=String.format("%04d", year);
		}
		if(p.compareTo(Precision.MONTH)<=0) {
			s+=String.format("-%02d", month);
		}
		if(p.compareTo(Precision.DAY)<=0) {
			s+=String.format("-%02d", day);
		}
		if(p.compareTo(Precision.MINUTE)<=0) {
			s+=String.format("T%02d:%02d", hour, minute);
		}
		if(p.compareTo(Precision.SECOND)<=0) {
			s+=String.format(":%02d", second);
		}
		if(this.extraData!=null) {
			s+=" "+this.extraData;
		}
		return s;
	}
    /**
     * Determine the precision by examining which fields are available
     * @return current Precision of object
     */
	public Precision getPrecision() {
		if(year<0)
			return Precision.NONE;
		if(month<0) 
			return Precision.YEAR;
		if(day<0)
			return Precision.MONTH;
		if(hour<0 || minute<0) 
			return Precision.DAY;
		if(second<0)
			return Precision.MINUTE;
		return Precision.SECOND;
	}
    /**
     * Set the value of this object from an input string, discarding existing value
     * @return true if successful
     */
	@Override
	public boolean setFromString(String value) {
		return this.setIso8601Date(value);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + hour;
		result = prime * result + minute;
		result = prime * result + month;
		result = prime * result + second;
		result = prime * result + year;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariablePrecisionTime other = (VariablePrecisionTime) obj;
		if (day != other.day)
			return false;
		if (hour != other.hour)
			return false;
		if (minute != other.minute)
			return false;
		if (month != other.month)
			return false;
		if (second != other.second)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	public void setExtraData(String extraData) {
		this.extraData=extraData;
	}
	public String getExtraData() {
		return this.extraData;
	}
    
}
