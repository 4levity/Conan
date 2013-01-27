package info.jlibrarian.propertytree;

import java.util.ArrayList;
import java.util.Collections;

/* 
 * result set of a PropertyTree search for a SINGLE given property
 * supplies a list of unique values for that property
 * each result has one value (Object, runtime class type implied by property) 
 * 		plus each result has a list of one or more nodes having that value (supporting evidence for property=value)
 */
public class PropertySearchResults<PROPERTY extends Property> {

	// a single result (i.e. a single *consistent* value with a list of nodes all matching the same value)
	public class Result implements Comparable<Result> {
		private ArrayList<PropertyTree<PROPERTY>> nodes;		
		public Result(PropertyTree<PROPERTY> firstMatchingNode) {
			super();
			nodes=new ArrayList<PropertyTree<PROPERTY>>(1);
			nodes.add(firstMatchingNode);
		}
		public void add(PropertyTree<PROPERTY> nextMatchingNode) {
			if(nodes.get(0).getValue().equals(nextMatchingNode.getValue())) {
				nodes.add(nextMatchingNode);
			} else {
				throw new RuntimeException("Attempted to store non-consistent result in PropertySearchResults.Result");
			}
		}
		@Override		
		public int compareTo(Result arg0) {
			// TODO sort by confidence
			return 0;
		}
		public int confidence() {
			return nodes.size();
		}
		public Object getValue() {
			return nodes.get(0).getValue();
		}
		public PROPERTY getProperty() {
			return nodes.get(0).getNodeProperty();
		}
		public Object getNode(int result) {
			if(nodes.size()>result) {
				return nodes.get(result);
			}
			return null;
		}
	}
	private ArrayList<Result> results;
	int totalNodes;
	boolean sorted;
	
	PropertySearchResults(PropertyTree<PROPERTY> firstNode) {
		results=new ArrayList<Result>(1);
		results.add(new Result(firstNode));
		sorted=true;
		totalNodes=1;
	}
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
	public Object count() {
		return results.size();
	}
	public boolean isUnanimous() {
		return results.size()==1;
	}
	public PROPERTY getProperty() {
		return results.get(0).getProperty();
	}
	public void addResult(PropertyTree<PROPERTY> node) {
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
		sort();
		for(Result sr : results) {
			if(sr.getValue().equals(match)) {
				return sr;
			}
		}
		return null;
	}
	private void sort() {
		if(this.sorted)
			return;
		
		Collections.sort(this.results);
		this.sorted=true;
	}
	public void print() {
		PROPERTY p=this.getProperty();
        System.out.print(p.toString());
        System.out.print(" = {");
        if(this.results != null) {
            for(Result r : this.results) {
            	System.out.print(r.getValue().toString()
            			+" ["+r.confidence()+"] ");
            }
        }
        System.out.println("}");
	}
}
