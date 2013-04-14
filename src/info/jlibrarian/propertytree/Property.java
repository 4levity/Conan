package info.jlibrarian.propertytree;

/**
 * The parameter to PropertyTree needs to implement this interface,
 * in order to describe the data type held at node with property X.
 *
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public interface Property {
    public Class<?> getDataType();
    
    // istypeof should return true if p is null or if p==this. it may return true if p is a superset of this.
    public boolean isTypeOf(Property p);
    
    /**
     * Specifies whether a value is supposed to be distinct/concrete at the unit/file level.
     * 
     * this can control query behavior: if more than one distinct value is given for a "unique" attribute 
     * then it is considered a "conflict," not so if the attribute is not expected to be unique.
     * 
     * the query system will attempt to resolve conflicts by sorting values on confidence/match count
     * but if the attribute is NOT marked unique, results will be grouped by value and unsorted. 
     * this allows for instance guessing which Title is correct if a file has multiple Title properties.
     * 
     * when doing higher level queries (e.g. query "title" on a folder containing multiple works) 
     * this behavior is not particularly relevant and conflicts would usually be expected.
     * 
     * @return 	true if this attribute is supposed to be concrete/unique for an object (e.g. Title, Track number), or false if it may be expected to be repeated (file, "unknown field") 
     */
    public boolean getIsUniqueAttribute();
    
    public String getName();
    
    public String getDescription();
    
    public Property extended(String name,boolean create);
}
