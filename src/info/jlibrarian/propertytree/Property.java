package info.jlibrarian.propertytree;

/* 
 * the parameter to PropertyTree needs to implement this interface,
 * in order to describe the data type held at node with property X
 */
public interface Property {
    public Class<?> getDataType();
    
    // istypeof should return true if p is null or if p==this. it may return true if p is a superset of this.
    public boolean isTypeOf(Property p);
    
    /* if this attribute is supposed to be unique (e.g. Title, Track number) then return true
     * otherwise if the attribute is expected to be repeated (file, unknown field) return false 
     * 
     * this controls query behavior: if more than one distinct value is given for a "unique" attribute 
     * then it is considered a conflict, not so if the attribute is not expected to be unique.
     * 
     * the query system will attempt to resolve conflicts by sorting values on confidence/match count
     * if the attribute is NOT marked unique, results will be grouped by value but unsorted.
     * 
     * when doing higher level queries (e.g. query "title" on a folder containing multiple works) 
     * conflicts should be expected.
     */
    public boolean getIsUniqueAttribute();
    
    public String getName();
    
    public String getDescription();
    
    public Property extended(String name,boolean create);
}
