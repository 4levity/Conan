package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MetadataNodeList<PROPERTY> 
	implements 
	Comparable<MetadataNodeList<PROPERTY>>,
    Comparator<MetadataNodeList<PROPERTY>> {
    private List<MetaTree<PROPERTY>> sources;
    PROPERTY property;
    public MetadataNodeList(MetaTree<PROPERTY> source) {
        sources=new ArrayList<MetaTree<PROPERTY>>(1);
        sources.add(source);
        property=source.getProperty();
    }
    /**
     * only usable as comparator when not initialized with a first node
     */
    public MetadataNodeList() {
        sources=null;
        property=null;
    }
    public void addSource(MetaTree<PROPERTY> s) {
        if(sources==null)
            throw new RuntimeException("MetadataNodeList internal error: trying to add to comparator");
        if(s.getProperty() != property)
            throw new RuntimeException("MetadataNodeList internal error: add mismatched value");
        sources.add(s);
    }
    public List<MetaTree<PROPERTY>> getSources() { 
        return sources;
    }
    public Integer getConfidence() { 
        if(sources==null)
            return -1;
        int total_confidence=0;
        for(MetaTree<PROPERTY> node : sources) {
            total_confidence += node.getConfidence();
        }
        return total_confidence; 
    }
    public MetaTree<PROPERTY> getFirstValue() {
        if(sources==null)
            return null;
        return sources.get(0);
    }
    public int compare(MetadataNodeList<PROPERTY> arg0, MetadataNodeList<PROPERTY> arg1) {
        return (arg1.getConfidence()-arg0.getConfidence());
    }
    public int compareTo(MetadataNodeList<PROPERTY> arg0) {
        if(arg0==null)
            throw new NullPointerException("cannot compare to null");
        return compare(this,arg0);
    }
}
