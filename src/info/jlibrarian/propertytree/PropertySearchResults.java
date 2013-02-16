package info.jlibrarian.propertytree;

import info.jlibrarian.stringutils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

/* 
 * result set of a PropertyTree search for a SINGLE given property
 * supplies a list of unique values for that property
 * each result has one value (Object, runtime class type implied by property) 
 * 		plus each result has a list of one or more nodes having that value (supporting evidence for property=value)
 */
public class PropertySearchResults {

	// a single result (i.e. a single *consistent* value with a list of nodes all matching the same value)
	public class Result implements Comparable<Result> {
		private ArrayList<PropertyTree> nodes;		
		public Result(PropertyTree firstMatchingNode) {
			super();
			nodes=new ArrayList<PropertyTree>(1);
			nodes.add(firstMatchingNode);
		}
		public void add(PropertyTree nextMatchingNode) {
			// NOTE: remove check to improve performance
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
	private ArrayList<Result> results;
	int totalNodes;
	boolean sorted;
	
	PropertySearchResults(PropertyTree firstNode) {
		results=new ArrayList<Result>(1); // size 1; guessing there will not be any conflicting values
		results.add(new Result(firstNode));
		sorted=true;
		totalNodes=1;
	}

	// for unique attributes, this will return the one with the most matches in the result set (ties undefined)
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
	public void addResult(PropertyTree node) {
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
        if(this.results != null) {
            for(Result r : this.results) {
        		if(r.getProperty().getIsUniqueAttribute()) {
        			// prints out each unique value (most likely first), do not print nodes
        			result += StringUtils.stripControlCharacters(r.getValue().toString())
        			 		+" ["+r.size();
        			if(r.size()==1) {
            			result+=" result] ";
          			} else {
            			result+=" results] ";
          			}
        		} else {
        			// for nonunique attributes, print the full list of nodes
        			for(int i=0;i<r.size();i++) {
            			result+=r.getNode(i).describeNode()+"  ";
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
