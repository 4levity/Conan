package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;
import info.jlibrarian.metatree.MetaTreeWithObj;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class MediaTag extends MetaTreeWithObj<MediaProperty> {
    public MediaTag(MediaProperty prop, MetaTree<MediaProperty> parent) {
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
