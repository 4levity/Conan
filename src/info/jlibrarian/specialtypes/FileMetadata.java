package info.jlibrarian.specialtypes;


import java.io.IOException;
import java.util.zip.CRC32;

/*
 * attribute associated with objects that are "tangible files" with data in them,
 * whether stored on a file system, encoded and embedded in a media tag, in a 
 * compressed file etc.
 * 
 * this object does not contain the file, but contains data about the file and also
 * points to a PayloadContainer where it can get access to the file data if needed.
 * 
 * contains size of file
 * may contain 32 bit CRC of file
 * may contain thumbnail image/icon (image files) or can retrieve one (tagged files)
 * may contain mime type, description, filename
 * 
 * on instantiation, mime type is supplied if known
 * - for unknown or non-image types: minimal size/filename data stored
 * - for image files: data scanned, CRC & icon created, metadata supplied/guessed
 */

public class FileMetadata {
	private PayloadContainer source;
	private String filename;
	private long fileSize;
	private CRC32 crc32;
	ImageInfo imageInfo; 

	// subclasses might do better filling these in
	protected String mimeType;
	protected String description;
		
	/* instantiate a reference to a hunk of data (file) at a node
	 * 
	 * only required parameter is "source" and all else may be null.
	 * 
	 * note if mimeType or picType indicate an image and fileData=null,
	 * 	then source.getPayload() will be called during instantiation 
	 */
	public FileMetadata(PayloadContainer source, String filename, String mimeType, String description,
			Id3PictureType picType, byte[] fileData) {
		super();
		this.source = source;
		this.filename = filename;
		this.mimeType = mimeType;
		this.description = description;

		// if file contents are supplied, store size/crc
		if(fileData != null) {
			this.scanFileData(fileData);
		}

		// if this is an image file, try to load it 
		if(ImageInfo.isImageMimeType(mimeType) || picType!=null) {
			if(fileData == null) {
				try {
					fileData = source.getPayload();
				} catch (IOException e) {
					// TODO log (get log owner?)
					fileData = null;
					this.fileSize = -1;
					this.crc32 = null;
					this.imageInfo = null;
				}
			}
			
			if(fileData != null) {
				this.scanFileData(fileData);
				this.imageInfo = new ImageInfo(picType,fileData);				
			}
		}
	}

	protected boolean scanFileData(byte[] fileData) {
		if(fileData == null) {
			return false;
		}
	    this.fileSize = fileData.length;
	    if(this.crc32==null)
	        this.crc32=new CRC32();
	    this.crc32.reset();
	    this.crc32.update(fileData);
	    return true;
	}

	public byte[] getFileData() throws IOException {
	    byte[] data = source.getPayload();
	    CRC32 check=new CRC32();
	    // todo: see how much this crc check is slowing us down
	    check.update(data);
	    if(check.getValue() != this.crc32.getValue()) {
	        throw new IOException("CRC mismatch reloading file");
	    }
	    return data;
	}

	public String getFilename() {
	    return filename;
	}

	public void setFilename(String filename) {
	    this.filename = filename;
	}

	public String getMimeType() {
	    return mimeType;
	}

	public void setMimeType(String mimeType) {
	    this.mimeType = mimeType;
	}

	public long getCrc32() {
	    return crc32.getValue();
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@Override
	public String toString() {
		return "file [" 
					+ (filename==null?"embedded":filename )+" " 
					+ (crc32==null?"":(", crc="+Long.toHexString(crc32.getValue())))+" "
					+ (imageInfo==null?"":imageInfo.toString())
					+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crc32 == null) ? 0 : crc32.hashCode());
		result = prime * result + (int) (fileSize ^ (fileSize >>> 32));
		result = prime * result
				+ ((imageInfo == null) ? 0 : imageInfo.hashCode());
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
		FileMetadata other = (FileMetadata) obj;
		if (crc32 == null) {
			if (other.crc32 != null)
				return false;
		} else if(other.crc32 == null) {
			return false;
		} else if (crc32.getValue() != other.crc32.getValue())
			return false;

		if (fileSize != other.fileSize)
			return false;
		
		if (imageInfo == null) {
			if (other.imageInfo != null)
				return false;
		} else if (!imageInfo.equals(other.imageInfo))
			return false;
		return true;
	}
	
	
}
