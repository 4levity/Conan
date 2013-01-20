package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * represent a list of languages (as for id3v2 TLAN tag)
 * 
 * also contains static function for handling language code conversion
 * 
 * @author ivan
 */
public class Iso839_2LocaleSet extends HashSet<Locale> implements Set<Locale> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5208601891396446456L;

	public Iso839_2LocaleSet(String threeCharCode) throws UnsupportedEncodingException {
        super();
        add(new Locale(Iso839_2LocaleSet.Iso839_2_to_Iso839_1(threeCharCode)));
    }

    public boolean add(String threeCharCode) throws UnsupportedEncodingException {
        return super.add(new Locale(Iso839_2LocaleSet.Iso839_2_to_Iso839_1(threeCharCode)));
    }
    
    static public String Iso839_2_to_Iso839_1(String threeCharCode) throws UnsupportedEncodingException {
        /** 
         * thanks to the GNU Classpath 0.95 for providing the ISO 839-1
         * language code list under GPL v2+ so I didn't have to type it in
         */
        
        int index= 
           ("aarabkaframharaasmaymazebakbelbulbihbisbenbodbrecatcoscescymdandeu"
            + "dzoellengepospaesteusfasfinfijfaofrafrygaigdhglggrngujhauhebhinhrv"
            + "hunhyeinaindileipkindislitaikuhebjpnyidjawkatkazkalkhmkankorkaskur"
            + "kirlatlinlaolitlavmlgmrimkdmalmonmolmarmsamltmyanaunepnldnorociorm"
            + "oripanpolpusporquerohrunronruskinsansndsagsrpsinslkslvsmosnasomsqi"
            + "srpsswsotsunsweswatamteltgkthatirtuktgltsntonturtsotattwiuigukrurd"
            + "uzbvievolwolxhoyidyorzhazhozul").indexOf(threeCharCode);
        
        if (index % 3 != 0 || threeCharCode.length() != 3)
            throw new UnsupportedEncodingException("Can't find ISO3 language for " + threeCharCode);
        
        return 
           ("aa,ab,af,am,ar,as,ay,az,ba,be,bg,bh,bi,bn,bo,br,ca,co,cs,cy,da,de,"
            + "dz,el,en,eo,es,et,eu,fa,fi,fj,fo,fr,fy,ga,gd,gl,gn,gu,ha,iw,hi,hr,"
            + "hu,hy,ia,in,ie,ik,in,is,it,iu,iw,ja,ji,jw,ka,kk,kl,km,kn,ko,ks,ku,"
            + "ky,la,ln,lo,lt,lv,mg,mi,mk,ml,mn,mo,mr,ms,mt,my,na,ne,nl,no,oc,om,"
            + "or,pa,pl,ps,pt,qu,rm,rn,ro,ru,rw,sa,sd,sg,sh,si,sk,sl,sm,sn,so,sq,"
            + "sr,ss,st,su,sv,sw,ta,te,tg,th,ti,tk,tl,tn,to,tr,ts,tt,tw,ug,uk,ur,"
            + "uz,vi,vo,wo,xh,ji,yo,za,zh,zu.")
            .substring(index,index+2);
    }

}
