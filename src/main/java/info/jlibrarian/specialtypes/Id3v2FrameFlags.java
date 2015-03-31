package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.stringutils.VersionString;


/**
 * optional frame flag info for Id3v2.3 and later
 * 
 * note that equals/isDefault/hashcode IGNORE the frame unsynchronization setting
 * 
 * @author ivan
 */
public class Id3v2FrameFlags {
	// frame status flags
	public boolean tag_alter_preservation = false;
	public boolean file_alter_preservation = false;
	public boolean read_only = false;

	// frame format flags
	public Integer grouping_identity_byte = null; // flag adds one byte to
														// frame
	public boolean compression = false; // 2.3 implies data_length_indicator,
											// 2.4 requires
	public Integer encryption_method = null; // flag adds one byte to frame
	public boolean frame_unsynchronization = false; // 2.4+
	public boolean useDataLengthIndicator = false; // 2.4+

	// data
	public Long data_length_indicator = null; // optional 2.4+ size before
													// deunsync/compress/encrypt

	public final static Id3v2FrameFlags defaultFlags = new Id3v2FrameFlags();

	final public byte[] toBytes(String version) {
		byte b[]=new byte[2];
		if(VersionString.compareVersions(version,"2.4+")==0) {
			b[0]=(byte)
				((this.tag_alter_preservation?0x40:0)
			 	|(this.file_alter_preservation?0x20:0)
			 	|(this.read_only?0x10:0));
			b[1]=(byte)
				((this.grouping_identity_byte!=null?0x40:0)
				|(this.compression?0x08:0)
				|(this.encryption_method!=null?0x04:0)
				|(this.frame_unsynchronization?0x02:0)
				|(this.data_length_indicator!=null?0x01:0));
		} else if(VersionString.compareVersions(version,"2.3+")==0) {
			b[0]=(byte)
				((this.tag_alter_preservation?0x80:0)
				|(this.file_alter_preservation?0x40:0)
				|(this.read_only?0x20:0));
			b[1]=(byte)
				((this.compression?0x80:0)
				|(this.encryption_method!=null?0x40:0)
				|(this.grouping_identity_byte!=null?0x20:0));
		} else {
			throw new UnsupportedOperationException("attempted to generate frame flag bytes for id3tag < v2.3");
		}
		return b;
	}
	public final boolean isDefault() {
		return this.equals(defaultFlags);
	}

	public final int getExtendedHeaderSize() {
		return ((this.grouping_identity_byte == null ? 0 : 1)
				+ (this.encryption_method == null ? 0 : 1) + (this.data_length_indicator == null ? 0
				: 4));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Id3v2FrameFlags other = (Id3v2FrameFlags) obj;
		if (this.tag_alter_preservation != other.tag_alter_preservation) {
			return false;
		}
		if (this.file_alter_preservation != other.file_alter_preservation) {
			return false;
		}
		if (this.read_only != other.read_only) {
			return false;
		}
		if (this.grouping_identity_byte != other.grouping_identity_byte
				&& (this.grouping_identity_byte == null || !this.grouping_identity_byte
						.equals(other.grouping_identity_byte))) {
			return false;
		}
		if (this.compression != other.compression) {
			return false;
		}
		if (this.encryption_method != other.encryption_method
				&& (this.encryption_method == null || !this.encryption_method
						.equals(other.encryption_method))) {
			return false;
		}
		if (this.useDataLengthIndicator != other.useDataLengthIndicator) {
			return false;
		}
		if (this.data_length_indicator != other.data_length_indicator
				&& (this.data_length_indicator == null || !this.data_length_indicator
						.equals(other.data_length_indicator))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + (this.tag_alter_preservation ? 1 : 0);
		hash = 37 * hash + (this.file_alter_preservation ? 1 : 0);
		hash = 37 * hash + (this.read_only ? 1 : 0);
		hash = 37
				* hash
				+ (this.grouping_identity_byte != null ? this.grouping_identity_byte
						.hashCode()
						: 0);
		hash = 37 * hash + (this.compression ? 1 : 0);
		hash = 37
				* hash
				+ (this.encryption_method != null ? this.encryption_method
						.hashCode() : 0);
		hash = 37 * hash + (this.useDataLengthIndicator ? 1 : 0);
		hash = 37
				* hash
				+ (this.data_length_indicator != null ? this.data_length_indicator
						.hashCode()
						: 0);
		return hash;
	}
}
