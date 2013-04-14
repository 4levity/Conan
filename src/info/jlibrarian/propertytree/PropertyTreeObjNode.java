package info.jlibrarian.propertytree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

/**
 * A basic concrete PropertyTree which encapsulates an Object as the node value.
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public class PropertyTreeObjNode extends PropertyTree {
    Object obj;
    public PropertyTreeObjNode(Property prop,PropertyTree parent) {
        super(prop,parent);
        this.obj=null;
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
     * @param o	new value
     */
    @Override
    //@SuppressWarnings("unchecked")
    public void setValue(Object o) {
        obj=convertObject(o);
    }
 }
