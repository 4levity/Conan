package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.propertytree.PropertyTreeObjNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaFile extends PropertyTreeObjNode<MediaProperty> implements PayloadContainer {
    public MediaFile(MediaProperty prop,PropertyTree<MediaProperty> parent) {
        super(prop,parent);
    }
    /**
     * Subclasses could override the load() method to create metadata 
     * subtrees (other than "tags") for supported file types. 
     * 
     * @param file
     * @param opt
     * @return this object, or null
     * @throws java.io.IOException
     * @throws info.jlibrarian.mediatree.FileFormatException
     */
    public MediaFile load(File f) 
            throws IOException {
        setValue(f);
        dropChildren();
        if(!getFile().isDirectory() && getFile().exists()) {
            log(this,Level.INFO,"Loading file "+getFile().toString());
            boolean tagerror=false;
            loadTags();
            if(tagerror) {
                delete();
                return null;
            }
            return this;
        }
        log(this,Level.WARNING,"Warning: File does not exist or is a directory: "+getFile().toString());
        delete();
        return null;
    }

    /**
     * 
     * @return true if at least one tag was successfully loaded
     * @throws java.io.IOException
     * @throws info.jlibrarian.mediatree.FileFormatException
     */
    private boolean loadTags() throws IOException {
        List<Registry.FileType.TagConfig> tags=Registry.getTagsByFile(getFile());
        if(tags==null)
            return false;
        if(tags.isEmpty())
            return false; // no supported tags for this file type
                          // subclass might specify additional child creation behavior
        
        RandomAccessFile rf=null;
        try {
            rf = new RandomAccessFile(getFile(), "r");
        } catch (FileNotFoundException ex) {
            log(this,Level.WARNING,"Can't load tags, file not found: "+getFile().toString()+" ("+ex.toString()+")");
        }
        if(rf==null)
            return false;
        MediaTag tag=null;
        for(Registry.FileType.TagConfig tagConfig : tags) {
            // load every appropriate tag in the file
            try {
                Constructor<? extends MediaTag> con = tagConfig.tagClass
                        .getDeclaredConstructor(MediaProperty.class,PropertyTree.class);
                if(con!=null)
                    tag=con.newInstance(tagConfig.tagProperty,this)
                            .load(rf, tagConfig.tagVersions);
                //TODO: else close file and throw
            } catch (InstantiationException ex) {
                log(this,Level.SEVERE,"reflection FAIL - InstantiationException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            } catch (IllegalAccessException ex) {
                log(this,Level.SEVERE,"reflection FAIL - IllegalAccessException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            } catch (IllegalArgumentException ex) {
                log(this,Level.SEVERE,"reflection FAIL - IllegalArgumentException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            } catch (InvocationTargetException ex) {
                log(this,Level.SEVERE,"reflection FAIL - InvocationTargetException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            } catch (NoSuchMethodException ex) {
                log(this,Level.SEVERE,"reflection FAIL - NoSuchMethodException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            } catch (SecurityException ex) {
                log(this,Level.SEVERE,"reflection FAIL - SecurityException: "+ex.toString());
                ex.printStackTrace(); throw new RuntimeException("reflection FAIL",ex);
            }
        }
        rf.close();
        return (tag!=null);
    }

    @Override
    public byte[] getPayload() throws IOException {
        //todo: config value
        File f=getFile();
        if(f==null) {
            log(this,Level.WARNING,"getPayload() called for null file - "+this.describeNode());
            return null;
        }
        if(f.length() > 1500000) {
            log(this,Level.WARNING,"getPayload() not supported on large file: "+f.toString());
            return null;
        }
        byte buf[]= new byte[(int)(f.length())];
        FileInputStream ins = null;
        ins = new FileInputStream(f);

        try {
            ins.read(buf);
        } catch (IOException ex) {
        	ins.close();
            Logger.getLogger(MediaFile.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        
        ins.close();

        return buf;
    }    
    public String getExtension() {
        if(this.getFile()==null)
            return null;
        return MediaFileUtil.getFileExtension(this.getFile());
    }
}
