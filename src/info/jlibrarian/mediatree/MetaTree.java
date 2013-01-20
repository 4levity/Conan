package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MetaTree<PROPERTY> {
    private final PROPERTY property;
    private MetaTree<PROPERTY> parent;
    private List<MetaTree<PROPERTY>> children;
    
    public MetaTree(PROPERTY property,MetaTree<PROPERTY> parent) {
        this.property=property;
        this.parent=parent;
        this.children=null;
        if(parent!=null)
            parent.addChild(this);
    }
    final public PROPERTY getProperty() {
        return property;
    }
    final protected boolean delete() {
        if(parent==null)
            return false;
        if(!parent.dropChild(this))
            return false;
        return setParent(null);
    }
    final protected boolean dropChild(MetaTree<PROPERTY> childMatch) {
        boolean found=false;
        for(Iterator<MetaTree<PROPERTY>> c=children.iterator();c.hasNext();) {
            if(c.next() == childMatch) {
                c.remove();
            }
        }
        return found;
    }
    final protected void dropChildren() {
        children=null;
    }
    final private boolean setParent(MetaTree<PROPERTY> newParent) {
        if(parent!=null) {
        	throw new UnsupportedOperationException("MetaTree: tried to reparent a node, not possible");
        }
        parent=newParent;
        return true;
    }
    final protected MetaTree<PROPERTY> getParent() {
        return parent;
    }
    private boolean addChild(MetaTree<PROPERTY> newChild) {
        if(children==null)
            children=new LinkedList<MetaTree<PROPERTY>>();
        children.add(newChild);
        return true;
    }
    final public Iterator<MetaTree<PROPERTY>> getChildren() {
        if(children==null)
            return null;
        return children.iterator();
    }
    final public MetaTree<PROPERTY> getSingleChild(PROPERTY p) {
        MetaTree<PROPERTY> match=null;
        for(MetaTree<PROPERTY> node : children) {
            if(node.getProperty().equals(p)) {
//                if(match!=null)
  //                  return null; // one and ONLY one match
    //            match=node;
                return node;
            }
        }
        return match;
    }
    final public boolean represents(PROPERTY p) {
        if(property.equals(p))
            return true;
        if(children==null)
            return false;
        for(MetaTree<PROPERTY> child : children) {
            if(child.represents(p))
                return true;
        }
        return false;
    }
    
    public boolean isValueValid() {
        if(getValue()==null)
            return true;
        if(this.getValue().getClass().isAssignableFrom(String.class)) {
            // default behavior for strings: no nulls or control characters allowed
            if(toString().replaceAll("\\p{Cntrl}","").equals(toString()))
                return true;
            return false;
        } // else default is to assume valid
        return true;
    }
    public void forceValueValid() {
        if(getValue()==null)
            return;
        if(this.getValue().getClass().isAssignableFrom(String.class)) {
            // default behavior for strings: no nulls or control characters allowed
            setValue(toString().replaceAll("\\p{Cntrl}",""));
        } // else default is to assume valid
        return;
    }
    public void forceSubtreeValid() {
        if(children==null)
            return;

        for(MetaTree<PROPERTY> child : children) {
            child.forceSubtreeValid();
        }
    }
    public int getConfidence() {
        return 1; 
    }
    public void setConfidence(int new_confidence) { 
        throw new UnsupportedOperationException("setConfidence not implemented");
    }

    public abstract void setValue(Object o);
    public abstract Object getValue();
 
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
    public Object convertObject(Class<?> targetType,Object o) {
        if(targetType.equals(Long.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Long storage
            try {
                o=new Long(Long.parseLong(o.toString()));
            } catch (NumberFormatException ex) {
                log(Level.WARNING,"String to Long conversion failed on "+o.toString());
                return null;
            }
        } else if(targetType.equals(Integer.class) 
                && String.class.isAssignableFrom(o.getClass()) ) {
            // automatically convert string initializer to Integer storage
            try {
                o=new Integer(Integer.parseInt(o.toString()));
            } catch (NumberFormatException ex) {
            	log(Level.WARNING,"String to Integer conversion failed on "+o.toString());
                return null;
            }
        } else if(!targetType.isAssignableFrom(o.getClass())) {
            String msg="runtime type mismatch: can't assign "+targetType.getName()+" to "+o.getClass().getName();
            log(Level.SEVERE,msg);
            throw new ClassCastException(msg);
        }
        return o;
    }
    
/*    public boolean createChild(MetaTree<PROPERTY> copyFrom) {
        // default: node unable to create, must add children from load() or other methods
        return false;
    }
    public boolean canCreateChild(PROPERTY newProperty) {
        return false;
    }
*/  
    // functions to create text representation of the tree (TODO: XML?)
    public String describeTree() {
        return describeTree(0);
    }
    public String describeTree(int indent) {
        String s=new String();
        for(int i=indent;i>0;i--)
            s += "   ";
        s += this.describeNode() + System.getProperty("line.separator");
        for(Iterator<MetaTree<PROPERTY>> j=getChildren();j !=null && j.hasNext();) {
            s += j.next().describeTree(indent+1);
        }
        return s;
    }
    public String describeNode() {
        return  (getProperty()==null?"null":getProperty().toString().replaceAll("\\p{Cntrl}",""))
                +": "+ toString() + (isValueValid()?"":" (invalid)"); 
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
        return val.toString().replaceAll("\\p{Cntrl}","");
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
        final MetaTree<PROPERTY> other = (MetaTree<PROPERTY>) obj;
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
    public static void log(MetaTree<?> node,Level level,String msg,Throwable ex) {
    	MetaTree.logger.log(level,(node==null?msg:msg+" / at "+node.describePath()),ex);
    }
    public static void log(MetaTree<?> node,Level level,String msg) {
       	MetaTree.logger.log(level,(node==null?msg:msg+" / at "+node.describePath()));
    }
    public void log(Level level,String msg) {
    	MetaTree.log(this,level,msg);
    }
    public void log(Level level,String msg,Throwable ex) {
    	MetaTree.log(this,level,msg,ex);
    }
}
