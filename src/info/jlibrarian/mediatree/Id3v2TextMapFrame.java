package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2TextMapFrame extends Id3v2Frame {
    protected StringMap stringMap=null;
    protected byte originalEncodingType=-1;
    public Id3v2TextMapFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    public Object getValue() {
        return stringMap;
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
        
        originalEncodingType=frameData[0];
        byte[] strBytes=Arrays.copyOfRange(frameData, 1, frameData.length);
        String fieldString=this.encode(originalEncodingType,strBytes);        
        StringMap map=new StringMap(this);
        String[] strs=fieldString.split("\\x00");
//        if(strs.length==0) {
//            throw new FileFormatException("comment/map may not be empty");
//        }
        if(strs.length == 1) { // as in id3v2 "USER" TOU frame
            map.put("",strs[0]);
        } else { 
            for(int ix=0;ix<strs.length;ix+=2) {
                if(ix+1 < strs.length) {
                	map.put(strs[ix], strs[ix+1]);
                } else {
                	map.put("", strs[ix]);
                	log(Level.WARNING,"odd # of strings in comment/map: "+fieldString);
                }
            }
        }
        stringMap=map;
        
        return frameData;
    }
}
