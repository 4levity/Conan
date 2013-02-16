package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;
import info.jlibrarian.stringutils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Id3v2UserTextFrame extends Id3v2Frame {
	String value;
	
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
    	this.value=null;
    	if(strings.length>=1) {
    		if(strings[0].length() > 0) {
            	// convert name to property name
        		String name=Registry.getPrefixUserText() + StringUtils.stripControlCharacters(strings[0]);

        		this.clarifyProperty(MediaProperty.getPropertyByName(name, this.getNodeProperty()));
    		}
        } 
    	if(strings.length>1) {
    		// set the value
    		this.value=strings[1];
        }
    	if(strings.length>2) {
    		// nonconforming use of usertext frame. nulls will be converted to "/" character
    		for(int i=2;i<strings.length;i++) {
    			this.value += "/"+strings[i];
    		}
    	}
    	
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
		// must encapsulate a String
		this.value=(String)this.convertObject(o);
	}

	@Override
	public Object getValue() {
		return this.value;
	}
	
}
