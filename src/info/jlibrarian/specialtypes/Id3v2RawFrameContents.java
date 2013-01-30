package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.Arrays;

public class Id3v2RawFrameContents {
	// does not actually contain the FrameID; that is stored in the FrameNode
	// just the frame flags and data
    private byte[] rawFrameData;
    private Id3v2FrameFlags flags;

    public Id3v2RawFrameContents(byte[] rawFrameData, Id3v2FrameFlags flags) {
        this.rawFrameData = rawFrameData;
        this.flags = flags;
    }

    public Id3v2FrameFlags getFlags() {
        if(flags==null)
            return Id3v2FrameFlags.defaultFlags;
        return flags;
    }

    public byte[] getRawFrameData() {
        return rawFrameData;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
		result = prime * result + Arrays.hashCode(rawFrameData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Id3v2RawFrameContents other = (Id3v2RawFrameContents) obj;
		if (flags == null) {
			if (other.flags != null)
				return false;
		} else if (!flags.equals(other.flags))
			return false;
		if (!Arrays.equals(rawFrameData, other.rawFrameData))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Id3v2RawFrameContents ["+(rawFrameData==null?"null]":rawFrameData.length+" bytes]");
	}

}
