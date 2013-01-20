package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

public class SequencePosition {
    private Integer position;
    private Integer setLength;
    public SequencePosition(Integer pos,Integer tot) {
        position=pos;
        setLength=tot;
    }
    @Override
    public String toString() {
        return 
               "position "+
               (position==null?"?":String.format("%02d",position))
               +"/"
               +(setLength==null?"?":String.format("%02d",setLength));
    }
}
