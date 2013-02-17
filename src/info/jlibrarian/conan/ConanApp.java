package info.jlibrarian.conan;

import info.jlibrarian.mediatree.MediaFileUtil;
import info.jlibrarian.mediatree.MediaFolder;
import info.jlibrarian.mediatree.MediaProperty;
import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertySearchResults;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class ConanApp {
    public static void main(String[] args) {
    	ArrayList<File> targets=new ArrayList<File>();
    	ArrayList<String> queryStrings=new ArrayList<String>();
    	ArrayList<Property> queryProperties=new ArrayList<Property>();
    	Level logLevel=Level.WARNING;
    	int maxResults=-1;
    	boolean showNodes=false;
    	
    	if(args.length==0) {
    		printUsage();
    	} else {
    		int arg=0;
    		while(arg<args.length) {
    			if(args[arg].equalsIgnoreCase("--query")) {
    				if(arg+1<args.length) {
        				arg++;
        				queryStrings.add(args[arg]);
    				}
    			} else if(args[arg].equalsIgnoreCase("--log")) {
    				if(arg+1<args.length) {
	    				arg++;
	    				logLevel=Level.parse(args[arg]);
	    				System.out.println("** Setting log level to "+logLevel.getLocalizedName());
    				}
    			} else if(args[arg].equalsIgnoreCase("--maxresults")) {
    				if(arg+1<args.length) {
	    				arg++;
	    				try {
							maxResults=Integer.parseInt(args[arg]);
						} catch (NumberFormatException e) {
							printUsage();
							return;
						}
	    				System.out.println("** Setting maxresults to "+maxResults);
    				}
    			} else if(args[arg].equalsIgnoreCase("--shownodes")) {
    				showNodes=true;
    			} else {
    				targets.add(new File(args[arg]));
    			}
    			arg++;
    		}
    	}
    	
    	PropertyTree.setLogLevel(logLevel);
    	
    	for(File f : targets) {
    		PropertyTree root;
    		if(f.isDirectory()) {
    			root=null;
    			try {
					root=new MediaFolder().load(f, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			if(root!=null) {
    				System.out.println("** Loaded all from "+f);
    				for(String pStr : queryStrings) {
    					Property p=MediaProperty.getPropertyByName(pStr);
    					if(p!=null) {
    						queryProperties.add(p);
            				System.out.println("** Query property: "+queryProperties.get(queryProperties.size()-1));
    					} else {
    						System.out.println("** Unknown property: "+pStr);
    						printUsage();
    						return;
    					}
    				}

    				
    				
    				Map<Property, PropertySearchResults> queryResults;
    				if(queryProperties.size()>0) {
    					queryResults=root.query(queryProperties);
    				} else {
    					queryResults=root.query();
    				}

    				if(queryResults!=null) {
        				Set<Entry<Property, PropertySearchResults>> entries=queryResults.entrySet();
        				if(entries!=null) {
            				for(Entry<Property, PropertySearchResults> entry : entries) {
            					if(maxResults<0) {
                					System.out.println(
                							StringUtils.lineWrap(
                									StringUtils.stripControlCharacters(entry.getValue().formattedResults(showNodes,-1))
                							,80)
                									);
            					} else if(maxResults==0) {
            						System.out.println(entry.getKey()+"("+entry.getKey().getName()+"): "+entry.getValue().getNumResults()
            								+" results in "+entry.getValue().getTotalNodes()+" total nodes");
            					} else {
            						// TODO: limit results
                					System.out.println(
                							StringUtils.lineWrap(
                									StringUtils.stripControlCharacters(entry.getValue().formattedResults(showNodes,maxResults))
                							,80)
                									);
            					}
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
		System.out.println("usage: java -jar conan.jar [options] folder1 [folder2 [folder3...]]");
		System.out.println("");
		System.out.println("options:");
		System.out.println("  --query PropertyNAME");
		System.out.println("       query this property on each folder after loading (default is to query ");
		System.out.println("       all properties). can be repeated to query a list of properties.");
		System.out.println("");
		System.out.println("  --log LEVEL");
		System.out.println("       set the logging verbosity. LEVEL can be INFO (show everything), WARNING,");
		System.out.println("       or SEVERE (only show major issues).");
		System.out.println("");
		
/*		System.out.println("list of properties:");
 		for(MediaProperty m : MediaProperty.values()) {
			System.out.println(m.getShortName()+ " - " + m.getDescription());
		}*/
		System.out.println(MediaFileUtil.supportReport(true));		
	}
}

// TODO: javadoc this whole project