package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;


public class Id3v2TextFrame extends Id3v2Frame {
    protected byte originalEncodingType=-1;
    private Object string=null;

    public Id3v2TextFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    public Object getValue() {
        return string;
    }

    @Override
    public void setValue(Object o) {
        string=convertObject(this.getProperty().getDataType(), o);
    }
    
    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
        originalEncodingType=frameData[0];
        Class<?> dataType=getProperty().getDataType();
        if(dataType.isAssignableFrom(String.class)
                || dataType.isAssignableFrom(Integer.class)
                || dataType.isAssignableFrom(Long.class) ) {
            byte[] strBytes=Arrays.copyOfRange(frameData, 1, frameData.length);
            String newValue = encode(originalEncodingType,strBytes);
            if(newValue.length()>0) {
                if(newValue.charAt(newValue.length()-1) == 0) {
                    // remove invalid null terminator
                    newValue = newValue.substring(0, newValue.length()-1);
                    log(Level.WARNING,"Removed invalid null terminator in "+this.getFrameIdentifier()+": "+newValue);
                }
            }
            setValue(newValue);
        } 
        return frameData;
    }
}
