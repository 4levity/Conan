package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.metatree.MetaTree;

import java.util.logging.Level;


public class Id3v2TagRestrictions {
    public int max_tag_size;
    public short max_frames;
    public boolean encoding_only_ISO8859_1_or_UTF8;
    public boolean images_only_PNG_or_JPEG;
    public int image_max_dimensions;
    public int max_text_field_length;

    // "...64x64 pixels, unless required otherwise" - not a restriction?
    public boolean images_should_be_64x64;
    
    public Id3v2TagRestrictions(int restrictions) {
        switch(restrictions >> 6) {
            case 0:
                max_tag_size=1048576;
                max_frames=128;
                break;
            case 1:
                max_tag_size=131072;
                max_frames=64;
                break;
            case 2:
                max_tag_size=40960;
                max_frames=32;
                break;
            case 3:
                max_tag_size=4096;
                max_frames=32;
        }
        encoding_only_ISO8859_1_or_UTF8=((restrictions & 0x20)!=0);
        switch((restrictions >> 3) & 0x03) {
            case 0: max_text_field_length=Integer.MAX_VALUE; break;
            case 1: max_text_field_length=1024; break;
            case 2: max_text_field_length=128; break;
            case 3: max_text_field_length=30;
        }
        images_only_PNG_or_JPEG=((restrictions & 0x04)!=0);
        switch(restrictions & 0x03) {
            case 0:
                image_max_dimensions=Integer.MAX_VALUE;
                images_should_be_64x64=false;
                break;
            case 1: 
                image_max_dimensions=256; 
                images_should_be_64x64=false;
                break;
            case 2: 
                image_max_dimensions=64; 
                images_should_be_64x64=false;
                break;
            case 3: 
                image_max_dimensions=Integer.MAX_VALUE;
                images_should_be_64x64=true;
        }
    }
    
    public Id3v2TagRestrictions() {
        max_tag_size=1048576;
        max_frames=128;
        encoding_only_ISO8859_1_or_UTF8=false;
        max_text_field_length=Integer.MAX_VALUE;
        images_only_PNG_or_JPEG=false;
        image_max_dimensions=Integer.MAX_VALUE;
        images_should_be_64x64=false;
    }
    
    public int getBitmap(MetaTree<?> owner) {
        int bm;
        if(max_tag_size==1048576 && max_frames==128)
            bm=0;
        else if(max_tag_size==131072 && max_frames==64)
            bm=1<<6;
        else if(max_tag_size==40960 && max_frames==32)
            bm=2<<6;
        else if(max_tag_size==4096 && max_frames==32)
            bm=3<<6;
        else {
        	bm=0;
        	if(owner!=null)
	        	owner.log(Level.WARNING,"unrecordable id3v2 tag size restrictions (max size="
	        				+max_tag_size+", max frames="+max_frames+") - using 1048576, 128");
        }

        if(encoding_only_ISO8859_1_or_UTF8)
            bm|=0x20;
        if(max_text_field_length==1024)
            bm|=1<<3;
        else if(max_text_field_length==128)
            bm|=2<<3;
        else if(max_text_field_length==30)
            bm|=3<<3;
        else if(max_text_field_length!=Integer.MAX_VALUE) {
        	if(owner!=null)
	        	owner.log(Level.WARNING,"unrecordable id3v2 tag size restrictions (max text field length="
	    				+max_text_field_length+")");
        }
        if(images_only_PNG_or_JPEG)
            bm|=0x04;
        if(image_max_dimensions==256 && !images_should_be_64x64)
            bm|=0x01;
        else if(image_max_dimensions==64 && !images_should_be_64x64)
            bm|=0x02;
        else if(image_max_dimensions==Integer.MAX_VALUE && images_should_be_64x64)
            bm|=0x03;
        else if(image_max_dimensions!=Integer.MAX_VALUE || images_should_be_64x64) {
        	if(owner!=null)
	        	owner.log(Level.WARNING,"unrecordable id3v2 tag size restrictions (image max dimensions="
	    				+image_max_dimensions+", 64x64="+images_should_be_64x64+")");
        }
        return bm;
    }
    
    @Override
    public String toString() {
        String desc;
        desc = "id3v2TagRestrictions = "+Integer.toBinaryString(getBitmap(null));
        return desc;
    }
}
