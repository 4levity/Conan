package info.jlibrarian.propertytree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeList<PROPERTY extends Property> 
	implements 
	Comparable<NodeList<PROPERTY>>,
    Comparator<NodeList<PROPERTY>> {
    private List<PropertyTree<PROPERTY>> sources;
    PROPERTY property;
    public NodeList(PropertyTree<PROPERTY> source) {
        sources=new ArrayList<PropertyTree<PROPERTY>>(1);
        sources.add(source);
        property=source.getNodeProperty();
    }
    
    /**
     * only usable as comparator when not initialized with a first node
     */
    public NodeList() {
        sources=null;
        property=null;
    }
    public void addSource(PropertyTree<PROPERTY> s) {
        if(sources==null)
            throw new RuntimeException("MetadataNodeList internal error: trying to add to comparator");
        if(s.getNodeProperty() != property)
            throw new RuntimeException("MetadataNodeList internal error: add mismatched value");
        sources.add(s);
    }
    public List<PropertyTree<PROPERTY>> getSources() { 
        return sources;
    }
    public Integer getConfidence() { 
        if(sources==null)
            return -1;
        int total_confidence=0;
        for(PropertyTree<PROPERTY> node : sources) {
            total_confidence += node.getConfidence();
        }
        return total_confidence; 
    }
    public PropertyTree<PROPERTY> getFirstValue() {
        if(sources==null)
            return null;
        return sources.get(0);
    }
    public int compare(NodeList<PROPERTY> arg0, NodeList<PROPERTY> arg1) {
        return (arg1.getConfidence()-arg0.getConfidence());
    }
    public int compareTo(NodeList<PROPERTY> arg0) {
        if(arg0==null)
            throw new NullPointerException("cannot compare to null");
        return compare(this,arg0);
    }
}
