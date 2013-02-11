package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * represent a list of languages (as for id3v2 TLAN tag) 
 * as a HashSet of native Locale objects
 * 
 * also contains static function for handling language code conversion 
 * for ISO 639-2 / ISO 639-1
 * 
 * throws UnsupportedEncodingException throughout - should catch to define behavior
 * 
 * @author ivan
 */
public class LocaleSet extends HashSet<Locale> implements Set<Locale> {
	static HashMap<String,String> iso639_2to1;
	static {
		// TODO: correct and complete list
		/* should allow conversion both ways between ISO 639-2 (id3) to Java Locale 2/3 char codes
		 * (java uses 3 character iso 639-2 codes in cases where 2 character code is not available)
		 */
		iso639_2to1=new HashMap<String,String>();
		iso639_2to1.put("aar","aa");
		iso639_2to1.put("abk","ab");
		iso639_2to1.put("afr","af");
		iso639_2to1.put("amh","am");
		iso639_2to1.put("ara","ar");
		iso639_2to1.put("asm","as");
		iso639_2to1.put("aym","ay");
		iso639_2to1.put("aze","az");
		iso639_2to1.put("bak","ba");
		iso639_2to1.put("bel","be");
		iso639_2to1.put("bul","bg");
		iso639_2to1.put("bih","bh");
		iso639_2to1.put("bis","bi");
		iso639_2to1.put("ben","bn");
		iso639_2to1.put("bod","bo");
		iso639_2to1.put("bre","br");
		iso639_2to1.put("cat","ca");
		iso639_2to1.put("cos","co");
		iso639_2to1.put("ces","cs");
		iso639_2to1.put("cym","cy");
		iso639_2to1.put("dan","da");
		iso639_2to1.put("deu","de");
		iso639_2to1.put("dzo","dz");
		iso639_2to1.put("ell","el");
		iso639_2to1.put("eng","en");
		iso639_2to1.put("epo","eo");
		iso639_2to1.put("spa","es");
		iso639_2to1.put("est","et");
		iso639_2to1.put("eus","eu");
		iso639_2to1.put("fas","fa");
		iso639_2to1.put("fin","fi");
		iso639_2to1.put("fij","fj");
		iso639_2to1.put("fao","fo");
		iso639_2to1.put("fra","fr");
		iso639_2to1.put("fry","fy");
		iso639_2to1.put("gai","ga");
		iso639_2to1.put("gdh","gd");
		iso639_2to1.put("glg","gl");
		iso639_2to1.put("grn","gn");
		iso639_2to1.put("guj","gu");
		iso639_2to1.put("hau","ha");
		iso639_2to1.put("heb","iw");
		iso639_2to1.put("hin","hi");
		iso639_2to1.put("hrv","hr");
		iso639_2to1.put("hun","hu");
		iso639_2to1.put("hye","hy");
		iso639_2to1.put("ina","ia");
		iso639_2to1.put("ind","in");
		iso639_2to1.put("ile","ie");
		iso639_2to1.put("ipk","ik");
		iso639_2to1.put("ind","in");
		iso639_2to1.put("isl","is");
		iso639_2to1.put("ita","it");
		iso639_2to1.put("iku","iu");
		iso639_2to1.put("heb","iw");
		iso639_2to1.put("jpn","ja");
		iso639_2to1.put("yid","ji");
		iso639_2to1.put("jaw","jw");
		iso639_2to1.put("kat","ka");
		iso639_2to1.put("kaz","kk");
		iso639_2to1.put("kal","kl");
		iso639_2to1.put("khm","km");
		iso639_2to1.put("kan","kn");
		iso639_2to1.put("kor","ko");
		iso639_2to1.put("kas","ks");
		iso639_2to1.put("kur","ku");
		iso639_2to1.put("kir","ky");
		iso639_2to1.put("lat","la");
		iso639_2to1.put("lin","ln");
		iso639_2to1.put("lao","lo");
		iso639_2to1.put("lit","lt");
		iso639_2to1.put("lav","lv");
		iso639_2to1.put("mlg","mg");
		iso639_2to1.put("mri","mi");
		iso639_2to1.put("mkd","mk");
		iso639_2to1.put("mal","ml");
		iso639_2to1.put("mon","mn");
		iso639_2to1.put("mol","mo");
		iso639_2to1.put("mar","mr");
		iso639_2to1.put("msa","ms");
		iso639_2to1.put("mlt","mt");
		iso639_2to1.put("mya","my");
		iso639_2to1.put("nau","na");
		iso639_2to1.put("nep","ne");
		iso639_2to1.put("nld","nl");
		iso639_2to1.put("nor","no");
		iso639_2to1.put("oci","oc");
		iso639_2to1.put("orm","om");
		iso639_2to1.put("ori","or");
		iso639_2to1.put("pan","pa");
		iso639_2to1.put("pol","pl");
		iso639_2to1.put("pus","ps");
		iso639_2to1.put("por","pt");
		iso639_2to1.put("que","qu");
		iso639_2to1.put("roh","rm");
		iso639_2to1.put("run","rn");
		iso639_2to1.put("ron","ro");
		iso639_2to1.put("rus","ru");
		iso639_2to1.put("kin","rw");
		iso639_2to1.put("san","sa");
		iso639_2to1.put("snd","sd");
		iso639_2to1.put("sag","sg");
		iso639_2to1.put("srp","sh");
		iso639_2to1.put("sin","si");
		iso639_2to1.put("slk","sk");
		iso639_2to1.put("slv","sl");
		iso639_2to1.put("smo","sm");
		iso639_2to1.put("sna","sn");
		iso639_2to1.put("som","so");
		iso639_2to1.put("sqi","sq");
		iso639_2to1.put("srp","sr");
		iso639_2to1.put("ssw","ss");
		iso639_2to1.put("sot","st");
		iso639_2to1.put("sun","su");
		iso639_2to1.put("swe","sv");
		iso639_2to1.put("swa","sw");
		iso639_2to1.put("tam","ta");
		iso639_2to1.put("tel","te");
		iso639_2to1.put("tgk","tg");
		iso639_2to1.put("tha","th");
		iso639_2to1.put("tir","ti");
		iso639_2to1.put("tuk","tk");
		iso639_2to1.put("tgl","tl");
		iso639_2to1.put("tsn","tn");
		iso639_2to1.put("ton","to");
		iso639_2to1.put("tur","tr");
		iso639_2to1.put("tso","ts");
		iso639_2to1.put("tat","tt");
		iso639_2to1.put("twi","tw");
		iso639_2to1.put("uig","ug");
		iso639_2to1.put("ukr","uk");
		iso639_2to1.put("urd","ur");
		iso639_2to1.put("uzb","uz");
		iso639_2to1.put("vie","vi");
		iso639_2to1.put("vol","vo");
		iso639_2to1.put("wol","wo");
		iso639_2to1.put("xho","xh");
		iso639_2to1.put("yid","ji");
		iso639_2to1.put("yor","yo");
		iso639_2to1.put("zha","za");
		iso639_2to1.put("zho","zh");
		iso639_2to1.put("zul","zu");	
	}
	private static final long serialVersionUID = 5208601891396446456L;

	public LocaleSet(String threeCharCode) throws UnsupportedEncodingException {
        super();
        add(new Locale(LocaleSet.iso639_2_to_iso639_1(threeCharCode)));
    }

    public boolean add(String threeCharCode) throws UnsupportedEncodingException {
    	// TODO: also accept two character codes, full names of languages, etc
        return super.add(new Locale(LocaleSet.iso639_2_to_iso639_1(threeCharCode)));
    }
    
    static public String iso639_2_to_iso639_1(String threeCharCode) throws UnsupportedEncodingException {
    	String lowercase=threeCharCode.toLowerCase(Locale.ENGLISH);
    	String iso639_1=iso639_2to1.get(lowercase);
        
        if (iso639_1==null)
            throw new UnsupportedEncodingException("Can't use ISO 639-2 language \"" + threeCharCode +"\"");
        
        return iso639_1;
    }

}
