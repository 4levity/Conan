package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;


public class Id3v2URLFrame extends Id3v2Frame {
    URL url=null;
    public Id3v2URLFrame(Property property, PropertyTree parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
    	byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
    	if(frameData==null)
    		return null;

    	String originalString = this.encode(0, frameData);
    	URL u=null;
    	if(originalString.length()>0) {
    		if(originalString.charAt(0) == 0) {
    			log(Level.WARNING,"Invalid byte 00 leading URL frame (skipping)");
    			originalString = originalString.substring(1);
    		}
    	}
    	if(originalString.length()>0) {
    		if (originalString.indexOf(0) >= 0) {
    			originalString=originalString.substring(0, originalString.indexOf(0));
    			log(Level.WARNING,"discarded extra characters in URL frame");
    		}
    		if(originalString.indexOf(':')<0 && 
    				(originalString.startsWith("www.") || originalString.startsWith("WWW."))) {
    			originalString = "http://"+originalString;
    		}

    		try {
    			u = new URL(originalString);
    		} catch (MalformedURLException ex) {
    			log(Level.WARNING,"Malformed URL in a frame, will convert to search URL: "+originalString);
    		}
    		if(u==null) {
    			try {
    				u = new URL("http://www.google.com/search?q="+URLEncoder.encode(originalString, "US-ASCII"));
    			} catch (MalformedURLException ex) {
    				log(Level.SEVERE,"FAIL correcting malformed URL: "+originalString);
    				throw new RuntimeException("FAIL correcting malformed URL "+originalString,ex);
    			}
    		}
    	}
    	setValue(u);
    	return frameData;
    }

    @Override
    public Object getValue() {
        if(!this.getNodeProperty().getDataType().isAssignableFrom(URL.class))
            return null;
        return (Object)url;

    }

    @Override
    public void setValue(Object o) {
        if(!this.getNodeProperty().getDataType().isAssignableFrom(URL.class))
            return;        
        if(!URL.class.isAssignableFrom(o.getClass())) 
            return;
        url=(URL)o;
    }

	@Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't just reload frame, regenerate it
		bb.put(this.reload());
	}
}
