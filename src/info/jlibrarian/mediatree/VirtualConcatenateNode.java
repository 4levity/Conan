package info.jlibrarian.mediatree;

import info.jlibrarian.metatree.MetaTree;

/* 
 * defines a virtual-value node (no object payload) representing a value 
 * which is a concatenated composite of two other properties.
 */
public class VirtualConcatenateNode extends MetaTree<MediaProperty> {
	MediaProperty part1,part2;
	String separator;
	public VirtualConcatenateNode(MediaProperty property, MetaTree<MediaProperty> parent,
			MediaProperty part1,MediaProperty part2,String separator) {
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
		MetaTree<MediaProperty> encoderSoftwareNode=this.getParent().getSingleChild(this.part1);
		MetaTree<MediaProperty> encoderSettingsNode=this.getParent().getSingleChild(this.part2);
		if(encoderSoftwareNode!=null) {
			Object s=encoderSoftwareNode.getValue();
			if(s!=null) {
				value=s.toString();
			}
		}
		if(encoderSettingsNode!=null) {
			Object s=encoderSettingsNode.getValue();
			if(s!=null) {
				if(value.length()>1) {
					value += this.separator;
				}
				value += s.toString();
			}
		}
		return value;
	}

}
