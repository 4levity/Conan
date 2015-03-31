package info.jlibrarian.stringutils; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

/**
 * This is a collection of static string functions.
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public class StringUtils {
	/**
	 * Remove control characters from input string by replacing with ?. 
	 * 
	 * @param s	string which may contain control characters
	 * @return copy of string which does not contain control characters
	 */
	public static String stripControlCharacters(String s) {
		return s.replaceAll("\\p{Cntrl}","?");
	}
	
	/**
	 * Wraps a long string into multiple lines by inserting line separators.
	 * 
	 * @param src	input string
	 * @param width	column to wrap
	 * @return		string with line separators inserted
	 */
	public static String lineWrap(String src,int width) {
		String result="";
		int ix=0;
		while(ix+width < src.length()) {
			int nextSplit = src.indexOf(System.lineSeparator(), ix);
			if(nextSplit<0 || nextSplit>width) {
				result += src.substring(ix,ix+width);
				result += System.lineSeparator();
				ix+=width;
			} else {
				result += src.substring(ix,ix+nextSplit+1);
				ix+=nextSplit+1;
			}
		}
		result += src.substring(ix);
		
		return result;
	}
	
	/**
	 * Returns the portion of a string that can be interpreted as a number, or blank string.
	 * 
	 * @param src	string to evaluate
	 * @return	string in the format of a number with no trailing garbage, or, blank string
	 */
	public static String trimNonNumeric(String src/*boolean allowNegative,boolean allowDecimal*/) {
		if(src==null) {
			return ""; // cuz I'm a nice guy
		}
		if(src.isEmpty()) {
			return src;
		}
		if(src.length()==1) {
			return src.matches("\\d")?src:"";
		}
		if(!src.matches("[-]{0,1}\\d+\\.{0,1}\\d+.*")) {
			// TODO: did I get that right? it does seem to let pass valid strings at least..
			return "";
		}
		for(int ix=1;ix<src.length();ix++) {
			char c=src.charAt(ix);
			if(!Character.isDigit(c) && c!='.') {
				return src.substring(0, ix);
			}
		}
		return src;
	}
	
	/**
	 * Convert an Object to a Long (Object->String->strip->parse)
	 * 
	 * @param obj	the object to convert
	 * @return	new Long, or null if conversion failed
	 */
	public static Long toLong(Object obj) {
		if(obj==null)
			return null;
    	String string=StringUtils.trimNonNumeric(obj.toString());
    	Long o=null;
        try {
            o=new Long(Long.parseLong(string));
        } catch (NumberFormatException ex) {
        }
        return o;
	}
	
	/**
	 * Convert an Object to an Integer (Object->String->strip->parse)
	 * 
	 * @param obj	the object to convert
	 * @return	new Integer, or null if conversion failed
	 */
	public static Integer toInteger(Object obj) {
		if(obj==null)
			return null;
    	String string=StringUtils.trimNonNumeric(obj.toString());
    	Integer o=null;
        try {
            o=new Integer(Integer.parseInt(string));
        } catch (NumberFormatException ex) {
        }
        return o;
	}

	/**
	 * Convert an Object to a Float (Object->String->strip->parse)
	 * 
	 * @param obj	the object to convert
	 * @return	new Float, or null if conversion failed
	 */
	public static Float toFloat(Object obj) {
		if(obj==null)
			return null;
    	String string=StringUtils.trimNonNumeric(obj.toString());
    	Float o=null;
        try {
            o=new Float(Float.parseFloat(string));
        } catch (NumberFormatException ex) {
        }
        return o;
	}

	/**
	 * Convert an Object to a Double (Object->String->strip->parse)
	 * 
	 * @param obj	the object to convert
	 * @return	new Double, or null if conversion failed
	 */
	public static Double toDouble(Object obj) {
		if(obj==null)
			return null;
    	String string=StringUtils.trimNonNumeric(obj.toString());
    	Double o=null;
        try {
            o=new Double(Double.parseDouble(string));
        } catch (NumberFormatException ex) {
        }
        return o;
	}
}
