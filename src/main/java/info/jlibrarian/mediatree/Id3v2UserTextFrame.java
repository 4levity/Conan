package info.jlibrarian.mediatree;

import info.jlibrarian.mediatree.Registry.GeneralNameValueConfig;
import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;
import info.jlibrarian.stringutils.SettableFromString;
import info.jlibrarian.stringutils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Id3v2UserTextFrame extends Id3v2Frame {
	String name;
	Object nativeValue;
	
	public Id3v2UserTextFrame(Property property, PropertyTree parent) {
		super(property, parent);
	}

	@Override
	protected byte[] load_from_current_position(String frameId, int dataLength,
			RandomAccessFile openFile) throws IOException {
		byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
			return null;
		if(frameData.length<1)
			return frameData;
        int originalEncodingType=frameData[0];
        int end=frameData.length;
        if(end>1 && frameData[end-1]==0) {
        	// trim trailing \0
        	end--;
        }
        String strings[]=encode(originalEncodingType,Arrays.copyOfRange(frameData, 1, end))
        		.split("\\x00");
    	this.nativeValue=null;
    	if(strings.length>=1) {
    		if(strings[0].length() > 0) {
    			// store field name
    			this.name=StringUtils.stripControlCharacters(strings[0]);
            	// convert name to property
    			GeneralNameValueConfig cfg=Registry.getGeneralConfigByField(strings[0]);
    			if(cfg!=null) {
    				// found a general name/value pair config for this field name
    				this.changeProperty(cfg.fieldProperty);
    			} else {
    				// generic user text extended property named after this field
            		this.changeProperty(this.getNodeProperty().extended(this.name, true));
    			}
    			
    		}
        }
    	
    	// name/property are set, now handle value
    	String settableValue=null;
    	if(strings.length>1) {
    		// set the value
    		settableValue=strings[1];
        }
    	if(strings.length>2) {
    		// non-spec for TXXX frame. this should be rare.
    		String separator;
    		if(SettableFromString.class.isAssignableFrom(this.getNodeProperty().getClass())) {
        		// nonconforming use of usertext frame, but we will let SettableFromString take care of data
    			// restore null character in between extra strings
    			separator=Character.toString((char)0);
    		} else {
        		// nonconforming use of usertext frame, replace null with slash
    			separator="/";
    		}
    		
    		for(int i=2;i<strings.length;i++) {
    			settableValue += separator+strings[i];
    		}
    	}
    	this.setValue(settableValue);
    	
		return frameData;
	}

	@Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: generate, don't reload
		bb.put(this.reload());
	}

	@Override
	public void setValue(Object o) {
		this.nativeValue=this.convertObject(o);
	}

	@Override
	public Object getValue() {
		return this.nativeValue;
	}

	@Override
    public String describeNode() {
        String description="Frame:"+this.frameIdentifier;
        if(this.name!=null) {
       		description+="/"+this.name;
        }
        /*
        if(this.nativeValue!=null) {
       		description+="="+String.format("%.15s", this.nativeValue.toString())
       				+(this.nativeValue.toString().length()>15?"...":"");
        }
        */
        return description;
   }
}
