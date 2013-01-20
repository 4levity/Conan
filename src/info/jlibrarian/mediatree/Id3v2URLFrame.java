package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;


public class Id3v2URLFrame extends Id3v2Frame {
    URL url=null;
    public Id3v2URLFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLength, RandomAccessFile openFile) throws IOException {
        byte[] frameData=super.load_from_current_position(frameId, dataLength, openFile);
        if(frameData==null)
            return null;
                
        String originalString = this.encode(0, frameData);
        if (originalString.indexOf(0) >= 0) {
        	originalString=originalString.substring(0, originalString.indexOf(0));
            log(Level.WARNING,"discarded extra characters in URL frame");
        }
        if(originalString.indexOf(':')<0 && 
                (originalString.startsWith("www.") || originalString.startsWith("WWW."))) {
            originalString = "http://"+originalString;
        }
        URL u=null;
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
        //TODO: throw away original frame data
        setValue(u);
        return frameData;
    }

    @Override
    public Object getValue() {
        if(!this.getProperty().getDataType().isAssignableFrom(URL.class))
            return null;
        return (Object)url;

    }

    @Override
    public void setValue(Object o) {
        if(!this.getProperty().getDataType().isAssignableFrom(URL.class))
            return;        
        if(!URL.class.isAssignableFrom(o.getClass())) 
            return;
        url=(URL)o;
    }
}
