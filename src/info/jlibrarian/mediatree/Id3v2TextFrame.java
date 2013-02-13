package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;
import info.jlibrarian.stringutils.SettableFromString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/*
 * node class for any id3 text, map or map-with-locale frame regardless of native type.
 * 
 * native type can be String, Integer, Long, *or* any class that
 * implements SettableFromString.
 */
public class Id3v2TextFrame extends Id3v2Frame {
    protected byte originalEncodingType=-1;
    private Object nativeValue=null; 

    public Id3v2TextFrame(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    public Object getValue() {
        return nativeValue;
    }

    @Override
    public void setValue(Object o) {
        nativeValue=convertObject(o);
    }
    
    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
        originalEncodingType=frameData[0];
        Class<?> dataType=getNodeProperty().getDataType();
        byte[] strBytes=Arrays.copyOfRange(frameData, 1, frameData.length);
        String newValue = encode(originalEncodingType,strBytes);


        if(dataType.isAssignableFrom(String.class)
                || dataType.isAssignableFrom(Integer.class)
                || dataType.isAssignableFrom(Long.class)) {
            // trim all from first \0 character
            int ix=newValue.indexOf(0);
            if(ix>=0) {
        		/* null terminators are specifically indicated in id3 2.4.0
        		 * and optional on most text frames in id3 2.3.0 and 2.2.0
        		 * 
        		 * but if we are trying to assign to a single String/Integer/Long
        		 * then we will **ignore** extra data in multistring frames
        		 */
                //log(Level.WARNING,"Removed invalid null terminator in "+this.getFrameIdentifier()+": "+newValue);
            	newValue=newValue.substring(0,ix);
            }
        	
        	setValue(newValue);
        } else if(SettableFromString.class.isAssignableFrom(dataType)) {
        	// trim final \0 character if any
    		if(newValue.endsWith("\00")) {
    			newValue = newValue.substring(0, newValue.length()-1);
    		}
        	
        	setValue(newValue);
        }
        return frameData;
    }

	@Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't just reload frame, regnerate it
		bb.put(this.reload());
	}
}
