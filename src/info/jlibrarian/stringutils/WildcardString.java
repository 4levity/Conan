package info.jlibrarian.stringutils; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.Comparator;

public class WildcardString implements Comparable<WildcardString>,Comparator<String> {
    private final String string;
    /**
     * Compares two strings with a simple wildcard rule (see WildcardString)
     * 
     * @param a
     * @param b
     * @return comparison result or 0 if equal
     */
    static public int compareWildcardString(String a,String b) {
        if(a==null && b==null) 
            return 0;
        //else
        if(a==null)
            return -1;
        //else
        if(b==null)
            return 1;
        
        int wca=a.indexOf("*");
        int wcb=b.indexOf("*");
        if(wcb>=0) {
            b = b.substring(0,wcb); // remove * and trailing chars
            if(a.length()>wcb)
                a = a.substring(0,wcb);
        }
        if(wca>=0) {
            a = a.substring(0,wca); // remove * and trailing chars
            if(b.length()>wca)
                b = b.substring(0,wca);
        }
        return a.compareToIgnoreCase(b);
        //return a.compareTo(b);
    }
    static public String generalizeWildcard(String s) {
        if (s==null)
            return "*";
        int ix=s.length()-1;
        if(ix<=1)
            return "*";
        while(ix>0 && s.charAt(ix)=='*')
            ix--; // skip any * fields starting from the end
        return s.substring(0, ix)+"*";
    }
    public WildcardString(String s) {
        if(s==null)
            string="*";
        else
            string=s;
    }
    public WildcardString() {
        string="*";
    }
    /**
     * note: violates transitivity of equality
     * @param arg0
     * @param arg1
     * @return
     */
    public int compare(String arg0, String arg1) {
        return compareWildcardString(arg0,arg1);
    }
    /**
     * note: violates transitivity of equality
     * @param arg0
     * @return
     */
    public int compareTo(WildcardString arg0) {
        if(arg0==null)
            throw new NullPointerException("cannot compare WildcardString to null");
        return compareWildcardString(string,arg0.toString());
    }
    @Override
    public String toString() {
        return string;
    }
    /**
     * note: violates transitivity of equality
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WildcardString other = (WildcardString) obj;
        return (0==compareTo(other));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
