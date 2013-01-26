package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;
import info.jlibrarian.stringutils.VersionString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;


public class Id3v2Tag extends MediaTag {
    private Id3v2TagHeader originalHeader=null;

    public Id3v2Tag(MediaProperty prop, MetaTree<MediaProperty> parent) {
        super(prop, parent);
    }
    
    public String getVersion() {
        if(this.originalHeader==null)
            return null;
        return this.originalHeader.getVersion();
    }

    public boolean isTagUnsynchronized() {
        if(this.originalHeader==null)
            return false;
        return this.originalHeader.isTagUnsynchronized();
    }

    final public MediaTag load(RandomAccessFile raf,String versions) 
                throws IOException {
        // search for tag, load header info etc, set container value
        Id3v2TagHeader hdr;
        hdr=new Id3v2TagHeader();
        hdr.load(raf,this);
        if(hdr.isLoaded())
        {
            if(VersionString.compareVersions(hdr.getVersion(),versions)==0) {
                originalHeader=hdr;
                setValue(hdr);
                loadFrames(raf);
                
                // TODO: optionally, recreate "clean" tag from this,
                // by pushing all the old tag's metadata into new tag.

                return this;
            } 
            // else header version didn't match
        }
        delete();
        return null;
    }
    
    /**
     * converts an id3v2 "sync safe 28 bit integer"
     * @param d
     * @param i
     * @return integer value, or -1 if format is invalid
     */
    public static Integer convertSyncSafeBytes_to_Int(byte[] d) {
        byte b3=d[0];
        byte b2=d[1];
        byte b1=d[2];
        byte b0=d[3];

        if((0xff&b3)>0x80 || (0xff&b2)>0x80 || (0xff&b1)>0x80 || (0xff&b0)>0x80) {
            return -1;
        }
        return ((0xff&b3)<<21)+((0xff&b2)<<14)+((0xff&b1)<<7)+(0xff&b0);
    }

	public static byte[] syncSafeInt(int i) {
		byte b[]= new byte[4];
		b[0]= (byte)((i>>21) & 0x7f);
		b[1]= (byte)((i>>14) & 0x7f);
		b[2]= (byte)((i>>7) & 0x7f);
		b[3]= (byte)((i) & 0x7f);
		return b;
	}    

	public static byte[] readTagBytes(RandomAccessFile raf,int bytes,boolean unsync) throws IOException
    {
        if(unsync) {
            return Id3v2Tag.readDeUnsynchronizedBytes(raf, bytes);
        }
        byte[] b=new byte[bytes];
        MediaFileUtil.read_sure(raf, b);
        return b;
    }

    public static Integer readSyncSafeInt(RandomAccessFile f) throws IOException {
        byte[] buf=new byte[4];
        MediaFileUtil.read_sure(f,buf);
        return convertSyncSafeBytes_to_Int(buf);
    }
    /**
     * reads len bytes of de-unsynchronized data from an unsynchronized 
     * portion of a file, positioning the file pointer after the 
     * de-unsynchronized data that was read
     * 
     * @param f open file
     * @return integer value read
     */
    public static byte[] readDeUnsynchronizedBytes(RandomAccessFile f, int len) throws IOException {
        byte buf[]=new byte[len];
        int bp=0;
        int next_byte=-1; 
        while(bp<len) { 
            if(next_byte==-1) {
                next_byte=MediaFileUtil.read_sure(f);
            }
            buf[bp]=(byte)next_byte;
            bp++;
            if(next_byte==0xFF) { // possible unsynchronization
                next_byte=MediaFileUtil.read_sure(f);
                if(next_byte==0x00) { // found unsynchronization marker
                    next_byte=-1;   // eat the 0x00 and continue
                }
                // else leave the 2nd byte in last_byte and it'll be appended
                // (or we'll back up file pointer after loop done, below)
            } else {
                next_byte=-1; // read another character next time round
            }
        }
        if(next_byte!=-1) {
            // stopped right after a non-false-sync 0xFF, back up 1 byte
            f.seek(f.getFilePointer()-1);
        }
        return buf;
    }

    /**
     * performs the Id3v2 de-unsynchronization process on a block of data.
     * 
     * on large data where the de-unsynchronized size is known, use readDeUnsynchronizedBytes 
     * instead so that you only need to allocate one buffer rather than two or three.
     * 
     * @param tag
     * @return
     */
    public static byte[] deUnsynchronize(byte[] d) {
        byte[] buf=new byte[d.length];
        int dix=0;
        int bix=0;
        while((dix+1)<d.length) {
            buf[bix]=d[dix];
            bix++;
            if(d[dix] == -1 && d[dix+1]==0)
                dix++; // skip extra 0x00 inserted for Unsynchronization
            dix++;
        }
        if(dix<d.length) {
            buf[bix]=d[dix];
            bix++;
            dix++;
        }
        if(bix!=dix) {
            buf=Arrays.copyOf(buf,bix);
        }
        return buf;
    }

