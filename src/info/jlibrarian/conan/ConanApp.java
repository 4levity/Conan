package info.jlibrarian.conan;

import info.jlibrarian.mediatree.*;
import info.jlibrarian.propertytree.PropertySearchResults;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ConanApp {
    public static void main(String[] args) {

    	MediaFolder mf=new MediaFolder();
    	try {
    		mf.load("/home/ivan/Music/The Polish Ambassador/2008 - I Found Him. Now I Must Kill Him",true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	MediaFile f=(MediaFile)mf.queryBestResultNode(MediaProperty.AUDIOFILE);

    	if(f!=null) {
    		PropertySearchResults<MediaProperty> results=f.query(MediaProperty.ARTIST);
    		results.print();
    		Map<MediaProperty, PropertySearchResults<MediaProperty>> multiResults=f.query();
    		Set<Entry<MediaProperty, PropertySearchResults<MediaProperty>>> entries=multiResults.entrySet();
    		for(Entry<MediaProperty, PropertySearchResults<MediaProperty>> entry : entries) {
    			entry.getValue().print();
    		}
    		
			MediaTag t=(MediaTag)f.queryBestResultNode(MediaProperty.ID3V23TAG);
	        FileOutputStream fs;
	        try {
	            fs = new FileOutputStream("/home/ivan/Music/preview.dat");
	            fs.write(t.generate(100000, 0));
	            fs.close();
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        }
    	} else {
    		
    		System.out.println(mf.describeTree());
    		
    	}
		
    	System.out.println("Exit.");
    }
}
