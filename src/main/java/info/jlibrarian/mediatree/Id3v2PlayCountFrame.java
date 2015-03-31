package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;

public class Id3v2PlayCountFrame extends Id3v2Frame {
	Long playCounter;
	
    // constructor has to look like this; supported id3 tag frames instantiated by reflection
	public Id3v2PlayCountFrame(Property property,PropertyTree parent) {
		super(property, parent);
	}

	@Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: hey you! before implementing the frame generators, make this and ALL unimplemented frame generators throw UnsupportedOperationException. test thoroughly to ensure there aren't any unnecessary calls to regenerate frame data during reading/parsing/querying
		bb.put(this.reload());
	}

	@Override
	public void setValue(Object o) {
		// property of this frame must have data type Long
		this.playCounter=(Long)this.convertObject(o);
	}

	@Override
	public Object getValue() {
		return this.playCounter;
	}

	@Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) 
            throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;

        if(frameData.length<4) {
            log(Level.WARNING,"playcount frame too short: "+frameData.length);
            return frameData;
        }
        if(frameData.length>8 || (frameData.length==8 && (frameData[0] & 0x80) != 0) ) {
        	//oh come on
            log(Level.WARNING,"playcount frame too long: "+frameData.length);
            return frameData;        	
        }
        
    	this.playCounter = 0L;
        if(frameData.length>4) {
        	for(int yeahRightMoreThanFourBillionPlays=0;yeahRightMoreThanFourBillionPlays<frameData.length-4 ; yeahRightMoreThanFourBillionPlays++) {
        		this.playCounter = this.playCounter << 8;
        		this.playCounter += (0xff & frameData[yeahRightMoreThanFourBillionPlays]);
        	}
        	this.playCounter = this.playCounter << 32;
        }
        this.playCounter += MediaFileUtil.convert32bitsToUnsignedInt(frameData, frameData.length-4);
        return frameData;        
    }
}
