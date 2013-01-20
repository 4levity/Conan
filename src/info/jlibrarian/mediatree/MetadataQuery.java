package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MetadataQuery<PROPERTY> {
    private final Set<PROPERTY> properties;
    private HashMap<PROPERTY,ArrayList<MetadataNodeList<PROPERTY>>> allValues=null;
    private List<MetaTree<PROPERTY>> invalidNodes=null;
    private List<MetaTree<PROPERTY>> createCapableNodes=null;
    
    public MetadataQuery(MetaTree<PROPERTY> queryFrom,
            PROPERTY targetProperty) {
        properties = new HashSet<PROPERTY>(1);
        properties.add(targetProperty);
        allValues = new HashMap<PROPERTY, ArrayList<MetadataNodeList<PROPERTY>>>(1);
            processNode(queryFrom);
        sortAllValues();
    }
    public MetadataQuery(MetaTree<PROPERTY> queryFrom,
            Set<PROPERTY> targetProperties) {
        properties=targetProperties;
        allValues=new HashMap<PROPERTY,ArrayList<MetadataNodeList<PROPERTY>>>();
        processNode(queryFrom);
        sortAllValues();
    }
    public MetadataQuery(MetaTree<PROPERTY> queryFrom,
            PROPERTY targetProperty,Object matchValue) {
        throw new UnsupportedOperationException("value parameter query not supported yet.");
    }

    public void print() {
        System.out.println("query result:");
        for(PROPERTY p :properties) {
            System.out.print(p.toString());
            System.out.print(" = {");
            if(allValues.get(p)!=null) {
                for(MetadataNodeList<PROPERTY> node : allValues.get(p)) {
                    Object val = node.getFirstValue().getValue();
                    System.out.print((val==null?"null":val.toString())
                            +" ["+node.getConfidence().toString()+"] ");
                }
            }
            System.out.println("}");
        }
        if(invalidNodes!=null) {
            System.out.println("invalid nodes: ");
            for(MetaTree<PROPERTY> n : this.invalidNodes) {
                System.out.println(" * "+n.describeNode());
            }
        }
    }
    public MetaTree<PROPERTY> getFirstValue(PROPERTY p) {
        ArrayList<MetadataNodeList<PROPERTY>> v=allValues.get(p);
        if(v==null)
            return null;
        return v.get(0).getFirstValue();
    }
    public MetaTree<PROPERTY> getFirstValue() {
        if(allValues.keySet().isEmpty())
            return null;
        //else
        return getFirstValue(allValues.keySet().iterator().next());
    }
    private boolean isRepresented(PROPERTY p) {
        return(allValues.get(p)!=null);
    }
    private void processNode(MetaTree<PROPERTY> subtree) {
        if(properties.contains(subtree.getProperty())) {
            processMatchingNode(subtree);
        }
/*      
		for(PROPERTY p : properties) {
            // list all nodes that do not have any info on this property, but could
            if(subtree.canCreateChild(p) && !subtree.represents(p)) {
                if(createCapableNodes==null)
                    createCapableNodes=new LinkedList<MetaTree<PROPERTY>>();
                createCapableNodes.add(subtree);
            }
        }
 */
        // process all children of this node recursively
        for(Iterator<MetaTree<PROPERTY>> i=subtree.getChildren();i !=null && i.hasNext();) {
            processNode(i.next());
        }
//            throw new QueryException("could not process query at node "
//                    +subtree.getProperty().toString() ,ex);
    }
    private void processMatchingNode(MetaTree<PROPERTY> subtree) {
        ArrayList<MetadataNodeList<PROPERTY>> valueList=allValues.get(subtree.getProperty());
        if(valueList==null)
        {   // have we not yet processed any value for this property?
            // create a new list of value-sets for this property.
            valueList=new ArrayList<MetadataNodeList<PROPERTY>>();
            valueList.add(new MetadataNodeList<PROPERTY>(subtree));
            allValues.put(subtree.getProperty(),valueList);
        } else {
            // we already have a value for this property.  is it the same?
            MetadataNodeList<PROPERTY> match = findValue(valueList,subtree.getValue());
            if(match==null) {
                valueList.add(new MetadataNodeList<PROPERTY>(subtree));
            } else {
                match.addSource(subtree);
            }
        }

        if(!subtree.isValueValid()) {
            if(invalidNodes==null)
                invalidNodes=new LinkedList<MetaTree<PROPERTY>>();
            invalidNodes.add(subtree);
        }
    }
    private MetadataNodeList<PROPERTY> findValue(ArrayList<MetadataNodeList<PROPERTY>> vlist,Object v) {
        for(MetadataNodeList<PROPERTY> node : vlist) {
            if(node.getSources()!=null) {
                if(node.getSources().get(0).getValue()==null)
                    return (v==null?node:null);
                if(node.getSources().get(0).getValue().equals(v))
                    return node;
            }                
        }
        return null;
    }
    public Set<PROPERTY> getProperties() {
        return properties;
    }
    public HashMap<PROPERTY,ArrayList<MetadataNodeList<PROPERTY>>> getValues() {
        return allValues;
    }
    public boolean isConsistent(PROPERTY p) {
        if(!isRepresented(p))
            return true;
        return(allValues.get(p).size()==1);
    }
    public Iterator<MetaTree<PROPERTY>> getInvalidNodes() {
        if(invalidNodes==null)
            return null;
        return invalidNodes.iterator();
    }
    /**
     * get the list of nodes that do NOT represent the property, but know how
     * to represent it by creating a child node.
     * 
     * @return Iterator over node list
     */
    public Iterator<MetaTree<PROPERTY>> getCreateCapableNodes() {
        if(createCapableNodes==null)
            return null;
        return createCapableNodes.iterator();
    }

    private void sortAllValues() {
        // sort the value results so that the most confident is first
        for(ArrayList<MetadataNodeList<PROPERTY>> nodes : allValues.values()) {
            Collections.sort(nodes);
        }
    }
}
