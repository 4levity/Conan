package info.jlibrarian.specialtypes; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.swing.ImageIcon;

/*
 * details about an image file (optional member of FileMetadata)
 * 
 * contains h/w/bpp and id3 "picture type"
 * may also contain thumbnail/icon
 * 
 * instantiation requires passing in file data so image details can be extracted
 */
public class ImageInfo implements ImageObserver {
	private Id3PictureType pictureType=null;
    protected int imgHeight;
    protected int imgWidth;
    //protected int imgBitsPerPixel;
    //protected int imgIndexedColors;
    
    public ImageInfo(Id3PictureType id3PicType,byte[] imageData) {
    	super();
        this.pictureType = id3PicType;
        this.scanImage(imageData);
    }

    public ImageInfo(byte[] imageData) {
    	super();
        this.pictureType = Id3PictureType.getDefaultType();
        this.scanImage(imageData);
    }

    private void resetImageGeometry() {
        this.imgHeight = -1;
        this.imgWidth = -1;
        //this.imgBitsPerPixel = -1;
        //this.imgIndexedColors = -1;
    }

    protected boolean scanImage(byte[] imageData) {
        Image img=new ImageIcon(imageData).getImage();
        if(img!=null) {
            // TODO: validate h/w/bpp/ix         
            this.imgHeight=img.getHeight(this);
            this.imgWidth=img.getWidth(this);
            if(this.imgHeight>0)
                return true;
        }
        //todo: log (need to pass in log owner..)
        this.resetImageGeometry();
        return false;
    }

    public int getImgHeight() {
        return imgHeight;
    }
    /*
    public int getImgBitsPerPixel() {
        return imgBitsPerPixel;
    }
    public int getImgIndexedColors() {
        return imgIndexedColors;
    }
	*/
    public Id3PictureType getId3PictureType() {
        return this.pictureType;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    static public boolean isImageMimeType(String mimeType) {
    	if(mimeType==null) 
    		return false;
    	
        return(mimeType.equals("image/jpeg")
                || mimeType.equals("image/jpg")
                || mimeType.equals("image/png")
                || mimeType.equals("image/gif")
                || mimeType.equals("JPG")
                || mimeType.equals("PNG")
                || mimeType.equals("GIF")
                );
    }

    public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
        if((flags & HEIGHT)!=0) {
            this.imgHeight=height;
        }
        if((flags & WIDTH)!=0) {
            this.imgWidth=width;
        }
        return(this.imgHeight<0 || this.imgWidth<0 /*|| this.imgBitsPerPixel <0
                || this.imgIndexedColors <0*/);
    }
    
    public ImageIcon getImageIcon(byte imageData[], int size) throws IOException {
        if(imageData==null)
            return null;
        
        ImageIcon ico = new ImageIcon(imageData);
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

    @Override
    public String toString() {
        return "image "+(this.pictureType==null?"":"("+this.pictureType+ ")/")
                + (imgHeight>0 && imgWidth>0 ? (""+imgHeight+"x"+imgWidth):"?x?");
        }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + imgHeight;
		result = prime * result + imgWidth;
		result = prime * result
				+ ((pictureType == null) ? 0 : pictureType.hashCode());
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
		ImageInfo other = (ImageInfo) obj;
		if (imgHeight != other.imgHeight)
			return false;
		if (imgWidth != other.imgWidth)
			return false;
		if (pictureType == null) {
			if (other.pictureType != null)
				return false;
		} else if (!pictureType.equals(other.pictureType))
			return false;
		return true;
	}
    

}
