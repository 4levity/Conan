package info.jlibrarian.propertytree;

import info.jlibrarian.stringutils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This is a result set of a PropertyTree search for a SINGLE given property.
 * supplies a list of unique values for that property
 * each result has one value (Object, runtime class type implied by property) 
 * 		plus each result has a list of one or more nodes having that value (supporting evidence for property=value)
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public class PropertySearchResults {
	private ArrayList<Result> results;
	private int totalNodes;
	private boolean sorted;
		
	/**
	 * 'Result' is a single search result (i.e. 1 property with a list of 1 or more nodes representing it).
	 * 
	 * @author C. Ivan Cooper (ivan@4levity.net)
	 *
	 */
	public class Result implements Comparable<Result> {
		private ArrayList<PropertyTree> nodes;		
		public Result(PropertyTree firstMatchingNode) {
			super();
			nodes=new ArrayList<PropertyTree>(1);
			nodes.add(firstMatchingNode);
		}
		public void add(PropertyTree nextMatchingNode) {
			// TODO: remove check to improve performance
			if(nodes.get(0).getValue().equals(nextMatchingNode.getValue())) {
				nodes.add(nextMatchingNode);
			} else {
				throw new RuntimeException("Attempted to store non-consistent result in PropertySearchResults.Result");
			}
		}
		@Override		
		public int compareTo(Result arg0) {
			if(arg0==null)
				return 0;
			//else
			return arg0.size()-this.size() ; // descending by # of results i.e. most results first
		}
		public int size() {
			return nodes.size();
		}
		public Object getValue() {
			PropertyTree i=nodes.get(0);
			return i.getValue();
		}
		public Property getProperty() {
			return nodes.get(0).getNodeProperty();
		}
		public PropertyTree getNode(int result) {
			if(nodes.size()>result) {
				return nodes.get(result);
			}
			return null;
		}
	}
	protected PropertySearchResults(PropertyTree firstNode) {
		results=new ArrayList<Result>(1); // size 1; guessing there will not be any conflicting values
		results.add(new Result(firstNode));
		sorted=true;
		totalNodes=1;
	}

	/**
	 * Get the first value in the search result.
	 * 
	 * For unique attributes, this will return the one with the highest rank/probability (e.g. most trusted)
	 * 
	 * @return	first value in search result
	 */
	public Object getFirstValue() {
		return getValue(0);
	}
	
	public Object getValue(int ix) {
		Result r=getResult(ix);
		if(r!=null) {
			return r.getValue();
		}
		return null;
	}
	public Result getResult(int ix) {
		if(this.results.size()>ix) {
			this.sort();
			return this.results.get(ix);
		}
		return null;
	}
	
	
	public int getNumResults() {
		return results.size();
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public Object count() {
		return results.size();
	}
	public boolean isUnanimous() {
		return results.size()==1;
	}
	public Property getProperty() {
		// these should all be the same so no need to sort
		return results.get(0).getProperty();
	}
	protected void addResult(PropertyTree node) {
		// todo: don't call getValue again right here!
		Object value=node.getValue();
		Result match=getMatchingResult(value);
		if(match==null) {
			results.add(new Result(node));
		} else {
			match.add(node);
		}
		this.totalNodes++;
		this.sorted=false;
	}
	private Result getMatchingResult(Object match) {
		for(Result sr : results) {
			if(sr.getValue().equals(match)) {
				return sr;
			}
		}
		return null;
	}
	private void sort() {
		// skip sorting nonunique attributes
		if(this.sorted || !this.getProperty().getIsUniqueAttribute())
			return;
		
		Collections.sort(this.results);
		this.sorted=true;
	}
	
	@Override
	public String toString() {
		Property p=this.getProperty();
        return "Query ("+this.totalNodes+" elements) "+p.toString();
	}

	/**
	 * Returns formatted text results for display in console/etc.
	 * 
	 * @param showNodes		if true, show source nodes instead of just name/value
	 * @param maxResults	max results to show, or -1 for no limit
	 * @return				formatted search results
	 */
	public String formattedResults(boolean showNodes,int maxResults) {
		sort();
		Property p=this.getProperty();
        String result="Query ("+this.totalNodes+" elements) "+p.toString();
    	if(this.isUnanimous()) {
    		result+=": ";
    	} else if(p.getIsUniqueAttribute()) {
            result+=": conflict { ";
    	} else {
            result+=": SET { ";
    	}
    	
    	int numResults=0;
        if(this.results != null) {
            for(Result r : this.results) {
            	if(maxResults==-1 || numResults<maxResults) {
            		numResults++;
            		
                	if(showNodes) {
//                		if(!r.getProperty().getIsUniqueAttribute()) {
                			// print the full list of nodes
                			for(int i=0;i<r.size();i++) {
                				PropertyTree node=r.getNode(i);
                				Object val=node.getValue();
                    			result+=r.getNode(i).describeNode()
                    					+(val==null?"":"="+val.toString())
                    					+"  ";
                			}
                		} else {
                			// prints out each unique value (starting with most results), do not print nodes
                			result += StringUtils.stripControlCharacters(r.getValue().toString())
                			 		+" ["+r.size();
                			if(r.size()==1) {
                    			result+=" result] ";
                  			} else {
                    			result+=" results] ";
                  			}
                		}
            	}
            }
        }
    	if(!this.isUnanimous()) {
            result+="}";
    	}
    	
    	return result;
	}
}
