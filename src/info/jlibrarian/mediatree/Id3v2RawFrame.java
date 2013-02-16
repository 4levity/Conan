package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Unsupported/unknown ID3V2 frames are represented by this type of node.
 */

public class Id3v2RawFrame extends Id3v2Frame {
	ResizingByteBuffer rawFrameContents=null;
    public Id3v2RawFrame(Property property, PropertyTree parent) {
        super(property, parent);
    }

    @Override
    public Object getValue() {
    	return rawFrameContents;
    }
    
    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null) {
        	this.setValue(null);
        } else if(frameData.length==0) {
        	this.setValue(null);
        } else { 
        	this.rawFrameContents=new ResizingByteBuffer(frameData);
        }
        return frameData;
    }

	@Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		if(this.rawFrameContents!=null) {
			bb.put(this.rawFrameContents);
		}
	}

	@Override
	public void setValue(Object o) {
		// instance must encapsulate a ResizingByteBuffer
		this.rawFrameContents=(ResizingByteBuffer) this.convertObject(o);
	}
}
