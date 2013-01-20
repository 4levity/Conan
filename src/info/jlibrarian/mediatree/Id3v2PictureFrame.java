package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2PictureFrame extends Id3v2Frame {
    int originalTextEncoding=0;
    ImageProperties picHeader=null;
    protected int picDataFrameOffset=0;

    public Id3v2PictureFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
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
        picHeader = new ImageProperties(this, mimeType, description, picType, 
                Arrays.copyOfRange(frameData, this.picDataFrameOffset, frameData.length));
        return frameData;
    }

    @Override
    public Object getValue() {
        if(!getProperty().getDataType().isAssignableFrom(ImageProperties.class)) {
            throw new ClassCastException("getValue- not an EmbeddedPictureHeader");
        }
        return picHeader;
    }

    @Override
    public boolean isValueValid() {
        return true; // todo: validate image stuff against something
                    // without loading the image from disk with getValue()...
       
    }

    @Override
    public String toString() {
        return (picHeader==null?"embedded picture: null":picHeader.toString());
    }
}
