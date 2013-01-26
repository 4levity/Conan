package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;
import info.jlibrarian.metatree.MetaTreeWithObj;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class MediaFolder extends MetaTreeWithObj<MediaProperty> {
    public MediaFolder() {
        super(MediaProperty.FOLDER,null);
    }

    public MediaFolder(MetaTree<MediaProperty> parent) {
        super(MediaProperty.FOLDER,parent);
    }

    public MediaFolder(MediaProperty prop, MetaTree<MediaProperty> parent) {
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
            log(Level.INFO,"Loading folder: "+getFile().toString());
            for(File f : getFile().listFiles()) {
                // TODO: configurable behavior with symlinks
                if(f.isFile()) {
                    MediaFile newFile;
                    if (MediaFileUtil.isLink(f)) {
                    	log(Level.INFO,"Not loading symlink file: "+f.toString());
                        newFile = new MediaFile(MediaProperty.SYMLINKFILE,this);
                        // Just create a file object, but don't try to load it
                        newFile.setValue(f);
                    } else if(!addRegularFile(f)) {
                    	log(Level.INFO,"Failed to add file object to folder: "+f.toString());
                    }
                } else if(f.isDirectory()) {
                    MediaFolder newFolder;
                    if (MediaFileUtil.isLink(f)) {
                    	log(Level.INFO,"Not loading symlink folder: "+f.toString());
                        newFolder = new MediaFolder(MediaProperty.SYMLINKFOLDER,this);
                        // Just create a folder object, but don't try to load it
                        newFolder.setValue(f);
                    } else {
                        newFolder = new MediaFolder(this);
                        if(loadSubFolders)
                            newFolder=newFolder.load(f,true);
                        else {
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
        }
        log(Level.WARNING,"Doesn't exist or not a directory: "+getFile().toString());
        delete();
        return null;
    }

    private boolean addRegularFile(File f) throws IOException {
        Registry.FileType fType=Registry.getFileType(f);
        MediaFile newFile=null;
        if(fType==null) {
            newFile=new MediaFile(MediaProperty.OTHERFILE,this);
            newFile.setValue(f);
        } else {
            try {
                Constructor<? extends MediaFile> cons =
                        fType.fileClass.getDeclaredConstructor(MediaProperty.class,MetaTree.class);
                if(cons!=null) {
                    newFile=((MediaFile)cons.newInstance(fType.fileProperty,this))
                            .load(f);
                }
                
            } catch (InstantiationException ex) {
            	log(Level.SEVERE,"reflection FAIL - InstantiationException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (IllegalAccessException ex) {
            	log(Level.SEVERE,"reflection FAIL - IllegalAccessException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (IllegalArgumentException ex) {
            	log(Level.SEVERE,"reflection FAIL - IllegalArgumentException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (InvocationTargetException ex) {
            	log(Level.SEVERE,"reflection FAIL - InvocationTargetException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (NoSuchMethodException ex) {
            	log(Level.SEVERE,"reflection FAIL - NoSuchMethodException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            } catch (SecurityException ex) {
            	log(Level.SEVERE,"reflection FAIL - SecurityException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL");
            }
        }
        if(newFile!=null)
            return true;

        log(Level.WARNING,"Creating regular file node failed: "+f.toString());
        delete();
        return false;
    }
}
