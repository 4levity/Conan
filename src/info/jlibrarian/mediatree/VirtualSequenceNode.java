package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.SequencePosition;

/**
 * see also VirtualConcatenateNode
 * 
 * Defines a virtual-value node which represents a SequencePosition (x of y)
 * The node determines its value by assembling from two separate position and
 * total fields that are stored in the tag natively.
 *
 * So for instance, in a Vorbis comment block, the tag natively stores
 * TRACKNUMBER and TRACKTOTAL as two distinct fields, but the tag loader class
 * also creates this "view" node so we can easily translate to a different tag
 * format or do sequence verification.
 *
 * @author ivan
 */
public class VirtualSequenceNode extends PropertyTree<MediaProperty> {
    MediaProperty pPosition,pTotal;
    public VirtualSequenceNode(MediaProperty p,PropertyTree<MediaProperty> parent,MediaProperty pPos,MediaProperty pTot) {
        super(p,parent);
        pPosition=pPos;
        pTotal=pTot;
    }

    @Override
    public Object getValue() {
        if(!this.getNodeProperty().getDataType().isAssignableFrom(SequencePosition.class))
            return null;
        
        Integer position;
        try {
			position= Integer.parseInt((String)this.getParent().queryBestResult(pPosition));
		} catch (NumberFormatException e) {
			position=null;
		}
        
        Integer total;
        try {
        	total = Integer.parseInt((String)this.getParent().queryBestResult(pTotal));
		} catch (NumberFormatException e) {
			total=null;
		}
        return new SequencePosition(position,total);
    }

    @Override
    public void setValue(Object o) {
    	// TODO support setting (by pushing properties to parent)
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
