package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

public class Id3v2FrameContents {
    private String frameID; // e.g. "TIT2" or "TT2"
    private byte[] rawFrameData;
    private Id3v2FrameFlags flags;

    public Id3v2FrameContents(String frameID, byte[] rawFrameData, Id3v2FrameFlags flags) {
        this.frameID = frameID;
        this.rawFrameData = rawFrameData;
        this.flags = flags;
    }

    public String getFrameID() {
        return frameID;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Id3v2FrameContents other = (Id3v2FrameContents) obj;
        if (this.frameID != other.frameID && (this.frameID == null || !this.frameID.equals(other.frameID))) {
            return false;
        }
        if (this.rawFrameData != other.rawFrameData && (this.rawFrameData == null || !this.rawFrameData.equals(other.rawFrameData))) {
            return false;
        }
        if (this.flags != other.flags && (this.flags == null || !this.flags.equals(other.flags))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.frameID != null ? this.frameID.hashCode() : 0);
        hash = 59 * hash + (this.rawFrameData != null ? this.rawFrameData.hashCode() : 0);
        hash = 59 * hash + (this.flags != null ? this.flags.hashCode() : 0);
        return hash;
    }

}
