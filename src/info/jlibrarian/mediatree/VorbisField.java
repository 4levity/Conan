package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class VorbisField extends FrameNode {
	static String readFieldId(RandomAccessFile raf, int length, VorbisCommentBlock block) throws IOException {
        StringBuilder fieldId = new StringBuilder();
        char c;
        while( (c=(char)MediaFileUtil.read_sure(raf))!= '=' ) {
            fieldId.append(c);
        }
        return new String(fieldId);
    }


	
	Object nativeValue;
    public VorbisField(Property property, PropertyTree parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLen, 
    		RandomAccessFile openFile) 
            throws IOException {
	    byte[] frameData=new byte[dataLen];
	    MediaFileUtil.read_sure(openFile, frameData);
        
        String string=null;
        try {
            string = new String(frameData, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log(Level.SEVERE,"UTF-8 decoder not supported",ex);
            throw new RuntimeException("UTF-8 decoder not supported");
        }
        if(string!=null) {
        	setValue(string);
        }
        
        return frameData;
    }

    @Override
    public Object getValue() {
        return this.nativeValue;
    }

    @Override
    public void setValue(Object o) {
        this.nativeValue=convertObject(o);
    }

	@Override
	public String describeNode() {
		return this.getFrameIdentifier();//+(this.nativeValue==null?"":"="+this.nativeValue);
	}
    
}
