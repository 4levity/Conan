package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;
import info.jlibrarian.stringutils.AutoAllocatingByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2CommentFrame extends Id3v2Frame {
    byte originalEncodingType=-1;
    StringMap stringMap=null;
    
    public Id3v2CommentFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, 
    		RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
        
        if(frameData.length < 4)
        {
            log(Level.WARNING,"comment/map too small: "+frameData.length);
            originalEncodingType=0;
            stringMap=new StringMap("eng",this);
        } else {
	        originalEncodingType=frameData[0];
	        String language=null;
	        language=new String(Arrays.copyOfRange(frameData,1,4),"ISO-8859-1");
	
	        byte[] strBytes=Arrays.copyOfRange(frameData, 4, frameData.length);
	        String[] strs=this.encode(originalEncodingType,strBytes)
	                .split("\\x00");
	        stringMap=null;
	        stringMap=new StringMap(language,this);
	//        if(strs.length==0) {
	//            throw new FileFormatException("comment/map may not be empty");
	//        }
	        if(strs.length == 1) {
	            // not even gonna warn on this, just accept it.  also, now
	            // we can use this same class for the id3v2 TOU frame:
	            stringMap.put("",strs[0]);
	        } else {
	            for(int ix=0;ix<strs.length;ix+=2) {
	                if(ix+1 == strs.length) {
	                    log(Level.WARNING,"odd # of strings in comment/map: "+strs.length);
	                    stringMap.put("",strs[ix]);
	                }
	                else
	                    stringMap.put(strs[ix], strs[ix+1]);
	            }
	        }
        }
        return frameData;
    }

    @Override
    public Object getValue() {
        return stringMap;
    }

	@Override
	protected void generateFrameData(AutoAllocatingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't just reload frame, regnerate it
		bb.put(this.reload());
	}
}
