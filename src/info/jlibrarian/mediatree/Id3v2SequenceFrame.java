package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2SequenceFrame extends Id3v2Frame {
    byte originalEncodingType=-1;
    SequencePosition sequencePosition=null;
    
    public Id3v2SequenceFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData = super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
        originalEncodingType=frameData[0];
        byte[] strBytes=Arrays.copyOfRange(frameData, 1, frameData.length);
        String frameString=encode(originalEncodingType, strBytes);
        int nullterm=frameString.indexOf(0);
        if(nullterm>=0) {
            log(Level.WARNING,"multiple strings or null term in "+frameId+"frame (len="+dataLength+")");
            frameString=frameString.substring(0, nullterm);
        }
        Integer pos=null;
        Integer len=null;
        String[] strs=frameString.split("\\/");
        if(strs.length>0) {
            try {
                pos=new Integer(Integer.parseInt(strs[0]));
            } catch (NumberFormatException ex) {
                log(Level.INFO,"unparseable position in "+frameId+": "+frameString);
            }        	
        }
        if(strs.length>1) {
            try {
                len=new Integer(Integer.parseInt(strs[1]));
            } catch (NumberFormatException ex) {
            	log(Level.INFO,"unparseable len in "+frameId+": "+frameString);
            }
        }
        if(strs.length>2) {
        	log(Level.INFO,"too many fields in "+frameId+": "+frameString);
        }
        //if((pos==null) && (len==null))
        //    return null; //

        sequencePosition=new SequencePosition(pos,len);
        
        return frameData;        
    }

    @Override
    public Object getValue() {
        return sequencePosition;
    }
}
