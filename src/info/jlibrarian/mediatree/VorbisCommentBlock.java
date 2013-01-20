package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class VorbisCommentBlock extends MediaTag {
    
    public VorbisCommentBlock(MediaProperty prop, MetaTree<MediaProperty> parent) {
        super(prop, parent);
    }

    private void loadSupportedField(Registry.VorbisCommentConfig cfg, String fieldId, 
            int length, RandomAccessFile openFile) throws IOException {
        VorbisField newField=null;
        try {
            Constructor<? extends FrameNode> cons = cfg.fieldClass
                    .getConstructor(MediaProperty.class,MetaTree.class);
            if (cons != null) {
                newField = (VorbisField) cons.newInstance(cfg.fieldProperty,this);
                newField.load(fieldId, length, openFile);
            }
        } catch (NoSuchMethodException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (SecurityException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (InstantiationException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (IllegalAccessException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (IllegalArgumentException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (InvocationTargetException ex) {
        	log(Level.SEVERE,"reflection FAIL",ex);
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        }
    }

    @Override
    public MediaTag load(RandomAccessFile raf, String versions) throws IOException {
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
        int bytesRemaining = (blockLen - 8) - vendor_length;
        
        while(user_comment_list_length>0) {                     
            int length=(int)(MediaFileUtil.read32bitLittleEndianUnsignedInt(raf));
            String fieldId=VorbisField.readFieldId(raf,length,this);
            
            Registry.VorbisCommentConfig cfg = Registry.getConfigByField(fieldId);
            VorbisField newField=null;
            int valueLength = length - fieldId.length() -1;
            if(cfg != null) {
                this.loadSupportedField(cfg, fieldId, valueLength, raf);
            } else {
                newField=new VorbisRawField(MediaProperty.VORBISFIELD_UNKNOWN,this);
                newField.load(fieldId, valueLength , raf);                
            }
            
            bytesRemaining-=length;
            user_comment_list_length--;
        }
        
        // todo: do this "automatically"
        new SequenceView(MediaProperty.TRACK_SEQUENCE,this,
                MediaProperty.VORBISFIELD_TRACKNUMBER,
                MediaProperty.VORBISFIELD_TRACKTOTAL);
        
        
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
}
