package info.jlibrarian.propertytree;

/* 
 * the parameter to PropertyTree needs to implement this interface,
 * in order to describe the data type held at node with property X
 */
public interface Property {
    public Class<?> getDataType();
    
    // istypeof should return true if p is null or if p==this. it may return true if p is a superset of this.
    public boolean isTypeOf(Property p);
}
