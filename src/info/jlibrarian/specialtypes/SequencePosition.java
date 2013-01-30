package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

/*
 * defines a position in a sequence, e.g. album track no. 4 out of 5
 * in that example, 4 is the position and 5 is the setLength
 * position and/or length may be unknown (null)
 */
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result
				+ ((setLength == null) ? 0 : setLength.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequencePosition other = (SequencePosition) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (setLength == null) {
			if (other.setLength != null)
				return false;
		} else if (!setLength.equals(other.setLength))
			return false;
		return true;
	}
    
}
