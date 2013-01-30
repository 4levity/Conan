package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class VorbisTextField extends VorbisField {
    String string;
    public VorbisTextField(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLen, 
    		RandomAccessFile openFile) 
            throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLen, openFile);
        if(frameData==null)
            return null;
        
        try {
            string = new String(frameData, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log(Level.SEVERE,"UTF-8 decoder not supported",ex);
            throw new RuntimeException("UTF-8 decoder not supported");
        }
        
        return frameData;
    }

    @Override
    public Object getValue() {
        return string;
    }

    @Override
    public Integer getInteger() {
        if(string==null) 
            return null;
        Integer i;
        try {
            i=new Integer(Integer.parseInt(string));
        } catch (NumberFormatException numberFormatException) {
            i=null;
            //todo:log
        }
        return i;
    }
    
}
