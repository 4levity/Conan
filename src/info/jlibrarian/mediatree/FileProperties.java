package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.util.zip.CRC32;


public class FileProperties {
    private PayloadContainer source;
    protected String mimeType;
    protected String description;
    protected String filename;
    protected int fileSize;
    protected CRC32 crc32;

    public FileProperties(PayloadContainer source,String mimeType, String description, String filename,
            byte[] fileData) {
        this.source = source;
        this.mimeType = mimeType;
        this.description = description;
        this.filename = filename;
        fileSize=-1;
        crc32=null;
        
        // fileData will be null for files/objects if too large to cache at node
        // todo: scan from source?
        if(fileData != null)
        	scanFileData(fileData);
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

    @Override
    public String toString() {
        return "["+this.mimeType+", "+this.fileSize+" bytes] \""+this.description+"\"";

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getFileSize() {
        return fileSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileProperties other = (FileProperties) obj;
        if (this.mimeType != other.mimeType && (this.mimeType == null || !this.mimeType.equals(other.mimeType))) {
            return false;
        }
        if (this.description != other.description && (this.description == null || !this.description.equals(other.description))) {
            return false;
        }
        if (this.filename != other.filename && (this.filename == null || !this.filename.equals(other.filename))) {
            return false;
        }
        if (this.fileSize != other.fileSize) {
            return false;
        }
        if (this.crc32 == null && other.crc32 == null) {
            return true;
        }
        if (this.crc32 == null || other.crc32 == null) {
            return false;
        }
        if (this.crc32.getValue() != other.crc32.getValue()) {
            return false;
        }   
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.mimeType != null ? this.mimeType.hashCode() : 0);
        hash = 59 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 59 * hash + (this.filename != null ? this.filename.hashCode() : 0);
        hash = 59 * hash + this.fileSize;
        hash = 59 * hash + (this.crc32 != null ? this.crc32.hashCode() : 0);
        return hash;
    }
    
}
