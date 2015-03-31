package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.FileMetadata;
import info.jlibrarian.specialtypes.Id3PictureType;
import info.jlibrarian.specialtypes.ImageAttributes;
import info.jlibrarian.stringutils.ResizingByteBuffer;
import info.jlibrarian.stringutils.VersionString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2PictureFrame extends Id3v2Frame {
    int originalTextEncoding=0;
    protected int picDataFrameOffset=0;
    FileMetadata embeddedImageLink=null;

    // constructor has to look like this; supported id3 tag frames instantiated by reflection
    public Id3v2PictureFrame(Property prop,PropertyTree parent) {
        super(prop, parent);
    }

    @Override
    public byte[] getPayload() throws IOException {
        byte[] frameData = super.getPayload();
        return Arrays.copyOfRange(frameData, this.picDataFrameOffset, frameData.length);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) 
            throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;

        if(frameData.length<8) {
            log(Level.WARNING,"picture frame too short: "+frameData.length);
            return frameData;
        }
        
        this.originalTextEncoding=frameData[0];
        int mimeEnd;
        String mimeType;
        if(VersionString.compareVersions(tag().getVersion(),"2.3")<0)
        {
            mimeType = new String(Arrays.copyOfRange(frameData, 1, 4),"US-ASCII");
            mimeEnd=3;
        } else {
            mimeEnd=1;
            while(mimeEnd<frameData.length && frameData[mimeEnd]!=0)
                mimeEnd++;
            mimeType = new String(Arrays.copyOfRange(frameData, 1, mimeEnd),"US-ASCII");
        }
        if(mimeEnd==frameData.length) {
            log(Level.WARNING,"picture frame too short (2): "+frameData.length);
            return frameData;
        }
        byte picType=frameData[mimeEnd+1];
		
        int descrEnd=mimeEnd+2;
        String description;
        if(this.originalTextEncoding==1 || this.originalTextEncoding==2) {
            while((descrEnd+1)<frameData.length && 
                    (frameData[descrEnd]!=0 || frameData[descrEnd+1]!=0))
                descrEnd+=2;
            if((descrEnd+1)>=frameData.length) {
            	log(Level.WARNING,"picture frame too short (3): "+frameData.length);
            	return frameData;
            }
            description=encode(this.originalTextEncoding, 
                    Arrays.copyOfRange(frameData, mimeEnd+2, descrEnd));
            descrEnd+=2;                  
        } else {
            while(descrEnd<frameData.length && frameData[descrEnd]!=0)
                descrEnd++;
            if(descrEnd==frameData.length) {
            	log(Level.WARNING,"picture frame too short (4): "+frameData.length);
            	return frameData;
            }
            description=encode(this.originalTextEncoding, 
                    Arrays.copyOfRange(frameData, mimeEnd+2, descrEnd));
            descrEnd++;                  
        }
        this.picDataFrameOffset = descrEnd;
        this.setValue(new FileMetadata(this,null,mimeType,description,
        		Id3PictureType.getId3PictureType(picType), 
                Arrays.copyOfRange(frameData, this.picDataFrameOffset, frameData.length)));
        
        return frameData;
    }

    @Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't always reload (but maybe this is appropriate for big images)
		bb.put(this.reload());
	}

	@Override
	public Object getValue() {
		return this.embeddedImageLink;
	}

	@Override
	public void setValue(Object o) {
		// instance must encapsulate a FileMetadata
		this.embeddedImageLink=(FileMetadata) this.convertObject(o);
		Id3v2PictureFrame.updateNodePictureType(this);
	}
	
	static public void updateNodePictureType(PropertyTree node) {
		if(node==null) {
			return;
		}
		if (!FileMetadata.class.isAssignableFrom(node.getNodeProperty().getDataType())) {
			// not a file node?
			return;
		}
		FileMetadata fm = (FileMetadata) node.getValue();
		if(fm==null) {
			// no metadata?
			return;
		}
		ImageAttributes attr=fm.getImageAttributes();
		if(attr==null) {
			// not an image?
			return;
		}
		
		// determine property from metadata
		Property newProperty=MediaProperty.PICTURE;
		Id3PictureType pictureType=attr.getId3PictureType();
		if(pictureType!=null) {
			newProperty = newProperty.extended(pictureType.getDescription(),true);
		}

		// set property
		if(!newProperty.equals(node.getNodeProperty())) {
			node.changeProperty(newProperty);
		}
	}
}
