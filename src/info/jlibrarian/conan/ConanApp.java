package info.jlibrarian.conan;

import info.jlibrarian.mediatree.*;
import info.jlibrarian.metatree.MetadataQuery;

import java.io.FileOutputStream;
import java.io.IOException;

public class ConanApp {
    public static void main(String[] args) {

    	MediaFolder mf=new MediaFolder();
    	try {
    		mf.load("/home/ivan/Music/The Polish Ambassador/2008 - I Found Him. Now I Must Kill Him",true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	MediaFile f=(MediaFile)mf.getSingleChild(MediaProperty.AUDIOFILE);

    	if(f!=null) {    		
			MetadataQuery<MediaProperty> q;
			q=new MetadataQuery<MediaProperty>(f,MediaProperty.ARTIST);
			q.print();	
			MediaTag t=(MediaTag)f.getSingleChild(MediaProperty.ID3V23TAG);
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
