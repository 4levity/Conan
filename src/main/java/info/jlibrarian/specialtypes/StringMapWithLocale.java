package info.jlibrarian.specialtypes;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class StringMapWithLocale extends StringMap {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Locale loc;

    public StringMapWithLocale(String language) {
        initLocale(language);
    }
    public StringMapWithLocale() {
        initLocale(null);
    }

    public Locale getLocale() {
        return loc;
    }

    private boolean initLocale(String language) {
    	boolean success=false;
        String iso839_1_lang;
        if(language == null) {
            this.loc = null;
        } else if(language.isEmpty() || language.equalsIgnoreCase("xxx") || language.equalsIgnoreCase("xx")) {
        	this.loc = null;
        	success=true;
        } else if(language.length()==3) {
        	// TODO: once you get the language map fixed, clean up this processing for 2/3 char codes
            try {
                iso839_1_lang = LocaleSet.iso639_2_to_iso639_1(language);
                this.loc = new Locale(iso839_1_lang);
                success=true;
            } catch (UnsupportedEncodingException ex) {
                this.loc = null;
            }
        } else if(language.length()==2) {
        	iso839_1_lang=language.toLowerCase(Locale.ENGLISH);
            this.loc = new Locale(iso839_1_lang);
            if(this.loc != null) {
            	success=true;
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return (loc==null?"":"["+loc.toString()+"] ")+super.toString();
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((loc == null) ? 0 : loc.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringMapWithLocale other = (StringMapWithLocale) obj;
		if (loc == null) {
			if (other.loc != null)
				return false;
		} else if (!loc.equals(other.loc))
			return false;
		return true;
	}
	@Override
	public boolean setFromString(String value) {
		if(value.length()>4) {
			if(value.charAt(3)==0) {
				if(this.initLocale(value.substring(0, 3))) {
					value=value.substring(4);
				}
			}
		}
		return super.setFromString(value);
	}
}
