package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;
import info.jlibrarian.metatree.MetaTreeWithObj;
import info.jlibrarian.stringutils.VersionString;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2TagHeader {
    boolean loaded=false;
    String version=null;
    Long tag_size=null; // including the extended header
    Long tag_offset=null; // position in file
    int total_extended_header_size=0; // # of raw bytes used by ext. header
    boolean unsynchronisation=false;
    boolean experimental_indicator=false;
    boolean footer_present=false;

    // extended header contents, null if not present:
    Long reported_padding_size=null;
    Boolean tag_is_update=null;
    Long crc32=null;
    Id3v2TagRestrictions restrictions=null;

    public Id3v2TagHeader() {
    }
    
    public long getTagOffset() {
        return tag_offset;
    }

    public long getTagSize() {
        return tag_size;
    }
    public String getVersion() {
        return version;
    }
    public boolean isLoaded() {
        return loaded;
    }
    public boolean isTagUnsynchronized()
    {
        return this.unsynchronisation;
    }
    public boolean load(RandomAccessFile f,MetaTreeWithObj<MediaProperty> owner) 
            throws IOException {        

        loaded=false;
        
        byte id3v2Header[]=new byte[10];

        f.seek(0L);
        // TODO: load tags from elsewhere in file
        
        int res=f.read(id3v2Header);
        if(res!=10) {
        	owner.log(Level.FINER,"couldn't read 10 bytes from file, definitely no id3 tag");
            return false; // couldn't read 10 bytes, definitely no id3 tag
        }
        if( !   (id3v2Header[0]=='I' && id3v2Header[1]=='D' && id3v2Header[2]=='3'
                && id3v2Header[3]<0xFF && id3v2Header[4]<0xFF /* && (id3v2Header[5]&0x0F) == 0 flags validated later */
                && id3v2Header[6]<0x80 && id3v2Header[7]<0x80 && id3v2Header[8]<0x80 && id3v2Header[9]<0x80) )
        {
        	owner.log(Level.FINER,"did not detect id3 tag");
            return false; // header didn't match spec detection
        }

        version="2."+(new Integer(id3v2Header[3])).toString()+"."+(new Integer(id3v2Header[4])).toString();

        int flags=id3v2Header[5];
        unsynchronisation = (flags & 0x80)!=0;
        boolean extended_header_present = (flags & 0x40)!=0;
        owner.log(Level.FINEST,"id3 "+version+" tag found, unsync="+unsynchronisation+".  reading from "+f.getFilePointer()); 
        if(extended_header_present && (VersionString.compareVersions(version,"2.2.*")==0) )  {
            /**
             * in 2.2.* only, this bit means "compression" and spec states
             * "Since no compression scheme has been decided yet, the ID3 
             * decoder (for now) should just ignore the entire tag if the 
             * compression bit is set."
             */
        	owner.log(Level.WARNING,"Id3v2 unsupported compression flag detected, tag will not load.");
        	return false;
        }
        experimental_indicator = (flags & 0x20)!=0;
        footer_present = (flags & 0x10)!=0;
        if(footer_present && 
                (VersionString.compareVersions(version,"2.4") < 0) ) {
            // footer is not supported in older tag versions
        	owner.log(Level.WARNING,"Id3v2 footer flag ignored, not valid for tag version "+version);
        	footer_present=false;
        } 
        if((flags & 0x0F) > 0) {
        	owner.log(Level.WARNING,"Id3v2 unknown flag was set, ignoring ("+flags+")");
        }
        
        // so far so good, let's interpret the tag size        
        Integer hdr_tag_size=Id3v2Tag.convertSyncSafeBytes_to_Int(Arrays.copyOfRange(id3v2Header, 6, 10));
        if(hdr_tag_size<0) { // invalid syncsafe integer!
            // this isn't going to happen 'cause I validated when recognizing
            // the tag according to the recommendation in the id3v2 spec
        	owner.log(Level.SEVERE,"invalid Id3v2 tag size (validator fail)");
            throw new RuntimeException("invalid Id3v2 tag size (validator fail)");
        }
        tag_size = new Long(hdr_tag_size);
        tag_offset = new Long(f.getFilePointer() - 10);

        if(extended_header_present) {
            if(!loadExtendedHeader(f,owner))
            	return false;
            this.total_extended_header_size = (int)(f.getFilePointer() - this.tag_offset) - 10;
        }
        loaded=true;
        return true;
    }


    private boolean loadExtendedHeader(RandomAccessFile f,MetaTreeWithObj<MediaProperty> owner)
            throws IOException {
        if(VersionString.compareVersions(version, "2.3.*") == 0)  {
            // in id3 v2.3 only, extended header needs to be deunsynchronized
            // and is formatted differently.
            long hdrsize=MediaFileUtil.convert32bitsToUnsignedInt(
                    Id3v2Tag.readTagBytes(f, 4, this.unsynchronisation));
            if(hdrsize!=6 && hdrsize!=10) {
            	owner.log(Level.WARNING,"id3v2.3 invalid extended header size");
            	return false;
            }
            byte[] extflags=Id3v2Tag.readTagBytes(f, 2, this.unsynchronisation);
            if(((extflags[0] & 0x7F) > 0) || (extflags[1]!=0)) {
            	owner.log(Level.WARNING,"id3v2.3 unknown extended header flags");
            	return false;
            }
            boolean has_crc=(extflags[0]&0x80)>0;
            if((has_crc && hdrsize==6) || (!has_crc && hdrsize==10)) {
            	owner.log(Level.WARNING,"id3v2.3 ext. header size/crc flag mismatch");
            	return false;
            }
            
            reported_padding_size=new Long(MediaFileUtil.convert32bitsToUnsignedInt(
                    Id3v2Tag.readTagBytes(f, 4, this.unsynchronisation) ));
            // TODO: validate padding size

            if(has_crc) {
                // 32 more bits appended to header
                crc32=new Long(MediaFileUtil.convert32bitsToUnsignedInt(
                        Id3v2Tag.readTagBytes(f, 4, this.unsynchronisation) ));
            }
            // 2.3 extended header done!
        } else if (VersionString.compareVersions(version, "2.4") >= 0) {
            long extheaderstart = f.getFilePointer();

            // 2.4 or later extended header
            int hdrsize=Id3v2Tag.readSyncSafeInt(f);
            int numflagbytes=MediaFileUtil.read_sure(f);            
            if(numflagbytes!=1) {
            	owner.log(Level.WARNING,"id3v2 ext header numflagbytes invalid value="+Integer.toString(numflagbytes));
            	return false;
            }
            int extflags = MediaFileUtil.read_sure(f);
            tag_is_update=new Boolean((extflags & 0x40)!=0);
            if(tag_is_update) {
                if(MediaFileUtil.read_sure(f)!=0) {
                	owner.log(Level.WARNING,"id3v2 update flag data length invalid");
                	return false;
                }
            }
            boolean has_crc=((extflags & 0x20)!=0);
            if(has_crc) {
                if(MediaFileUtil.read_sure(f)!=5) {
                	owner.log(Level.WARNING,"id3v2 crc flag data length invalid");
                	return false;
                }
                long crc1=Id3v2Tag.readSyncSafeInt(f);
                // plus 7 more bits
                int crclsb=MediaFileUtil.read_sure(f);
                if(crclsb>=0x80) {
                	owner.log(Level.WARNING,"id3v2 crc formatting error");
                	return false;
                }
                crc32 = new Long((crc1 << 7) + crclsb);
            }
            boolean tagrestrict = ((extflags & 0x10)!=0);
            if(tagrestrict) {
                if(MediaFileUtil.read_sure(f)!=1) {
                	owner.log(Level.WARNING,"id3v2 tag-restrictions flag data length invalid");
                	return false;
                }
                restrictions=new Id3v2TagRestrictions(MediaFileUtil.read_sure(f));
            }
            long bytesRead=f.getFilePointer() - extheaderstart;
            if (bytesRead != hdrsize) {
            	owner.log(Level.WARNING, "Read "+bytesRead+" bytes, but tag extended header size should be "+hdrsize);
            	return false;
            }
        } else {
        	owner.log(Level.WARNING,"tried to load extended hdr on invalid version "+version);
        	return false;
        }
        return true;
    }

    /**
     * 
     * @param tagDataSize 	# of bytes in frames + padding (not including header, ext header, or footer)
     * @param owner
     * @return
     */
    public byte[] generateHeaderAndExt(int tagDataSize,int paddingSize,MetaTree<?> owner) {    	UnsynchronizingByteBuffer bb=new UnsynchronizingByteBuffer(10).enableUnsynchronizing(false);
    	UnsynchronizingByteBuffer xhdrPayload=new UnsynchronizingByteBuffer(0).enableUnsynchronizing(true);

		bb.put('I'); bb.put('D'); bb.put('3');

    	String[] strings=this.version.split("\\.");
		if(strings.length == 3) {
			bb.put(Byte.parseByte(strings[1]));
			bb.put(Byte.parseByte(strings[2]));
		} else {
			owner.log(Level.WARNING, "no version available, setting to v2.3.0");
			this.version = "2.3.0";
			bb.put(3); bb.put(0);
		}

		byte flags=0;
		byte extendedFlags=0;
		if(this.unsynchronisation)
			flags |= 0x80;
		if(this.experimental_indicator) {
			if(VersionString.compareVersions(this.getVersion(), "2.3+")==0) {
				flags |= 0x20;
			} else {
				owner.log(Level.WARNING, "Invalid experimental flag setting (cleared)");
				this.experimental_indicator=false;
			}
		}
		if(this.footer_present) {
			if(VersionString.compareVersions(this.getVersion(), "2.4+")==0) {
				flags |= 0x10;
			} else {
				owner.log(Level.WARNING, "Invalid footer flag setting (cleared)");
				this.footer_present=false;
			}
		}
		if(this.tag_is_update!=null) {
			if(VersionString.compareVersions(this.getVersion(), "2.4+")==0) {
				flags |= 0x40;
				extendedFlags |= 0x40;
				xhdrPayload.put(0);
			} else {
				owner.log(Level.WARNING, "Invalid tag-is-update setting for this tag version (cleared)");
				this.tag_is_update=false;
			}
		}		
		if(this.crc32 != null) {
			if(VersionString.compareVersions(this.getVersion(), "2.4+")==0) {
				flags |= 0x40;
				extendedFlags |= 0x20;
				xhdrPayload.put(5);
				// we just assume that the CRC we have is correct and write it - 
				// CRC must be updated by tag writer before calling!
				xhdrPayload.put(((int)(this.crc32 >> 28)) & 0x0F);
				xhdrPayload.put(Id3v2Tag.syncSafeInt((int)(this.crc32 & 0x0FFFFFFF)));
			} else if(VersionString.compareVersions(this.getVersion(), "2.3+")==0) {
				/* 
				flags |= 0x40;
				extendedFlags |= 0x80;

				 * after a lot of soul searching
				 * I decided that the best thing to do 
				 * when asked to write an extended header to a v2.3 tag
				 * is this:
				 */
				owner.log(Level.WARNING,"cannot in good conscience create an id3 2.3 extended header, clearing CRC");
				this.crc32=null;
			} else {
				owner.log(Level.WARNING, "Invalid CRC32 setting for this tag version (cleared)");
				this.crc32=null;
			}
		}
		if(this.restrictions != null) {
			if(VersionString.compareVersions(this.getVersion(), "2.4+")==0) {
				flags |= 0x40;
				extendedFlags |= 0x10;
				xhdrPayload.put(1);
				xhdrPayload.put(this.restrictions.getBitmap(owner));
			} else {
				owner.log(Level.WARNING, "Invalid restriction flag for this tag version (cleared)");
				this.restrictions=null;
			}
		}

		bb.put(flags);

		if(extendedFlags==0) {
			bb.put(Id3v2Tag.syncSafeInt(tagDataSize+paddingSize));
		} else {
			// write tag size & extended header
			if((VersionString.compareVersions(this.getVersion(), "2.3.*")==0)&&(crc32!=null)) {
				// this "situation" should've been corrected above
				throw new UnsupportedOperationException("cannot in good conscience write an id3 2.3 extended header");
				/*
				 * id3 v2.3 extended header is a land mine waiting to explode
				 * 
				 * the following commented code might indeed work
				 * but please do not implement 2.3 extended headers when writing tags
				 * thanks
				 * 
				 * -management
				 * 

				// guessing ext header unsynchronized length
				bb.put(Id3v2Tag.syncSafeInt(tagDataSize+14));

				int extHdrStart=bb.getLength();
				bb.enableUnsynchronizing(true);
				// write CRC header
				// for 2.3 we will only write an extended header if a CRC32 value is present
				bb.put(0x00);
				bb.put(0x00);
				bb.put(0x00);
				bb.put(0x0A);
				bb.put(0x80);
				bb.put(0x00);
				//size of padding
				bb.put(MediaFileUtil.convertUnsignedIntTo32bits(paddingSize));
				//crc
				bb.put(MediaFileUtil.convertUnsignedIntTo32bits(this.crc32));
				this.total_extended_header_size=bb.getLength()-extHdrStart;
				if(total_extended_header_size != 14) {
					// stupid deunsynchronization!
					// change length of tag...
					bb.setRange(extHdrStart-4,Id3v2Tag.syncSafeInt(tagDataSize+paddingSize+total_extended_header_size));
					
				}
				*/
			} else {
				// tag size:
				bb.put(Id3v2Tag.syncSafeInt(tagDataSize+paddingSize+6+xhdrPayload.getLength()));
				// 2.4+ extended header:
				bb.put(Id3v2Tag.syncSafeInt(6+xhdrPayload.getLength()));
				bb.put(1);
				bb.put(extendedFlags);
				bb.put(xhdrPayload.getAll());
				this.total_extended_header_size=6+xhdrPayload.getLength();
			}
		}
		
    	return bb.getAll();
    }
    
    public byte[] generateFooter(int tagDataSize,MetaTree<?> owner) {
    	if(this.footer_present) {
    		// TODO: implement, or insert comments scoffing at feature
    		throw new UnsupportedOperationException("generateFooter not yet implemented");
    	}
    	return new byte[0];
    }

    @Override
    public String toString() {
        return "Id3v2Tag version "+version+" ("+tag_size+" bytes at offset "+tag_offset+")";
    }
}
