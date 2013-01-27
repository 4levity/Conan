package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.propertytree.PropertyTreeObjNode;

import java.io.IOException;
import java.io.File;


public class ImageFile extends MediaFile {

    public ImageFile(MediaProperty prop, PropertyTree<MediaProperty> parent) {
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
        ImageLink imgProp=null;
        imgProp = new ImageLink(this, mimeType, "", getFile().getName(), 
                ImageLink.defaultPictureType, this.getPayload());
        
        PropertyTreeObjNode<MediaProperty> newChild=new PropertyTreeObjNode<MediaProperty>(MediaProperty.PICTURE,this);
        newChild.setValue(imgProp);

        return this;
    }
}
