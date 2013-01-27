package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.AutoAllocatingByteBuffer;
import info.jlibrarian.stringutils.VersionString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;


public abstract class Id3v2Frame extends FrameNode {
    protected Id3v2FrameFlags nonDefaultFlags=null; // null if all flags are normal and 0 appended header bytes
    public Id3v2Frame(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }
    public Id3v2FrameFlags getFlags() {
        if(this.nonDefaultFlags==null)
            return Id3v2FrameFlags.defaultFlags;
        return this.nonDefaultFlags;
    }
    public String getVersion() {
        if(MediaTag.class.isAssignableFrom(getParent().getClass())) {
            return ((MediaTag)getParent()).getVersion();
        }
        return "";
    }
    protected Id3v2Tag tag() {
        if(Id3v2Tag.class.isAssignableFrom(getParent().getClass())) {
            return ((Id3v2Tag)getParent());
        }
        return null;
    }
    static public int readFrameSize(RandomAccessFile openFile,Id3v2Tag tag) throws IOException {
        int frameSize;
        byte[] buf= new byte[4];
        if(0 == VersionString.compareVersions(tag.getVersion(),"2.3.*")) {
            buf = Id3v2Tag.readTagBytes(openFile, 4, tag.isTagUnsynchronized());
            frameSize = (int)MediaFileUtil.convert32bitsToUnsignedInt(buf);
        } else if(0 == VersionString.compareVersions(tag.getVersion(),"2.4+")) {
            frameSize = Id3v2Tag.readSyncSafeInt(openFile);
        } else {
            // for 2.0/2.2, read 3 bytes
            buf = Id3v2Tag.readTagBytes(openFile, 3, tag.isTagUnsynchronized());
            frameSize=(((int)buf[0])&0xff)*0x10000 + (((int)buf[1])&0xff)*0x100 + (((int)buf[2])&0xff);
        }
        return frameSize;
    }

