package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.propertytree.PropertyTreeObjNode;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class MediaTag extends PropertyTreeObjNode<MediaProperty> {
    public MediaTag(MediaProperty prop, PropertyTree<MediaProperty> parent) {
        super(prop, parent);
    }
    public File getEnclosingFile() {
        if(getParent()==null)
            return null;
        if(MediaTag.class.isAssignableFrom(getParent().getClass()))
            return ((MediaTag)getParent()).getEnclosingFile();
        if(MediaFile.class.isAssignableFrom(getParent().getClass()))
            return ((MediaFile)getParent()).getFile();
        return null;
    }
    abstract public MediaTag load(RandomAccessFile raf,String versions) 
            throws IOException;
    abstract public byte[] generate(int targetLength,int preferredPadding);
    abstract public String getVersion();
}
