package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

/**
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
public class SequenceView extends MetaTree<MediaProperty> {
    MediaProperty pPosition,pTotal;
    public SequenceView(MediaProperty p,MetaTree<MediaProperty> parent,MediaProperty pPos,MediaProperty pTot) {
        super(p,parent);
        pPosition=pPos;
        pTotal=pTot;
    }

    @Override
    public Object getValue() {
        if(!this.getProperty().getDataType().isAssignableFrom(SequencePosition.class))
            return null;
        MetaTree<MediaProperty> refPosition = this.getParent().getSingleChild(pPosition);
        MetaTree<MediaProperty> refTotal = this.getParent().getSingleChild(pTotal);
        Integer position=null;
        Integer total=null;
        if(refPosition!=null)
            position=refPosition.getInteger();
        if(refTotal!=null)
            total=refTotal.getInteger();
        return new SequencePosition(position,total);
    }

    @Override
    public void setValue(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
