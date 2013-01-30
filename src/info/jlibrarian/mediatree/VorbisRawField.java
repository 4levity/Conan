package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;

import java.io.FileNotFoundException;
import java.io.IOException;

public class VorbisRawField extends VorbisField {

    public VorbisRawField(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    public Object getValue() {
        Object obj;
        
        try {
            obj=reload();
        } catch (FileNotFoundException ex) {
            obj=null; ex.printStackTrace(); // todo: log
        } catch (IOException ex) {
            obj=null; ex.printStackTrace(); // todo: log
        }
        return obj;
    }

    @Override
    public String toString() {
        return "raw frame, "+this.dataLength+" bytes";
    }

}
