package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;


public class FlacMetadataBlock extends FrameNode {
    static public final byte BLOCKTYPE_STREAMINFO=0;
    static public final byte BLOCKTYPE_PADDING=1;
    static public final byte BLOCKTYPE_APPLICATION=2;
    static public final byte BLOCKTYPE_SEEKTABLE=3;
    static public final byte BLOCKTYPE_VORBIS_COMMENT=4;
    static public final byte BLOCKTYPE_CUESHEET=5;
    static public final byte BLOCKTYPE_PICTURE=6;

    // other blocktypes may be used in future versions

    protected byte blockType;
    
    public FlacMetadataBlock(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }

    public byte getBlockType() {
        return blockType;
    }
    @Override
    public String toString() {
        
        return "FLAC metadata block type "+getBlockTypeString()
                +", size=" + this.dataLength;
    }
    private String getBlockTypeString() {
        switch(getBlockType()) {
            case BLOCKTYPE_STREAMINFO: return "BLOCKTYPE_STREAMINFO";
            case BLOCKTYPE_PADDING: return "BLOCKTYPE_PADDING";
            case BLOCKTYPE_APPLICATION: return "BLOCKTYPE_APPLICATION";
            case BLOCKTYPE_SEEKTABLE: return "BLOCKTYPE_SEEKTABLE";
            case BLOCKTYPE_VORBIS_COMMENT: return "BLOCKTYPE_VORBIS_COMMENT";
            case BLOCKTYPE_CUESHEET: return "BLOCKTYPE_CUESHEET";
            case BLOCKTYPE_PICTURE: return "BLOCKTYPE_PICTURE";
        }
        return "UNKNOWN BLOCK TYPE #"+this.getBlockType();
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLen, RandomAccessFile openFile) throws IOException {
        try {
            blockType = Byte.parseByte(frameId);
        } catch (NumberFormatException ex) {
        	log(Level.WARNING,"invalid metadata block type "+frameId,ex);
        }
        byte[] buf=new byte[dataLen];
        MediaFileUtil.read_sure(openFile, buf);
        return buf;
    }

    @Override
    public Object getValue() {
        return toString();
/*        Object obj=null;
        try {
            obj=reload();
        } catch (TreeInternalException ex) {
            ex.printStackTrace(); 
            //todo:log
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(); 
            //todo:log
        } catch (IOException ex) {
            ex.printStackTrace(); 
            //todo:log
        } catch (FileFormatException ex) {
            ex.printStackTrace(); 
            //todo:log
        }
        return obj;*/
    }
    static int readBlockSize(RandomAccessFile raf) throws IOException {
        // read size
        byte b[]=new byte[3];
        MediaFileUtil.read_sure(raf,b);
        return ((b[0]& 0xff)<<16) + ((b[1]& 0xff)<<8) + (b[2]& 0xff);
    }
}
