package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.File;


public class ImageFile extends MediaFile {

    public ImageFile(MediaProperty prop, MetaTree<MediaProperty> parent) {
        super(prop, parent);
    }

    @Override
    public MediaFile load(File f) 
    		throws IOException {
        if(super.load(f)==null)
            return null;
        
        String mimeType;
        if(getExtension().equals("jpg") || getExtension().equals("jpeg")) {
            mimeType="image/jpeg";
        } else if(getExtension().equals("png")) {
            mimeType="image/png";
        } else if(getExtension().equals("gif")) {
            mimeType="image/gif";
        } else {
            // unsupported image type
            return this;
        }
        ImageProperties imgProp=null;
        imgProp = new ImageProperties(this, mimeType, "", getFile().getName(), 
                ImageProperties.defaultPictureType, this.getPayload());
        if(imgProp != null) {
            ObjectNode newChild=new ObjectNode(MediaProperty.PICTURE,this);
            newChild.setValue(imgProp);
        }
        return this;
    }
}
