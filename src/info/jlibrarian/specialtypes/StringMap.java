package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Level;

/*
 * defines a value that is a map KEY -> VALUE of strings 
 * including a locale for interpreting the strings
 * e.g. id3 comment frame
 */
public class StringMap extends java.util.HashMap<String, String> {
	private static final long serialVersionUID = 9143105296565963435L;

	private Locale loc;

    public StringMap(String language,PropertyTree<?> owner) {
        initStringMap(language,owner);
    }
    public StringMap(String language) {
        initStringMap(language,null);
    }
    public StringMap(PropertyTree<?> owner) {
        initStringMap(null,owner);
    }
    public StringMap() {
        initStringMap(null,null);
    }


    private void initStringMap(String language,PropertyTree<?> owner) {
        String iso839_1_lang;
        if(language == null) {
            this.loc = null;
        } else if(language.isEmpty() || language.equalsIgnoreCase("xxx") || language.equalsIgnoreCase("xx")) {
        	this.loc = null;
        } else if(language.length()==3) {
        	// TODO: once you get the language map fixed, clean up this processing for 2/3 char codes
            try {
                iso839_1_lang = LocaleSet.iso639_2_to_iso639_1(language);
                this.loc = new Locale(iso839_1_lang);
            } catch (UnsupportedEncodingException ex) {
                this.loc = null;
                if(owner!=null)
                    owner.log(Level.WARNING,"Invalid language "+StringUtils.stripControlCharacters(language)
                    		+" initializing StringMap");
            }
        } else if(language.length()==2) {
        	iso839_1_lang=language.toLowerCase(Locale.ENGLISH);
            this.loc = new Locale(iso839_1_lang);
        }

    }

    public Locale getLocale() {
        return loc;
    }

    @Override
    public String toString() {
        return (loc==null?"":"["+loc.toString()+"] ")+super.toString();
    }
}
