package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.zip.CRC32;

import javax.swing.ImageIcon;

public class ImageLink implements ImageObserver {
    private static String[] pictureTypes=new String[] {
        "Other",
        "32x32 pixels 'file icon' (PNG only)",
        "Other file icon",
        "Cover (front)",
        "Cover (back)",
        "Leaflet page",
        "Media (e.g. label side of CD)",
        "Lead artist/lead performer/soloist",
        "Artist/performer",
        "Conductor",
        "Band/Orchestra",
        "Composer",
        "Lyricist/text writer",
        "Recording Location",
        "During recording",
        "During performance",
        "Movie/video screen capture",
        "A bright coloured fish",
        "Illustration",
        "Band/artist logotype",
        "Publisher/Studio logotype"
    };
    public static byte defaultPictureType=3;
    
    protected byte pictureType=defaultPictureType; // default: front cover
    protected int imgHeight=-1;
    protected int imgWidth=-1;
    protected int imgBitsPerPixel=-1;
    protected int imgIndexedColors=-1;
    
	private PayloadContainer source;
	protected String mimeType;
	protected String description;
	protected String filename;
	protected int fileSize;
	protected CRC32 crc32;

    public ImageLink(PayloadContainer source, String mimeType, String description, String filename,
            byte picType,byte[] imageData) {
        this.source = source;
        this.mimeType = mimeType;
        this.description = description;
        this.filename = filename;
        fileSize=-1;
        crc32=null;
        this.pictureType = picType;
        this.scanImage(imageData);
    }

    public ImageLink(PayloadContainer source, String mimeType, String description,
            byte picType,byte[] imageData) {
        this.source = source;
        this.mimeType = mimeType;
        this.description = description;
        this.filename = null;
        fileSize=-1;
        crc32=null;
        
        this.pictureType = picType;
        this.scanImage(imageData);
    }

    public ImageLink(FrameNode sourceNode, String mimeType, String description,
            byte picType,byte[] imageData,
            int h,int w,int bpp,int indexedColors) {
        this.source = sourceNode;
        this.mimeType = mimeType;
        this.description = description;
        this.filename = null;
        fileSize=-1;
        crc32=null;
        
        this.pictureType = picType;

        this.imgHeight = h;
        this.imgWidth = w;
        this.imgBitsPerPixel = bpp;
        this.imgIndexedColors = indexedColors;

        this.scanImage(imageData);
    }

    @Override
    public String toString() {
        return getPictureTypeString() +" " + super.toString() 
                + (imgHeight>0 && imgWidth>0 ? " {"+imgHeight+"x"+imgWidth+"}":" {?x?}");
    }
    
    public String getPictureTypeString() {
        if(pictureType>=0 && pictureType<pictureTypes.length)
            return(pictureTypes[pictureType]);
        //else 
        return "Invalid type";
    }
    
/*    private void setImageGeometry(int h,int w,int bpp,int numIndexedColors) {
        this.imgHeight = h;
        this.imgWidth = w;
        this.imgBitsPerPixel = bpp;
        this.imgIndexedColors = numIndexedColors;
    }*/
    
    private void resetImageGeometry() {
        this.imgHeight = -1;
        this.imgWidth = -1;
        this.imgBitsPerPixel = -1;
        this.imgIndexedColors = -1;
    }

    protected boolean scanImage(byte[] imageData) {
        if(!this.scanFileData(imageData))
            return false;
        
        if(isImageMimeType()) {
            Image img=new ImageIcon(imageData).getImage();
            if(img!=null) {
                // TODO: validate h/w/bpp/ix         
                this.imgHeight=img.getHeight(this);
                this.imgWidth=img.getWidth(this);
//                img.getProperty(mimeType, arg1)
                if(this.imgHeight>0)
                    return true;
            }
            //todo: else log
        }
        // todo: support URL MIME type "-->"
        resetImageGeometry();
        return false;
    }

    public int getimgBitsPerPixel() {
        return imgBitsPerPixel;
    }

    public int getimgHeight() {
        return imgHeight;
    }

    public int getimgIndexedColors() {
        return imgIndexedColors;
    }

    public byte getPictureType() {
        return pictureType;
    }

    public int getimgWidth() {
        return imgWidth;
    }

    private boolean isImageMimeType() {
        return(getMimeType().equals("image/jpeg")
                || getMimeType().equals("image/jpg")
                || getMimeType().equals("image/png")
                || getMimeType().equals("image/gif")
                || getMimeType().equals("JPG")
                || getMimeType().equals("PNG")
                || getMimeType().equals("GIF")
                );
    }

    public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
        if((flags & HEIGHT)!=0) {
            this.imgHeight=height;
        }
        if((flags & WIDTH)!=0) {
            this.imgWidth=width;
        }
        return(this.imgHeight<0 || this.imgWidth<0 || this.imgBitsPerPixel <0
                || this.imgIndexedColors <0);
    }
    
    public ImageIcon getImageIcon(int size) throws IOException {
        byte[] data = this.getFileData();

/*
        FileOutputStream fs;
        try {
            fs = new FileOutputStream("/home/ivan/Music/preview.dat");
            fs.write(data);
            fs.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
*/
        if(data==null)
            return null;
        
        ImageIcon ico = new ImageIcon(data);
        int h=ico.getIconHeight();
        int w=ico.getIconWidth();
        if(size<=0)//            || (h <= size && w <= size))
            return ico;
        
        int newh,neww;
        if(h >= w) {
            newh=size;
            neww= (int) (w * (1.0 * newh / h));
        } else {
            neww=size;
            newh= (int) (h * (1.0 * neww / w));
        }
        ico.setImage(
                ico.getImage().getScaledInstance(neww, newh, Image.SCALE_SMOOTH));
        
        return ico;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crc32 == null) ? 0 : crc32.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + fileSize;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + imgBitsPerPixel;
		result = prime * result + imgHeight;
		result = prime * result + imgIndexedColors;
		result = prime * result + imgWidth;
		result = prime * result
				+ ((mimeType == null) ? 0 : mimeType.hashCode());
		result = prime * result + pictureType;
		//result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		ImageLink other = (ImageLink) obj;
		if (crc32 == null) {
			if (other.crc32 != null)
				return false;
		} else if (!crc32.equals(other.crc32))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (fileSize != other.fileSize)
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (imgBitsPerPixel != other.imgBitsPerPixel)
			return false;
		if (imgHeight != other.imgHeight)
			return false;
		if (imgIndexedColors != other.imgIndexedColors)
			return false;
		if (imgWidth != other.imgWidth)
			return false;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		if (pictureType != other.pictureType)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

}
