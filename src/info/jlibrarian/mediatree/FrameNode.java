package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.PayloadContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;


public abstract class FrameNode extends PropertyTree<MediaProperty>
	implements PayloadContainer {
    Long offset=null;
    int dataLength=-1;
    String frameIdentifier=null;

    public FrameNode(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }
    abstract protected byte[] load_from_current_position(String frameId,int dataLen,RandomAccessFile openFile) 
            throws IOException;
    
    public String getFrameIdentifier() {
        return frameIdentifier;
    }
    final public byte[] load(String frameId,int dataLen,RandomAccessFile openFile) 
            throws IOException {
        offset = new Long(openFile.getFilePointer());
        this.frameIdentifier=frameId;
        this.dataLength=dataLen;
        return this.load_from_current_position(frameId, dataLen, openFile);
    }
    /**
     * get the raw "payload" of a frame that encapsulates an external type
     * such as an APIC frame.  this is defined as the frame itself by default.
     * @return
     */
    @Override
    public byte[] getPayload() throws IOException {
        byte[] payload=null;
        try {
            payload = reload();
        } catch (FileNotFoundException ex) {
        	this.log(Level.WARNING,"file not found retrieving payload "+ex.toString());
        }
        return payload;
    }
    final public byte[] reload() 
            throws FileNotFoundException, IOException {
        if(!MediaTag.class.isAssignableFrom(getParent().getClass())) 
            throw new RuntimeException("cannot reload frame: parent is not a tag");
        MediaTag parent=(MediaTag)getParent();
        File file=parent.getEnclosingFile();
        if(file==null)
            throw new RuntimeException("cannot reload frame: tag not loaded from file");
        byte[] rawData=null;
        RandomAccessFile raf=null;
        raf=new RandomAccessFile(file,"r");
        rawData=reload(raf);
        raf.close();
        return rawData;
    }
    private byte[] reload(RandomAccessFile raf) throws IOException {
        if((offset==null) || (frameIdentifier==null))
            throw new RuntimeException("cannot reload frame, was never loaded!");
        raf.seek(offset);
        return load(this.frameIdentifier,this.dataLength,raf);
    }

    @Override
    public String describeNode() {
        String description="Frame:"+this.frameIdentifier;
        Object val=this.getValue();
        if(val!=null) {
        	if(val.getClass()==String.class) {
        		description+="="+String.format("%.15s", val);
        	}
        }
        return description;
   }
/*
    @Override
    public void setValue(Object o) {
    	// subclasses should define this behavior if they will be settable
        throw new UnsupportedOperationException("Internal error: Frame is not inherently settable.");
    }
    */
}
