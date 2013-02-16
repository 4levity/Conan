package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.stringutils.SettableFromString;

/*
 * defines a value that is a map KEY -> VALUE of strings 
 * (see also subclass StringMapWithLocale)
 * 
 * if initialized with odd number of strings, last will be key to empty value.
 * 
 */
public class StringMap extends java.util.HashMap<String, String> 
		implements SettableFromString {
	
	private static final long serialVersionUID = 9143105296565963435L;

    public StringMap() {
    	super();
    }
    public StringMap(String strings) {
    	super();
    	this.setFromString(strings);
    }

	@Override
	public boolean setFromString(String value) {
		this.clear();
		String[] strings=value.split("\\x00");
		if(strings.length==0) {
			return false;
		}
		if(strings.length>1) {
			for(int i=0;i<(strings.length-1);i+=2) {
				this.put(strings[i], strings[i+1]);
			}
		}
		if(strings.length % 2 != 0) {
			if(!strings[strings.length-1].isEmpty()) {
				this.put(strings[strings.length-1],"");
			}
		}
		return true;
	}
}
