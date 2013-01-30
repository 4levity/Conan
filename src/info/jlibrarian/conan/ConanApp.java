package info.jlibrarian.conan;

import info.jlibrarian.mediatree.*;
import info.jlibrarian.propertytree.PropertySearchResults;
import info.jlibrarian.propertytree.PropertyTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class ConanApp {
    public static void main(String[] args) {
    	ArrayList<File> targets=new ArrayList<File>();
    	ArrayList<MediaProperty> queries=new ArrayList<MediaProperty>();
    	Level logLevel=Level.WARNING;;
    	
    	if(args.length==0) {
    		printUsage();
    	} else {
    		int arg=0;
    		while(arg<args.length) {
    			if(arg+1<args.length) {
        			if(args[arg].equalsIgnoreCase("--query")) {
        				arg++;
        				queries.add(MediaProperty.valueOf(args[arg]));
        				System.out.println("** Query property: "+queries.get(queries.size()-1));
        			} else if(args[arg].equalsIgnoreCase("--log")) {
        				arg++;
        				logLevel=Level.parse(args[arg]);
        				System.out.println("** Setting log level to "+logLevel.getLocalizedName());
        			} else {
        				targets.add(new File(args[arg]));
        			}
    			} else {
    				targets.add(new File(args[arg]));
    			}
    			arg++;
    		}
    	}
    	
    	PropertyTree.setLogLevel(logLevel);
    	
    	for(File f : targets) {
    		PropertyTree<MediaProperty> root;
    		if(f.isDirectory()) {
    			root=null;
    			try {
					root=new MediaFolder().load(f, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			if(root!=null) {
    				Map<MediaProperty, PropertySearchResults<MediaProperty>> queryResults;
    				if(queries.size()>0) {
    					queryResults=root.query(queries);
    				} else {
    					queryResults=root.query();
    				}

    				if(queryResults!=null) {
        				Set<Entry<MediaProperty, PropertySearchResults<MediaProperty>>> entries=queryResults.entrySet();
        				if(entries!=null) {
            				for(Entry<MediaProperty, PropertySearchResults<MediaProperty>> entry : entries) {
            					entry.getValue().print();
            				}
        				}
    				} else {
    					System.out.println("** no results to query");
    				}
    			}
    		} else {
        		System.out.println("loading file not supported yet, try folder");
    		}
    		
    	}

/*    	
    	// load a folder/file location
    	MediaFolder mf=new MediaFolder();
    	try {
    		//mf.load("/home/ivan/Music/The Polish Ambassador/2008 - I Found Him. Now I Must Kill Him",true);
    		//mf.load("/home/ivan/Music/The Polish Ambassador",true);
    		mf.load("/home/ivan/Music",true);
		} catch (IOException e) {
			e.printStackTrace();
		}

    	// single property search
    	PropertySearchResults<MediaProperty> propResults=mf.query(MediaProperty.PICTURE);
		propResults.print();
		propResults.getResult(0).getNode(0).describeTree();

		// all property search (flatten)
		Map<MediaProperty, PropertySearchResults<MediaProperty>> multiResults=mf.query();
		Set<Entry<MediaProperty, PropertySearchResults<MediaProperty>>> entries=multiResults.entrySet();
		for(Entry<MediaProperty, PropertySearchResults<MediaProperty>> entry : entries) {
			entry.getValue().print();
		}
		
		*/
	
		System.out.println("Exit.");
    }

	private static void printUsage() {
		// TODO Auto-generated method stub
		
	}
}
