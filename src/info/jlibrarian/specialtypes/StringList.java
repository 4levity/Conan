package info.jlibrarian.specialtypes;

import info.jlibrarian.stringutils.SettableFromString;

/* 
 * List of strings that can dynamically grow in size (but is often just one string).
 * 
 * toString returns just the first string in the list.
 */
public class StringList implements SettableFromString {
	String[] strings;
	int numEntries;
	public StringList() {
		this.strings=null;
		this.numEntries=0;
	}
	public int getNumEntries() {
		return numEntries;
	}
	public String getEntry(int ix) {
		return strings[ix];
	}
	@Override
	public boolean setFromString(String value) {
		this.strings=value.split("\\x00");
		if(strings.length==0) {
			this.strings=null;
		}
		return true;
	}	
	@Override
	public String toString() {
		// return first string
		if(strings==null) {
			return "(null)";
		} //else
		return strings[0];
	}
	
	// return all strings separated by specified separator
	public String toString(String separator) {
		String result="";
		int i;
		for(i=0;i<(strings.length-1);i++) {
			result += strings[i]+separator;
		}
		if(i<strings.length) {
			result += strings[i];
		}
		return result;
	}
	
}
