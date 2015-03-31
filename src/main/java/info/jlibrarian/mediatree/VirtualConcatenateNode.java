package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;

/* 
 * defines a virtual-value node (no object payload) representing a value 
 * which is a concatenated composite of two other sibling properties.
 */
public class VirtualConcatenateNode extends PropertyTree {
	Property part1,part2;
	String separator;
	public VirtualConcatenateNode(Property property, PropertyTree parent,
			Property part1,Property part2,String separator) {
		super(property, parent);
		this.part1=part1;
		this.part2=part2;
		this.separator=separator;
	}

	@Override
	public void setValue(Object o) {
    	// TODO support setting (by pushing properties to parent)
        throw new UnsupportedOperationException("Not supported yet.");
	
        /*
		String[] vals=o.toString().split("/");
		if(vals.length>0) {
			this.getParent().setValue(MediaProperty.VORBISFIELD_ENCODERSOFTWARE,vals[0]);
			if(vals.length>1) {
				String theRest="";
				for(int i=1;i<vals.length;i++) {
					if(i>1) {
						theRest+="/";
					}
					theRest += vals[i];
				}
				this.getParent().setValue(MediaProperty.VORBISFIELD_ENCODERSETTINGS,vals[0]);
			}
		}
		*/
	}

	@Override
	public Object getValue() {
		String value="";
		Object value1=this.getParent().queryBestResult(this.part1);
		Object value2=this.getParent().queryBestResult(this.part2);
		if(value1!=null) {
			value=value1.toString();
		}
		if(value2!=null) {
			if(value.length()>1) {
				value += this.separator;
			}
			value+=value2.toString();
		}
		return value;
	}

}
