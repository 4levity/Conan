package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.Id3v2RawFrameContents;
import info.jlibrarian.stringutils.AutoAllocatingByteBuffer;

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
        return new Id3v2RawFrameContents(/*this.frameIdentifier, */null, this.getFlags());
    }

    @Override
    public String toString() {
        return "raw frame, "+this.dataLength+" bytes";
    }

    @Override
	protected void generateFrameData(AutoAllocatingByteBuffer bb)
			throws FileNotFoundException, IOException {
		// TODO: don't just reload frame, regnerate it, this is easy
		bb.put(this.reload());
	}
}
