package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Level;

public class StringMap extends java.util.HashMap<String, String> {
	private static final long serialVersionUID = 9143105296565963435L;

	private Locale loc;

    public StringMap(String language,MetaTree<?> owner) {
        initStringMap(language,owner);
    }
    public StringMap(String language) {
        initStringMap(language,null);
    }
    public StringMap(MetaTree<?> owner) {
        initStringMap(null,owner);
    }
    public StringMap() {
        initStringMap(null,null);
    }


    private void initStringMap(String language,MetaTree<?> owner) {
        if(language == null) {
            this.loc = null;
            return;            
        }
        String iso839_1_lang;
        try {
            iso839_1_lang = Iso839_2LocaleSet.Iso839_2_to_Iso839_1(language);
        } catch (UnsupportedEncodingException ex) {
            if(owner!=null)
                owner.log(Level.WARNING,"Invalid language initializing StringMap (default=English)");

            try {
                iso839_1_lang = Iso839_2LocaleSet.Iso839_2_to_Iso839_1("eng");
            } catch (UnsupportedEncodingException ex1) {
                if(owner!=null)
                    owner.log(Level.SEVERE,"Iso839_2_to_Iso839_1() failed for English language.");

                throw new RuntimeException("Iso839_2_to_Iso839_1() failed for English language.",ex1);
            }
        }
        this.loc = new Locale(iso839_1_lang);
    }

    public Locale getLocale() {
        return loc;
    }

    @Override
    public String toString() {
        return (loc==null?"":"["+loc.toString()+"] ")+super.toString();
    }
}
