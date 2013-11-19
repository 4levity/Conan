package info.jlibrarian.conan;

import info.jlibrarian.mediatree.MediaFile;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

/**
 * This is the main app, either a command line utility or graphical editor.
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public class ConanApp {
    public static void main(String[] args) {
    	List<File> targets=new ArrayList<File>();
    	List<String> queryStrings=new ArrayList<String>();
    	Level logLevel=Level.WARNING;
    	int maxResults=-1;
    	boolean showNodes=false;
    	boolean interactiveMode=false;
    	
    	if(args.length==0) {
    		printUsage();
    	} else {
    		int arg=0;
    		while(arg<args.length) {
    			if (args[arg].equalsIgnoreCase("--ui")) {
    				interactiveMode=true;
    			} else if(args[arg].equalsIgnoreCase("--query")) {
    				if(arg+1<args.length) {
        				arg++;
        				queryStrings.add(args[arg]);
    				}
    			} else if(args[arg].equalsIgnoreCase("--listproperties")) {
    				printPropertyList();
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

    	if(interactiveMode) {
    		// GUI
    	} else {
    		searchFiles(targets,queryStrings,maxResults,showNodes);
    	} // else other command line modes
    	

		System.out.println("Exit.");
    }

    private static void printPropertyList() {
		System.out.println(MediaFileUtil.supportReport(true));		
	}

	/**
     * Search for values in files and output results to console.
     * 
     * @param targets		list of file/folder objects to search 
     * @param queryStrings	names of properties to search for
     * @param maxResults	max number of results to show per property 
     * @param showNodes		if true, show actual source nodes (e.g. id3 frame info) rather than just name/value
     */
    static void searchFiles(List<File> targets,List<String> queryStrings,int maxResults,boolean showNodes) {
    	List<Property> queryProperties=new ArrayList<Property>();

    	for(File f : targets) {
    		PropertyTree root=null;
    		if(f.isDirectory()) {
    			try {
					root=new MediaFolder().load(f, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		} else {
    			try {
					root=new MediaFile().load(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
    		} else {
        		System.out.println("loading file not supported yet, try folder");
    		}
    	}
    }
    
	private static void printUsage() {
		System.out.println("usage: java -jar conan.jar [options] folder1 [folder2 [folder3...]]");
		System.out.println("");
		System.out.println("options:");
		System.out.println("  --log LEVEL");
		System.out.println("       set the logging verbosity. LEVEL can be INFO (show everything), WARNING,");
		System.out.println("       or SEVERE (only show major issues).");
		System.out.println("");
		System.out.println("  --gui");
		System.out.println("       start graphical editor (not currently implemented)");
		System.out.println("");
		System.out.println("*** the following options have no effect in graphical mode ***");
		System.out.println("");
		System.out.println("  --query PropertyNAME");
		System.out.println("       query this property on each folder after loading (default is to query ");
		System.out.println("       all properties). can be repeated to query a list of properties.");
		System.out.println("");
		System.out.println("  --listproperties");
		System.out.println("       print entire list of supported properties");
		System.out.println("");
		System.out.println("  --maxresults N");
		System.out.println("       set the maximum number of results to return PER PROPERTY");
		System.out.println("       * has no effect in graphical mode");
		System.out.println("");
		System.out.println("  --shownodes");
		System.out.println("       show the source nodes (e.g. ID3 frames), not just property/value pairs");
		System.out.println("       * has no effect in graphical mode");
		System.out.println("");
		
/*		System.out.println("list of properties:");
 		for(MediaProperty m : MediaProperty.values()) {
			System.out.println(m.getShortName()+ " - " + m.getDescription());
		}*/
	}
}

// TODO: javadoc this whole project