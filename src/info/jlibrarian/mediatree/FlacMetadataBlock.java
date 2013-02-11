package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.FlacHeader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;


/* PropertyTree class representing metadata at the beginnning of a FLAC file
 * 
 * We are most interested in the streaminfo, Vorbis Comment and Picture tag.
 * (other blocks are ignored/skipped)  
 * 
 * @author ivan
 */
public class FlacMetadataBlock extends MediaTag {
    public FlacMetadataBlock(MediaProperty prop, PropertyTree<MediaProperty> parent) {
        super(prop, parent);
    }

    @Override
    public MediaTag load(RandomAccessFile raf, String versions) 
            throws IOException {
        if(raf.length()<42) {
        	// definitely not FLAC
        	return null;
        }
        
        byte b[]=new byte[4];
        raf.seek(0L);
        MediaFileUtil.read_sure(raf,b);
        if(!(new String(b,"US-ASCII")).equals("fLaC")) {
            return null; // no flac file header
        }
        
        int sampleRateHz;
        byte numChannels; 
        byte bitsPerSample;
		long numSamples;

		byte[] md5=new byte[16];
		int numMetadataBlocks=0;
		
        boolean lastBlock;
        do {
            int blockType=MediaFileUtil.read_sure(raf);
            lastBlock=(blockType & 0x80)>0;
            blockType = blockType & 0x7F;
            int blockSize;
            
            if(numMetadataBlocks==0) {
            	// first block must be stream info
            	if(blockType != FlacMetadataBlock.BLOCKTYPE_STREAMINFO) {
                	log(Level.WARNING,"Malformed FLAC file: First metadata must be STREAMINFO");
                	return null;
            	}
            	blockSize=FlacMetadataBlock.readBlockSize(raf);
            	if(blockSize != 34) {
            		log(Level.WARNING,"Malformed FLAC file: STREAMINFO must be 34 bytes long");
                	return null;
            	}
            	
            	byte buf[]=new byte[18];
            	MediaFileUtil.read_sure(raf,buf);
            	sampleRateHz = ((buf[10] & 0xff) << 12) + ((buf[11] & 0xff) << 4)
            			+ ((buf[12] & 0xff)>>4);
            	numChannels = (byte)(1 + ((buf[12] & 0x0e) >> 1));
            	bitsPerSample = (byte)(1 + ((buf[12] & 0x01) << 4) + ((buf[13] & 0xf0)>>4) );
            	numSamples = (((long)(buf[13] & 0x0f))<<32) + MediaFileUtil.convert32bitsToUnsignedInt(buf, 14);            	

            	md5=new byte[16];
            	MediaFileUtil.read_sure(raf, md5);
            	
                FlacHeader hdr=new FlacHeader(sampleRateHz, numChannels, bitsPerSample, numSamples, 
                		md5, numMetadataBlocks, raf.getFilePointer());
                
                this.setValue(hdr);
                            	            	
            } else if(blockType == FlacMetadataBlock.BLOCKTYPE_STREAMINFO) {
            	log(Level.WARNING,"Malformed FLAC file: Multiple STREAMINFO blocks");
            	return null;
            } else if(blockType == FlacMetadataBlock.BLOCKTYPE_VORBIS_COMMENT) {
                // vorbis comment block is treated as a sub-tag within a tag
                // as such we let it read its own size
                VorbisCommentBlock vorbis = new VorbisCommentBlock(this);
                vorbis.load(raf,this.getVersion());
            } else if(blockType == FlacMetadataBlock.BLOCKTYPE_PICTURE) {
                blockSize=FlacMetadataBlock.readBlockSize(raf);
            	FlacPicture pic = new FlacPicture(this);
            	pic.load(FlacMetadataBlock.getBlockTypeString((byte)blockType),blockSize,raf);
            } else {
                // skip over generic/unsupported flac metadata block
                blockSize=FlacMetadataBlock.readBlockSize(raf);  
                byte[] readx=new byte[blockSize];
                MediaFileUtil.read_sure(raf,readx);
            }
            // TODO: (low) implement CUESHEET, and I guess CDDA/Red Book too
            numMetadataBlocks++;
        } while(!lastBlock);

        return this;
    }

    @Override
    public String getVersion() {
        return("*");
    }

	@Override
	public byte[] generate(int targetLength, int preferredPadding) {
		// TODO implement!!
		return new byte[0];
	}


    static public final byte BLOCKTYPE_STREAMINFO=0;
    static public final byte BLOCKTYPE_PADDING=1;
    static public final byte BLOCKTYPE_APPLICATION=2;
    static public final byte BLOCKTYPE_SEEKTABLE=3;
    static public final byte BLOCKTYPE_VORBIS_COMMENT=4;
    static public final byte BLOCKTYPE_CUESHEET=5;
    static public final byte BLOCKTYPE_PICTURE=6;

    static public String getBlockTypeString(byte blockType) {
        switch(blockType) {
            case BLOCKTYPE_STREAMINFO: return "BLOCKTYPE_STREAMINFO";
            case BLOCKTYPE_PADDING: return "BLOCKTYPE_PADDING";
            case BLOCKTYPE_APPLICATION: return "BLOCKTYPE_APPLICATION";
            case BLOCKTYPE_SEEKTABLE: return "BLOCKTYPE_SEEKTABLE";
            case BLOCKTYPE_VORBIS_COMMENT: return "BLOCKTYPE_VORBIS_COMMENT";
            case BLOCKTYPE_CUESHEET: return "BLOCKTYPE_CUESHEET";
            case BLOCKTYPE_PICTURE: return "BLOCKTYPE_PICTURE";
        }
        return "UNKNOWN BLOCK TYPE #"+blockType;
    }

    static int readBlockSize(RandomAccessFile raf) throws IOException {
        // read size
        byte b[]=new byte[3];
        MediaFileUtil.read_sure(raf,b);
        return ((b[0]& 0xff)<<16) + ((b[1]& 0xff)<<8) + (b[2]& 0xff);
    }

}
