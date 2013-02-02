package info.jlibrarian.stringutils; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

public class StringUtils {
	public static String stripControlCharacters(String s) {
		return s.replaceAll("\\p{Cntrl}","?");
	}
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
}
