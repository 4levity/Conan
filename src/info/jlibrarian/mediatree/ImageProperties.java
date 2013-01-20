package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.swing.ImageIcon;

public class ImageProperties extends FileProperties implements ImageObserver {
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

    public ImageProperties(PayloadContainer source, String mimeType, String description, String filename,
            byte picType,byte[] imageData) {
        super(source, mimeType, description, filename, imageData);
        this.pictureType = picType;
        this.scanImage(imageData);
    }

    public ImageProperties(PayloadContainer source, String mimeType, String description,
            byte picType,byte[] imageData) {
        super(source, mimeType, description, null, imageData);
        this.pictureType = picType;
        this.scanImage(imageData);
    }

    public ImageProperties(FrameNode sourceNode, String mimeType, String description,
            byte picType,byte[] imageData,
            int h,int w,int bpp,int indexedColors) {
        super(sourceNode, mimeType, description, null, imageData);
        this.pictureType = picType;

        // TODO: examine image and validate h/w/bpp/ix if image type supported        
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
        // todo: get geometry from img
        if(!this.scanFileData(imageData))
            return false;
        
        if(isImageMimeType()) {
            Image img=new ImageIcon(imageData).getImage();
            if(img!=null) {
                this.imgHeight=img.getHeight(this);
                this.imgWidth=img.getWidth(this);
//                img.getProperty(mimeType, arg1)
                if(this.imgHeight>0)
                    return true;
            }
            //else todo: log
        }
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
}
