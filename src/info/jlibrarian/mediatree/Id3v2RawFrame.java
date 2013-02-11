package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.stringutils.ResizingByteBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * Unsupported/unknown ID3V2 frames are represented by this type of node.
 */

public class Id3v2RawFrame extends Id3v2Frame {
    public Id3v2RawFrame(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }
    @Override
    public Object getValue() {
    	// TODO: return ResizingByteBuffer with frame payload;
    	return null;
    }

    @Override
	protected void generateFrameData(ResizingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't just reload frame, regnerate it, this is easy
		bb.put(this.reload());
	}
}