    private boolean loadFrames(RandomAccessFile raf) throws IOException {
        FrameNode newFrame=null;
        while(raf.getFilePointer()<(originalHeader.getTagOffset()+10+originalHeader.getTagSize())) {
            String frameId=Id3v2Frame.readFrameId(raf, this);
            if(frameId!=null) {
                int frameSize=Id3v2Frame.readFrameSize(raf, this);

                Registry.Id3v2FrameConfig fc=Registry.getId3v2FrameConfig(frameId,
                        originalHeader.getVersion());
                
                if(fc!=null) {
                    newFrame=loadSupportedFrame(fc,frameId,frameSize,raf);
                } else if(frameId.startsWith("T") 
                        && !(frameId.equals("TXXX")) 
                        && !(frameId.equals("TXX"))) {
                	// special frames TXXX/TXX are not regular text frames
                	// TODO: support?
                    newFrame=new Id3v2TextFrame(MediaProperty.ID3V2TEXTFRAME,this);
                    newFrame.load(frameId, frameSize,raf);
                } else if(frameId.startsWith("W") 
                        && !(frameId.equals("WXXX"))
                        && !(frameId.equals("WXX"))) {
                	// special frames WXXX/WXX are not regular URL frames
                    newFrame=new Id3v2URLFrame(MediaProperty.ID3V2URLFRAME,this);
                    newFrame.load(frameId, frameSize,raf);
                } else if(frameId.length() == 3) {
                    newFrame=new Id3v2RawFrame(MediaProperty.ID3V22UNKNOWNFRAME,this);
                    newFrame.load(frameId, frameSize,raf);
                } else {
                    newFrame=new Id3v2RawFrame(MediaProperty.ID3V2UNKNOWNFRAME,this);
                    newFrame.load(frameId, frameSize,raf);
                }
                // TODO: need to be able to ignore frames
                // when loading produces no useful info
            } else {
                break;// TODO: handle padding measurement
            }
        }
        // TODO: fix this. we need to not rely on the last frame load status to decide success of this task
        return newFrame!=null;
    }

    private FrameNode loadSupportedFrame(Registry.Id3v2FrameConfig fc, 
            String frameId, int frameSize, RandomAccessFile openFile) throws IOException {
        FrameNode newFrame=null;
        try {
            Constructor<? extends FrameNode> cons = fc.frameClass.getConstructor(MediaProperty.class,MetaTree.class);
            if (cons != null) {
                newFrame = (FrameNode) cons.newInstance(fc.frameProperty,this);
            }
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (SecurityException ex) {
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (InstantiationException ex) {
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (IllegalAccessException ex) {
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (IllegalArgumentException ex) {
             ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        } catch (InvocationTargetException ex) {
            ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
        }
        if(newFrame.load(frameId, frameSize,openFile)==null)
            return null;
        
        return newFrame;
    }
    public String convertId3v2FrameID(byte[] d) {
        String id;
        try {
             id = new String(d, "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("platform doesn't support ISO-8859-1, abort");
        }
        for (int ix = 0; ix < id.length(); ix++) {
            if (!Character.isUpperCase(id.charAt(ix)) && !Character.isDigit(id.charAt(ix))) {
            	log(Level.WARNING,"invalid character "+(id.charAt(ix)==0?"\\0":id.charAt(ix))+" in Id3v2 frameID = " + id);
            }
        }
        return id;
    }

	@Override
	public byte[] generate(int targetLength,int preferredPadding) {
		// create frames first
		UnsynchronizingByteBuffer frames=new UnsynchronizingByteBuffer(targetLength-10);
		Iterator<MetaTree<MediaProperty>> children = this.getChildren();
		int length;
		while(children.hasNext()) {
			MetaTree<MediaProperty> c=children.next();
			if(Id3v2Frame.class.isInstance(c)) {
				length=frames.getLength();
				try {
					((Id3v2Frame)c).generateFullFrame(frames);
				} catch (FileNotFoundException e) {
					log(Level.SEVERE,"File not found (reading) when saving tag. Skip frame and continue.",e);
					frames.setLength(length);
				} catch (IOException e) {
					log(Level.SEVERE,"IO error (reading) when saving tag. Skip frame and continue.",e);
					frames.setLength(length);
				}
			}
		}
		
		UnsynchronizingByteBuffer tag=new UnsynchronizingByteBuffer(targetLength)
					.enableUnsynchronizing(true);
		
		int footerLength = (this.originalHeader.footer_present ? 10:0) ;
		if(tag.getLength()+footerLength > targetLength) {
			// if we have to grow the buffer past the target length, add preferred padding
			targetLength = tag.getLength() + footerLength + preferredPadding;
		}// else just use whatever space we have
		// TODO: decide if we should change tag size in order to *reduce* padding
		preferredPadding = targetLength - tag.getLength() - footerLength;

		if(preferredPadding<0)
			throw new RuntimeException("padding size < 0.  simple math FAIL");

		// rewrite header with actual size
		tag.put(this.originalHeader.generateHeaderAndExt(frames.getLength(),preferredPadding,this));

		tag.put(frames);

		// padding
		tag.setLength(targetLength-footerLength);
		
		// write footer if any
		tag.put(this.originalHeader.generateFooter(tag.getLength(), this));
		
		return tag.compact().getBuffer();
	}
}
