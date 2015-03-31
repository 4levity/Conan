package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class VorbisCommentBlock extends MediaTag {
    
    public VorbisCommentBlock(PropertyTree parent) {
        super(MediaProperty.VORBISCOMMENTBLOCK, parent);
    }

    public MediaTag load(RandomAccessFile raf,String versions) throws IOException {
        // todo: support Ogg Vorbis here if different
        int blockLen=FlacMetadataBlock.readBlockSize(raf);
        
        // standard vorbis stuff
        int vendor_length = (int)(MediaFileUtil.read32bitLittleEndianUnsignedInt(raf));
        byte[] buf=new byte[vendor_length];
        MediaFileUtil.read_sure(raf, buf);
        String vendor_string;
        try {
            vendor_string = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        	vendor_string = "?";
            log(Level.WARNING,"failed to decode vendor_string, using \"?\"");
        }
        int user_comment_list_length = (int)(MediaFileUtil.read32bitLittleEndianUnsignedInt(raf));
        
        @SuppressWarnings("unused")
		int bytesRemaining = (blockLen - 8) - vendor_length;
        
        while(user_comment_list_length>0) {                     
            int length=(int)(MediaFileUtil.read32bitLittleEndianUnsignedInt(raf));
            String fieldId=VorbisField.readFieldId(raf,length,this);
            
            Registry.GeneralNameValueConfig cfg = Registry.getVorbisConfigByField(fieldId);
            if(cfg==null) {
            	cfg=Registry.getGeneralConfigByField(fieldId);
            }
            VorbisField newField=null;
            int valueLength = length - fieldId.length() -1;
            if(cfg != null) {
                newField=new VorbisField(cfg.fieldProperty,this);
                newField.load(fieldId, valueLength, raf);
            } else {
                newField=new VorbisField(MediaProperty.USERTEXT.extended(fieldId, true),this);
                newField.load(fieldId, valueLength , raf);                
            }
            
            bytesRemaining-=length;
            user_comment_list_length--;
        }
        
        // todo: do this "automatically" from registry?
        new VirtualSequenceNode(MediaProperty.TRACK_SEQUENCE,this,
                MediaProperty.VORBISFIELD_TRACKNUMBER,
                MediaProperty.VORBISFIELD_TRACKTOTAL);

        new VirtualDateNode(MediaProperty.RELEASE_DATE,this);
        new VirtualDateNode(MediaProperty.RECORDING_DATE,this);
        
        new VirtualReplayGainNode(MediaProperty.REPLAYGAIN,this);

        /* in a vorbis comment, the encoder is stored in one field and the encoder settings 
         * are stored in another field. mediatree defines the core property "ENCODER" to mean
         * "encoder and settings" (same as ID3 definition)
         * 
         * id3 does not specify how encoder and settings could be combined, so this class
         * uses a slash "/" between the encoder and settings e.g. "lame 3.91/160kbps VBR"
         * 
         * (if this slash is not present in an ID3 TSSI/TSS tag then the entire string is 
         * treated as the encoder and no encoder-settings are implied)
         */
        new VirtualConcatenateNode(MediaProperty.ENCODER,this,
        		MediaProperty.VORBISFIELD_ENCODERSOFTWARE,
        		MediaProperty.VORBISFIELD_ENCODERSETTINGS,
        		"/");
        
        
        //todo: validate user comment list, bytes remaining
        setValue(vendor_string.replaceAll("\\p{Cntrl}", ""));
    
        return this;
    }

    @Override
    public String getVersion() {
        return "*";
    }

	@Override
	public byte[] generate(int targetLength, int preferredPadding) {
		return new byte[0];
	}

	@Override
	public String describeNode() {
		return "[VorbisComment]";
	}
}
