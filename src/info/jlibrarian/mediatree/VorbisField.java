package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.propertytree.PropertyTree;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class VorbisField extends FrameNode {
    static String readFieldId(RandomAccessFile raf, int length, VorbisCommentBlock block) throws IOException {
        StringBuilder fieldId = new StringBuilder();
        char c;
        while( (c=(char)MediaFileUtil.read_sure(raf))!= '=' ) {
            fieldId.append(c);
        }
        return new String(fieldId);
    }

    public VorbisField(MediaProperty property, PropertyTree<MediaProperty> parent) {
        super(property, parent);
    }

    @Override
    protected byte[] load_from_current_position(String frameId, int dataLen, RandomAccessFile openFile) throws IOException {
        byte[] frameData=new byte[dataLen];
        MediaFileUtil.read_sure(openFile, frameData);
        return frameData;
    }
}
