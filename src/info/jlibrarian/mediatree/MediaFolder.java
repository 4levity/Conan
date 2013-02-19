package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.propertytree.PropertyTreeObjNode;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MediaFolder extends PropertyTreeObjNode {
    public MediaFolder() {
        super(MediaProperty.FOLDER,null);
    }

    public MediaFolder(PropertyTree parent) {
        super(MediaProperty.FOLDER,parent);
    }

    public MediaFolder(Property prop, PropertyTree parent) {
        super(prop, parent);
    }

    public MediaFolder load(String string,boolean loadSubFolders) throws IOException {
        return load(new File(string),loadSubFolders);
    }
    public MediaFolder load(String string) throws IOException {
        return load(new File(string),true);
    }
    /**
     * THROWS AWAY ALL CHILDREN SUBTREES, then loads new files.
     */
    public MediaFolder load(File loadFile,boolean loadSubFolders) throws IOException {
        dropChildren();
        setValue(loadFile);
        if(getFile().isDirectory() && getFile().exists()) {
            log(Level.INFO,"Loading folder");
            for(File f : getFile().listFiles()) {
                // TODO: configurable behavior with symlinks
                if(f.isFile()) {
                    PropertyTree newFile;
                    if (MediaFileUtil.isLink(f)) {
                    	log(Level.INFO,"Not loading symlink file: "+f.toString());
                        newFile = new PropertyTreeObjNode(MediaProperty.SYMLINKFILE,this);
                        // Just create a file object, but don't try to load it
                        newFile.setValue(f);
                    } else {
                        newFile=new MediaFile(this);
                        ((MediaFile)newFile).load(f);
                    }
                } else if(f.isDirectory()) {
                    PropertyTree newFolder;
                    if (MediaFileUtil.isLink(f)) {
                    	log(Level.INFO,"Not loading symlink folder: "+f.toString());
                        newFolder = new PropertyTreeObjNode(MediaProperty.SYMLINKFOLDER,this);
                        // Just create a folder object, but don't try to load it
                        newFolder.setValue(f);
                    } else {
                        newFolder = new MediaFolder(this);
                        if(loadSubFolders) {
                            newFolder=((MediaFolder)newFolder).load(f,true);
                        } else {
                        	log(Level.INFO,"Not loading folder: "+f.toString());
                            newFolder.setValue(f); // just set this node without loading files/subfolders
                        }
                        if(newFolder==null)
                        	log(Level.WARNING,"Failed to load subfolder: "+f.toString());
                    }
                } else {
                	log(Level.WARNING,"Can't identify File/Folder: "+f.toString());
                }
            }
            return this;
        } //else
        log(Level.WARNING,"Doesn't exist or not a directory: "+getFile().toString());
        delete();
        return null;
    }

	@Override
	public String describeNode() {
		// since this is a simple object, we can include it in the description
		return super.describeNode()+(this.getFile()==null?"":this.getFile().getName());
	}
    
}
