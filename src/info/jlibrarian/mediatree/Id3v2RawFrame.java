package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.FileNotFoundException;
import java.io.IOException;


public class Id3v2RawFrame extends Id3v2Frame {
    public Id3v2RawFrame(MediaProperty property, MetaTree<MediaProperty> parent) {
        super(property, parent);
    }
    @Override
    public Object getValue() {
        return new Id3v2FrameContents(this.frameIdentifier, null, this.getFlags());
    }

    @Override
    public String toString() {
        return "raw frame, "+this.dataLength+" bytes";
    }

    @Override
    public boolean isValueValid() {
        return true;
    }

}
