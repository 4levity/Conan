package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.stringutils.SettableFromString;

import java.text.SimpleDateFormat;

/**
 * Automatic variable-precision date+time object with partial ISO 8601 support (see TODOs).  
 * Can only contain consistent dates/times (e.g. cannot contain 2001-02-29). 
 * 
 * 'Precision' is defined as the field before the first un-set field. Precision controls output format. 
 * 
 * e.g. if the obj represents 2004-04-04T??:??:?? we would say the precision is DAY.
 * But if you have set only year and day ("2004-??-04T??:??:??") then precision is YEAR 
 * until you also set the month to a value, then precision will become DAY.
 * 
 * @author ivan
 */
public class VariablePrecisionTime implements SettableFromString {	
    public enum Precision {
        SECOND ("yyyy-MM-dd'T'HH:mm:ss","\\d{4}-\\d{2}-\\d{2}[Tt:]\\d{2}:\\d{2}:\\d{2}",19),
        MINUTE ("yyyy-MM-dd'T'HH:mm","\\d{4}-\\d{2}-\\d{2}[Tt:]\\d{2}:\\d{2}",16),
        HOUR ("yyyy-MM-dd'T'HH","\\d{4}-\\d{2}-\\d{2}[Tt:]\\d{2}",13),
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
    public boolean setIso8601Date(String s) {
    	// TODO: support alternate ISO 8601 date/time format e.g. no dash between y-m-d, no colon between hh:mm:ss
    	// TODO: support ISO 8601 fractional time with decimal point
    	// TODO: support ISO 8601 time zone designation
    	// TODO: support ISO 8601 week number specification
    	// TODO: support ISO 8601 ordinal date specification
    	// TODO: support ISO 8601 "midnight" 24:00
    	
    	// first, try to evaluate SECOND precision e.g. the most precise
    	// then MINUTE, DAY and so on
    	// when a regex match is found, evaluate it and return
    	for(Precision p : Precision.values()) {
    		// depending on the iteration order of the enum here (should be declaration order)
    		if(s.length()>=p.expectedStringLength) {
            	if(s.substring(0, p.expectedStringLength).matches(p.regex)) {
            		if(s.length()>( p.expectedStringLength+1 )) {
            			if(s.substring(p.expectedStringLength, p.expectedStringLength+1 ).equals(" ")) { 
            				this.extraData = s.substring(p.expectedStringLength+1);
            			}
            		}
            		return setIso8601Date(s,p);
            	}
    		}
    	}
    	return false; // input does not look like complete or partial ISO 8601 date
	}

    /**
     * determine whether the given combination of year/month/day represents a valid past/future date
     * if any value is negative, it will be ignored (assumed valid). if all values are negative, return true.
     * @param year
     * @param month
     * @param day
     * @return false if invalid, true otherwise
     */
    static public boolean validYMD(int year,int month,int day) {
    	// non-interdependent checks
		if(year>9999)
			return false;
		if(month==0 || month>12)
			return false;
		if(day==0 || day>31)
			return false;
    	    	
    	if(month>0) {
			// check if day is invalid for this month/year
			if(month==2) {
				// feb
				if(day>29) return false;
				if(year>0 && day==29) {
					if(((year%4!=0)||(year%100==0))&&(year%400!=0) )
						// not leap year
						return false;
				}
			} else if(day>30 && (month==4 || month==6 || month==9 || month==11)) {
				// apr/jun/sep/nov
				return false;
			}
		}
    	return true;
    }
    /** 
     * Set one of the fields without affecting the value of others. Will fail if
     * resulting date does not make sense, for example:

     *  vpt.reset(); // precision is now NONE
     *  vpt.set(Precision.HOUR,13); // precision is still MONTH (because day is unknown)
     *  vpt.set(Precision.YEAR,2013); // precision is now YEAR
     *  vpt.set(Precision.MONTH,2); // precision is now MONTH
     *  vpt.set(Precision.DAY,29); // this will fail because month=2 and year is not a leap year
     * 
     */
    public boolean set(Precision p,int value) {
		if(p.compareTo(Precision.YEAR)==0) {
			if(!validYMD(value,month,day)) {
				return false;
			}
			this.year=value;
		} else if(p.compareTo(Precision.MONTH)==0) {
			if(!validYMD(year,value,day)) {
				return false;
			}
			this.month=(byte)value;
		} else if(p.compareTo(Precision.DAY)==0) { 
			if(!validYMD(year,month,value)) { 
				return false;
			}
			this.day=(byte)value;
		} else if(p.compareTo(Precision.HOUR)==0) { 
			if(value<0 || value>23) {
				return false;
			}
			this.hour=(byte)value;
		} else if(p.compareTo(Precision.MINUTE)==0) { 
			if(value<0 || value>59) {
				return false;
			}
			this.minute=(byte)value;
		} else if(p.compareTo(Precision.SECOND)==0) { 
			if(value<0 || value>59) {
				return false;
			}
			this.second=(byte) value;
		}
		return true;
    }
    
    /**
     * Sets value of date from partial/complete ISO 8601 at a certain precision.
     * @param s date/time stamp string in ISO8601 or partial ISO8601 format
     * @param p Precision of input string
     * @returns true if time set to requested precision, false if an error occurred
     */
	public boolean setIso8601Date(String s, Precision p) {
		// caller first validates string length 
		// and that numeric digits are in correct position

		reset();
		if(p.compareTo(Precision.YEAR)<=0) {
			if( !set(Precision.YEAR,Integer.parseInt(s.substring(0,4))) ) {
				return false;
			}
		} 
		if(p.compareTo(Precision.MONTH)<=0) {
			if( !set(Precision.MONTH,Integer.parseInt(s.substring(5,7))) ) {
				return false;
			}
		} 
		if(p.compareTo(Precision.DAY)<=0) { 
			if( !set(Precision.DAY,Integer.parseInt(s.substring(8,10))) ) {
				return false;
			}
		} 
		if(p.compareTo(Precision.HOUR)<=0) { 
			if( !set(Precision.HOUR,Integer.parseInt(s.substring(11,13))) ) {	
				return false;
			}
		} 
		if(p.compareTo(Precision.MINUTE)<=0) { 
			if( !set(Precision.MINUTE, Integer.parseInt(s.substring(14,16))) ) {
				return false;
			}
		} 
		if(p.compareTo(Precision.SECOND)==0) { 
			if( !set(Precision.SECOND, Integer.parseInt(s.substring(17,19))) ) {
				return false;
			}
		}
		return true;
	}
    /**
     * remove all date/time data which also makes precision "none"
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
     * Assume input is complete/partial ISO 8601 date
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
