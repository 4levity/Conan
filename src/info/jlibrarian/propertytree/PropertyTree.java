package info.jlibrarian.propertytree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.stringutils.SettableFromString;
import info.jlibrarian.stringutils.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PropertyTree<PROPERTY extends Property> {
    private final PROPERTY property;
    private PropertyTree<PROPERTY> parent;
    private List<PropertyTree<PROPERTY>> children;
    
    public PropertyTree(PROPERTY property,PropertyTree<PROPERTY> parent) {
        this.property=property;
        this.parent=parent;
        this.children=null;
        if(parent!=null)
            parent.addChild(this);
    }
    final public PROPERTY getNodeProperty() {
        return property;
    }
    public abstract void setValue(Object o);
    public abstract Object getValue();
/* lookup: get set of unique values under here that have propertyX
 * 		(per value: nodelist/confidence)
 * 		optionally only look up values that are direct children of given property/superproperty
 * 		if none match, return null set.
 */
    final public PropertySearchResults<PROPERTY> query(PROPERTY target,PROPERTY searchWithin) {
    	ArrayList<PROPERTY> targetList=null;
    	if(target!=null) {
    		targetList=new ArrayList<PROPERTY>(1);
    		targetList.add(target);
    	}
    	Map<PROPERTY, PropertySearchResults<PROPERTY>> results=this.doQuery(null, targetList, searchWithin);
    	if(results==null) {
    		return null;
    	}
    	return results.get(target);
    }
    final public PropertySearchResults<PROPERTY> query(PROPERTY target) {
    	return this.query(target, null);
    }
    final private Map<PROPERTY,PropertySearchResults<PROPERTY>> doQuery(Map<PROPERTY,PropertySearchResults<PROPERTY>> results,ArrayList<PROPERTY> targetList,PROPERTY searchWithin) {    	
    	PropertyTree<PROPERTY> parent=this.getParent();
    	boolean searchHere=false;
    	if(searchWithin==null) {
    		searchHere=true;
    	} else if(parent!=null) {
    		// TODO: go through code and figure out where else to change property== to property.isTypeOf!
    		if(parent.getNodeProperty().isTypeOf(searchWithin)) {
    			searchHere=true;
    		}
    	}
    	if(searchHere) {
    		if(targetList==null) {
    			// null target = add everything that exists
    			if(this.getValue()!=null) {
        			results=addThisResult(results,targetList);
    			}
    		} else {
        		for(PROPERTY target : targetList) {
            		if(this.getNodeProperty().isTypeOf(target)) {
                		if(this.getValue()!=null) {
                			results=addThisResult(results,targetList);
                		}
            		}
        		}
    		}
    	}
    	if(this.children!=null) {
        	for(PropertyTree<PROPERTY> child : this.children) {
        		results=child.doQuery(results, targetList, searchWithin);
        	}
    	}
    	return results;
    }

	// we have found a match at this node and need to add it to the result set
    final private Map<PROPERTY, PropertySearchResults<PROPERTY>> addThisResult(
			Map<PROPERTY, PropertySearchResults<PROPERTY>> results,ArrayList<PROPERTY> targetProperties) {
		if(results==null) {
	    	// first match, need to allocate a result set 
	    	// guess what size map to create
	    	int expectedResults;
	    	if(targetProperties!=null) {
	    		expectedResults=targetProperties.size();
	    	} else {
	    		expectedResults=10;
	    	}
	    	results=new HashMap<PROPERTY,PropertySearchResults<PROPERTY>>(expectedResults);

	    	// put this node in results
			results.put(this.getNodeProperty(),new PropertySearchResults<PROPERTY>(this));
		} else {
			PropertySearchResults<PROPERTY> resultForThisProp=results.get(this.getNodeProperty());
			if(resultForThisProp==null) {
				results.put(this.getNodeProperty(), new PropertySearchResults<PROPERTY>(this));
			} else {
				resultForThisProp.addResult(this);
			}
		}
		return results;
	}
	/* quick: return [Object or PropertyTree] most confident value under here matching propertyX
     * optionally only look up values that are direct children of given property/superproperty
     * if none match, return null.
     */
    final public Object queryBestResult(PROPERTY target,PROPERTY searchWithin) {
    	PropertySearchResults<PROPERTY> queryResult=this.query(target, searchWithin);
    	if(queryResult!=null) {
    		return queryResult.getFirstValue();
    	}
    	return null;
    }
    final public Object queryBestResult(PROPERTY target) {
    	return this.queryBestResult(target, null);
    }
    final public PropertyTree<PROPERTY> queryBestResultNode(PROPERTY target,PROPERTY searchWithin) {
    	PropertySearchResults<PROPERTY> queryResult=this.query(target, searchWithin);
    	if(queryResult!=null) {
    		return queryResult.getResult(0).getNode(0);
    	}
    	return null;
    }
    final public PropertyTree<PROPERTY> queryBestResultNode(PROPERTY target) {
    	return this.queryBestResultNode(target, null);
    }
/* lookup list: return hashmap<property,#1 lookup set>, given set of properties
 * 		optionally only look up values that are direct children of given property/superproperty
 * 		(same result as repeatedly calling lookup but ideally in one traversal)
 * 		if none match, return null hashset.
 */
    final public Map<PROPERTY,PropertySearchResults<PROPERTY>> query(ArrayList<PROPERTY> targetList,PROPERTY searchWithin) {
    	return doQuery(null,targetList,searchWithin);
    }
    final public Map<PROPERTY,PropertySearchResults<PROPERTY>> query(ArrayList<PROPERTY> targetList) {
    	return this.doQuery(null,targetList,null);
    }
/* lookup all: return hashmap<property,#1 lookup set>, for any property represented
 * optionally only look up values that are direct children of given property/superproperty
 * if this node has null value and no descendants with non-null values, 
 * return null set
 */
    final public Map<PROPERTY,PropertySearchResults<PROPERTY>> query() {
    	return this.doQuery(null, null, null);
    }
    
    final protected PropertyTree<PROPERTY> getParent() {
        return parent;
    }
    private boolean addChild(PropertyTree<PROPERTY> newChild) {
        if(children==null)
            children=new LinkedList<PropertyTree<PROPERTY>>();
        children.add(newChild);
        return true;
    }
    final public Iterator<PropertyTree<PROPERTY>> getChildren() {
        if(children==null)
            return null;
        return children.iterator();
    }
    public boolean isValueValid() {
    	// override to define how to determine if the assigned value is valid/allowed
    	Object o=getValue();
        if(o==null)
            return true;
        if(this.getNodeProperty().getDataType().isAssignableFrom(String.class)) {
            // default behavior for strings: no nulls or control characters allowed
            if(StringUtils.stripControlCharacters(o.toString()).equals(o.toString()))
                return true;
            return false;
        } // else default is to assume valid
        return true;
    }

    public void forceValueValid() {
    	// override to define a way to force an invalid/disallowed value to be valid/allowed
    	Object o=getValue();
        if(o==null)
            return;
        if(this.getNodeProperty().getDataType().isAssignableFrom(String.class)) {
            // default behavior for strings: no nulls or control characters allowed
        	// to make valid, replace invalid characters with "?"
            setValue(StringUtils.stripControlCharacters(toString()));
        } // else default is to assume valid
        return;
    }

    final public void forceSubtreeValid() {
        if(children==null)
            return;

        for(PropertyTree<PROPERTY> child : children) {
            child.forceSubtreeValid();
        }
    }
    
    public double getConfidence() {
    	// should be 1.0 if this node supposedly represents a "fact", or 0 > n > 1 if it is known to be a guess 
        return 1.0; 
    }

    public void setConfidence(int new_confidence) { 
        throw new UnsupportedOperationException("setConfidence not implemented");
    }

 
    public Integer getInteger() {
        Object gotValue=getValue();
        if (gotValue != null) {
            if (!Integer.class.isAssignableFrom(gotValue.getClass())) {
                throw new ClassCastException("don't know how to convert "
                		+gotValue.getClass().getName()+" to Integer");
            }
        }
        return (Integer)gotValue;
    }

    public Long getLong() {
        Object gotValue=getValue();
        if (gotValue != null) {
            if (!Long.class.isAssignableFrom(gotValue.getClass())) {            	
                throw new ClassCastException("don't know how to convert "
                		+gotValue.getClass().getName()+" to Long");
            }
        }
        return (Long)gotValue;
    }
    
    public Double getDouble() {
        Object gotValue=getValue();
        if (gotValue != null) {
            if (!Double.class.isAssignableFrom(gotValue.getClass())) {            	
                throw new ClassCastException("don't know how to convert "
                		+gotValue.getClass().getName()+" to Double");
            }
        }
        return (Double)gotValue;
    }
    
    public Float getFloat() {
        Object gotValue=getValue();
        if (gotValue != null) {
            if (!Float.class.isAssignableFrom(gotValue.getClass())) {            	
                throw new ClassCastException("don't know how to convert "
                		+gotValue.getClass().getName()+" to Float");
            }
        }
        return (Float)gotValue;
    }
    
    public File getFile() {
        Object gotValue=getValue();
        if(gotValue==null)
            return null;
        if(!gotValue.getClass().isAssignableFrom(File.class))  {
            throw new ClassCastException("don't know how to convert "
            		+gotValue.getClass().getName()+" to File");
        }
        return (File)gotValue;
    }

    public Object convertObject(Object o) {
    	if(o==null) {
    		return null;
    	}
    	
    	Class<?> targetType = this.property.getDataType();
        if(targetType.equals(Long.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Long storage
        	String string=StringUtils.trimNonNumeric(o.toString());
            try {
                o=new Long(Long.parseLong(string));
            } catch (NumberFormatException ex) {
                log(Level.WARNING,"String to Long conversion failed on "+StringUtils.stripControlCharacters(o.toString()));
                return null;
            }
        } else if(targetType.equals(Integer.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Integer storage
        	String string=StringUtils.trimNonNumeric(o.toString());
            try {
                o=new Integer(Integer.parseInt(string));
            } catch (NumberFormatException ex) {
            	log(Level.WARNING,"String to Integer conversion failed on "+StringUtils.stripControlCharacters(o.toString()));
                return null;
            }
        } else if(targetType.equals(Float.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Float storage
        	String string=StringUtils.trimNonNumeric(o.toString());
            try {
                o=new Float(Float.parseFloat(string));
            } catch (NumberFormatException ex) {
            	log(Level.WARNING,"String to Float conversion failed on "+StringUtils.stripControlCharacters(o.toString()));
                return null;
            }
        } else if(targetType.equals(Double.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Double storage
        	String string=StringUtils.trimNonNumeric(o.toString());
            try {
                o=new Double(Double.parseDouble(string));
            } catch (NumberFormatException ex) {
            	log(Level.WARNING,"String to Double conversion failed on "+StringUtils.stripControlCharacters(o.toString()));
                return null;
            }
        } else if(SettableFromString.class.isAssignableFrom(targetType)
        		&& String.class.isAssignableFrom(o.getClass()) ) {
        	SettableFromString sfs=null;
            try {
                @SuppressWarnings("unchecked")
				Constructor<? extends SettableFromString> cons = (Constructor<? extends SettableFromString>) targetType.getConstructor();
                if (cons != null) {
                    sfs = cons.newInstance();
                }
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (SecurityException ex) {
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (InstantiationException ex) {
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (IllegalAccessException ex) {
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (IllegalArgumentException ex) {
                 ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (InvocationTargetException ex) {
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            }
            if(sfs.setFromString((String)o)) {
            	return sfs;
            } else {
            	log(Level.WARNING,"String to "+targetType.getSimpleName()+" conversion failed on \""+StringUtils.stripControlCharacters(o.toString())+"\"");
                return null;
            }
        } else if(!targetType.isAssignableFrom(o.getClass())) {
            String msg="runtime type mismatch: can't assign "+o.getClass().getName()+" to "+targetType.getName();
            log(Level.SEVERE,msg);
            throw new ClassCastException(msg);
        }
        return o;
    }
    
	/*final public boolean setValue(MediaProperty property, Object newValue) {
		// TODO: implement child creation by pushing properties to nodes
		return false;
	}*/

/*    public boolean canCreateChild(PROPERTY newProperty) {
		return false;
	}
	public boolean createChild(PROPERTY newProperty,Object newValue) {
		
	}*/

    // functions to create text representation of the tree 
    // TODO: XML!
    public String describeTree() {
        return describeTree(0);
    }
    public String describeTree(int indent) {
        String s=new String();
        for(int i=indent;i>0;i--)
            s += "   ";
        s += this.describeNode() + System.getProperty("line.separator");
        for(Iterator<PropertyTree<PROPERTY>> j=getChildren();j !=null && j.hasNext();) {
            s += j.next().describeTree(indent+1);
        }
        return s;
    }
    public String describeNode() {
        return  (getNodeProperty()==null?"null":StringUtils.stripControlCharacters(getNodeProperty().toString()))
                +"="+ toString() + (isValueValid()?"":" (invalid)"); 
    }
	public String describePath() {
		if(parent==null)
			return describeNode();
		return parent.describePath()+"\\"+describeNode();
	}

    @Override
    public String toString() {
        Object val=getValue();
        if(val==null)
            return null;
        if(val.getClass().isAssignableFrom(File.class))
            return ((File)getValue()).getName();
        //else
        return StringUtils.stripControlCharacters(val.toString());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyTree<PROPERTY> other = (PropertyTree<PROPERTY>) obj;
        if (this.getValue() != other.getValue() && (this.getValue() == null || !this.getValue().equals(other.getValue()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.getValue() != null ? this.getValue().hashCode() : 0);
        return hash;
    }    
    private static Logger logger;
    static {
        logger=Logger.getLogger("info.jlibrarian.mediatree");
    };
    public static void log(PropertyTree<?> node,Level level,String msg,Throwable ex) {
    	PropertyTree.logger.log(level,(node==null?msg:msg+" / at "+node.describePath()),ex);
    }
    public static void log(PropertyTree<?> node,Level level,String msg) {
       	PropertyTree.logger.log(level,(node==null?msg:msg+" / at "+node.describePath()));
    }
    public static void setLogLevel(Level newLevel) {
    	PropertyTree.logger.setLevel(newLevel);
    }
    public void log(Level level,String msg) {
    	PropertyTree.log(this,level,msg);
    }
    public void log(Level level,String msg,Throwable ex) {
    	PropertyTree.log(this,level,msg,ex);
    }
    final public boolean delete() {
        if(parent==null)
            return false;
        if(!parent.dropChild(this))
            return false;
        return setParent(null);
    }
    final protected boolean dropChild(PropertyTree<PROPERTY> childMatch) {
        boolean found=false;
        for(Iterator<PropertyTree<PROPERTY>> c=children.iterator();c.hasNext();) {
            if(c.next() == childMatch) {
                c.remove();
            }
        }
        return found;
    }
    final protected void dropChildren() {
        children=null;
    }
    final private boolean setParent(PropertyTree<PROPERTY> newParent) {
        if(parent!=null) {
        	throw new UnsupportedOperationException("MetaTree: tried to reparent a node, not possible");
        }
        parent=newParent;
        return true;
    }
}
