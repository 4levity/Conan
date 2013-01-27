package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * By default the FlacMetadataTag children are represented as FlacMetadataBlocks.
 * We are most interested in the Vorbis Comment and the Picture tag.  
 * 
 * @author ivan
 */
public class FlacMetadataTag extends MediaTag {
    public FlacMetadataTag(MediaProperty prop, PropertyTree<MediaProperty> parent) {
        super(prop, parent);
    }

    @Override
    public MediaTag load(RandomAccessFile raf, String versions) 
            throws IOException {
        byte b[]=new byte[4];
        MediaFileUtil.read_sure(raf,b);
        if(!(new String(b,"US-ASCII")).equals("fLaC")) {
            return null; // no flac file header
        }
        
        boolean lastBlock;
        int sequence=0;
        do {
            lastBlock=readMetadataBlock(raf,sequence);
            sequence++;
        } while(!lastBlock);

        this.setValue(getNodeProperty().getDescription()+" ("+sequence+" blocks)");
        
        return this;
    }

    /**
     * read a metadata block and create mediatree child
     * 
     * @param raf the open file to read from, positioned at start of blk hdr
     * @return true if the "last block" flag is set, false otherwise
     */
    private boolean readMetadataBlock(RandomAccessFile raf,int seq) throws IOException {
        int blockType=MediaFileUtil.read_sure(raf);
        boolean lastBlock=(blockType & 0x80)>0;
        blockType = blockType & 0x7F;
        if(blockType == FlacMetadataBlock.BLOCKTYPE_VORBIS_COMMENT) {
            // vorbis comment block is treated as a sub-tag within a tag
            // as such we let it read its own size
            VorbisCommentBlock vorbis = new VorbisCommentBlock(MediaProperty.VORBISCOMMENTBLOCK,this);
            vorbis.load(raf,this.getVersion());
        } else {
            // load generic/unsupported flac metadata block
            int blockSize=FlacMetadataBlock.readBlockSize(raf);    
            FlacMetadataBlock flacBlock = new FlacMetadataBlock(MediaProperty.FLACMETADATABLOCK,this);
            flacBlock.load((new Integer(blockType)).toString(), blockSize, raf);
        }
        // TODO: implement PICTURE (also needed for Ogg Vorbis)
        // TODO: (low) implement CUESHEET, and I guess CDDA/Red Book too
        return lastBlock;
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

}