    public String encode(int encodingType,byte[] raw) {
        String newString=null;
        try {
            if(encodingType==0) {
                newString = new String(raw, "ISO-8859-1");
            } else if (encodingType==1) {
                newString = new String(raw, "UTF-16");
            } else if (encodingType==2) { // should only be in v2.4+
                newString = new String(raw, "UTF-16BE");
            } else if (encodingType==3) { // should only be in v2.4+
                newString = new String(raw, "UTF-8");
            } else {
            	log(Level.WARNING,"invalid text encoding "+encodingType+" (using US-ASCII)");
            	newString = new String(raw, "US-ASCII");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("platform doesn't support standard encoding "+encodingType);
        }
        return newString;
    }

    static public String readFrameId(RandomAccessFile openFile,Id3v2Tag tag) throws IOException {
        int frameIdSize;
        if(VersionString.compareVersions(tag.getVersion(),"2.3") < 0)
        {
            frameIdSize=3;
        } else {
            frameIdSize=4;
        }
        
        byte[] buf=new byte[frameIdSize];
        MediaFileUtil.read_sure(openFile, buf);

        boolean nonzero=false;
        for(int i=0;i<buf.length;i++) {
            if(buf[i]!=0)
                nonzero=true;
        }
        if(!nonzero)
            return null;
        
        return tag.convertId3v2FrameID(buf);
    }
    
    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) 
    			throws IOException {

        byte[] buf=new byte[2];
        long frame_data_start;
        Id3v2FrameFlags flags=new Id3v2FrameFlags();

        if(VersionString.compareVersions(getVersion(),"2.4+") == 0) {
            //2.4+
            buf[0] = (byte)MediaFileUtil.read_sure(openFile);
            buf[1] = (byte)MediaFileUtil.read_sure(openFile);
            frame_data_start = openFile.getFilePointer();
            log(Level.FINER,"Loading id3 v2.4+ frame "+frameId+" at position "+frame_data_start+", len="+dataLength);

            if((buf[0] & 0x8F) != 0 || (buf[1] & 0xB0) != 0) {
            	log(Level.WARNING,"unrecognized Id3v2 2.4+ frame flags "
                        +Integer.toBinaryString((((int)buf[0])&0xff)*256+(((int)buf[1])&0xff)) + " at "
                        +Long.toHexString(this.offset));
            }
            flags.tag_alter_preservation = (buf[0] & 0x40) > 0;
            flags.file_alter_preservation = (buf[0] & 0x20) > 0;
            flags.read_only = (buf[0] & 0x10) > 0;
            if((buf[1]&0x40) > 0) {
                byte[] x=Id3v2Tag.readTagBytes(openFile, 1, this.tag().isTagUnsynchronized());
                flags.grouping_identity_byte = new Integer(x[0]);
            }
            if((buf[1]&0x08) > 0) {
                flags.compression = true;
            }
            if((buf[1]&0x04) > 0) {
                byte[] x=Id3v2Tag.readTagBytes(openFile, 1, this.tag().isTagUnsynchronized());
                flags.encryption_method = new Integer(x[0]);
            }
            if((buf[1]&0x02) > 0) {
                flags.frame_unsynchronization = true;
            }

            if((buf[1]&0x01) > 0) {
                flags.data_length_indicator = new Long(Id3v2Tag.readSyncSafeInt(openFile));
            } else if(flags.compression) {            	
            	log(Level.WARNING,"format warning, compression flag set without data len indicator, offset "
                        +Long.toHexString(this.offset));
            }
        } else if(VersionString.compareVersions(getVersion(),"2.3+")==0) {

            buf = Id3v2Tag.readTagBytes(openFile, 2, this.tag().isTagUnsynchronized());
            frame_data_start=openFile.getFilePointer();
            log(Level.FINER,"Loading id3 v2.3 frame "+frameId+" at position "+frame_data_start+", len="+dataLength);
            if((buf[0] & 0x1F) != 0 || (buf[1] & 0x1F) != 0) {
            	log(Level.WARNING,"unrecognized 2.3+ frame flags "
                        +Integer.toBinaryString((((int)buf[0])&0xff)*256+(((int)buf[1])&0xff)) + " at "
                        +Long.toHexString(this.offset));
            }
            flags.tag_alter_preservation = (buf[0] & 0x80) > 0;
            flags.file_alter_preservation = (buf[0] & 0x40) > 0;
            flags.read_only = (buf[0] & 0x20) > 0;

            if((buf[1] & 0x80) > 0) {
                flags.compression = true;
                flags.data_length_indicator = MediaFileUtil.read32bitUnsignedInt(openFile);
            }
            if((buf[1]&0x40) > 0) {
                byte[] x=Id3v2Tag.readTagBytes(openFile,1,this.tag().isTagUnsynchronized());
                flags.encryption_method = new Integer(x[0]);
            }
            if((buf[1]&0x20) > 0) {
                byte[] x=Id3v2Tag.readTagBytes(openFile,1,this.tag().isTagUnsynchronized());
                flags.grouping_identity_byte = new Integer(x[0]);
            }
            flags.file_alter_preservation = (buf[1] & 0x40) > 0;
            flags.read_only = (buf[1] & 0x20) > 0;
        } else {
            frame_data_start=openFile.getFilePointer();
            log(Level.FINEST,"Loading id3 2.0/2.2 frame "+frameId+" at position "+frame_data_start+", len="+dataLength);
        }
        if(flags.isDefault()) 
            this.nonDefaultFlags=null;
        else
            this.nonDefaultFlags=flags;
        
        if(dataLength<1) {
        	log(Level.SEVERE,"invalid id3v2 frame size at "
                    +Long.toHexString(this.offset) );
            return null;
        }

        //now read the frame.  
        byte[] rawFrameData;
        if(VersionString.compareVersions(getVersion(),"2.4") >= 0) {
            // for 2.4, we read frame_size bytes (minus the actual size of the
            // flag bytes we read past), and then we deunsync.
            int remaining_frame_size = (int) (dataLength - (openFile.getFilePointer() - frame_data_start));
            rawFrameData = new byte[remaining_frame_size];
            MediaFileUtil.read_sure(openFile, rawFrameData);
            if(this.getFlags().frame_unsynchronization) {
                rawFrameData = Id3v2Tag.deUnsynchronize(rawFrameData);
            }
        } else {
            //for 2.3 and earlier, we read frame_size deunsynced bytes.
            int remaining_frame_size = (int) (dataLength - flags.getExtendedHeaderSize());
            rawFrameData = Id3v2Tag.readTagBytes(openFile, remaining_frame_size, this.tag().isTagUnsynchronized());
        }
        return rawFrameData;
    }
	abstract protected void generateFrameData(AutoAllocatingByteBuffer bb) throws FileNotFoundException, IOException;
		//bb.put(this.reload());
    public void generateFullFrame(AutoAllocatingByteBuffer bb) throws FileNotFoundException, IOException {
		AutoAllocatingByteBuffer frameData;

		byte[] fid;
		try {
			fid = this.frameIdentifier.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			this.log(Level.SEVERE, "could not convert frameID to bytes",e);
			throw new RuntimeException("could not convert frameID to bytes");
		}

		if(VersionString.compareVersions(this.getVersion(),"2.3+")==0) {
			// id3 2.3/2.4 frame
			if(fid.length != 4) {
				this.log(Level.SEVERE, "internal error: id3 "+this.getVersion()+" frame was "+fid.length+" bytes, aborting!");
				throw new RuntimeException("internal error: id3 "+this.getVersion()+" frame was "+fid.length+" bytes, aborting!");
			}
			bb.put(fid);
			Id3v2FrameFlags flags=this.getFlags();
			if(flags.compression) {
				log(Level.WARNING,"Cannot write compression flag, unsupported (clearing)");
				flags.compression=false;
			}
			if(flags.encryption_method != null) {
				log(Level.WARNING,"Cannot write encryption flag, unsupported (clearing)");
				flags.encryption_method=null;
			}
			if(VersionString.compareVersions(this.getVersion(), "2.4+")==0) {
				frameData=new AutoAllocatingByteBuffer(32);
				if(flags.grouping_identity_byte!=null)
					frameData.put(flags.grouping_identity_byte);
				if(flags.data_length_indicator!=null)
					frameData.fill(4, (byte)0);
				generateFrameData(frameData);	
				if(flags.data_length_indicator!=null)
					frameData.setRange(0,Id3v2Tag.syncSafeInt(frameData.getLength()));
				
				bb.put(Id3v2Tag.syncSafeInt(frameData.getLength()));
				bb.put(flags.toBytes(getVersion()));
				bb.put(frameData);
			} else {
				frameData=new UnsynchronizingByteBuffer(32).enableUnsynchronizing(true);
				if(flags.grouping_identity_byte!=null)
					frameData.put(flags.grouping_identity_byte);
				generateFrameData(frameData);
				bb.put(MediaFileUtil.convertUnsignedIntTo32bits(frameData.getLength()));
				bb.put(flags.toBytes(getVersion()));
				bb.put(frameData);
			}			
		} else {
			// id3 2.2 frame
			if(fid.length != 3) {
				this.log(Level.SEVERE, "internal error: id3 2.2 frame was "+fid.length+" bytes, aborting!");
				throw new RuntimeException("internal error: id3 2.2 frame was "+fid.length+" bytes, aborting!");
			}
			bb.put(fid);
			frameData=new UnsynchronizingByteBuffer(32).enableUnsynchronizing(true);
			generateFrameData(frameData);
			bb.put(MediaFileUtil.convertUnsignedIntTo24bits(frameData.getLength()));
			bb.put(frameData);
		}
	}
    
}
