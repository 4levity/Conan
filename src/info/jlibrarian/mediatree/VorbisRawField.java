package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.FileNotFoundException;
import java.io.IOException;

public class VorbisRawField extends VorbisField {

    public VorbisRawField(MediaProperty property, MetaTree<MediaProperty> parent) {
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
    public boolean isValueValid() {
        return true;
    }

    @Override
    public String toString() {
        return "raw frame, "+this.dataLength+" bytes";
    }

}
