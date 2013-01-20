package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

public class ObjectNode extends MetaTree<MediaProperty> {
    Object obj;
    public ObjectNode(MediaProperty prop,MetaTree<MediaProperty> parent) {
        super(prop,parent);
        obj=null;
    }

    @Override
    public Object getValue() {
        return obj;
    }
    /**
     * set the value at this node.  value must be of a runtime type that can
     * be assigned to this node.  (if this node represents a Long or Integer,
     * String values will be automatically converted as decimal values, and 
     * the value will be set to -1 if the string does not parse)
     * 
     * @param o
     */
    @Override
    //@SuppressWarnings("unchecked")
    public void setValue(Object o) {
        obj=convertObject(this.getProperty().getDataType(),o);
    }
 }
