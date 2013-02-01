package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.FileMetadata;
import info.jlibrarian.specialtypes.Id3PictureType;
import info.jlibrarian.specialtypes.ImageAttributes;

public class FlacPicture extends FrameNode {
    FileMetadata embeddedImageLink=null;

	public FlacPicture(PropertyTree<MediaProperty> parent) {
		super(MediaProperty.PICTURE, parent);
	}

	@Override
	protected byte[] load_from_current_position(String frameId, int dataLen,
			RandomAccessFile openFile) throws IOException {
		byte[] frameData=new byte[dataLen];
		MediaFileUtil.read_sure(openFile,frameData);
		
		int offs=0;
		int pictureTypeId = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData);
		offs+=4;
		
		int stringLength = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		String mimeType = new String(Arrays.copyOfRange(frameData, offs, offs+stringLength),"US-ASCII");
		offs+=stringLength;
		
		stringLength = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		String description = new String(Arrays.copyOfRange(frameData, offs, offs+stringLength),"UTF-8");
		offs+=stringLength;
		
		int imgWidthPx = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		int imgHeightPx = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		int imgBitsPerPixel = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		int imgNumIndexedColors = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;

		int imgDataLen = (int)MediaFileUtil.convert32bitsToUnsignedInt(frameData,offs);
		offs+=4;
		byte[] imageData = Arrays.copyOfRange(frameData, offs, offs+imgDataLen);
		
		this.embeddedImageLink = new FileMetadata(this,null,mimeType,description,
				Id3PictureType.getId3Picturetype((byte)(pictureTypeId & 0x7F)),imageData);
		ImageAttributes scannedAttrs=this.embeddedImageLink.getImageAttributes();
		
		if(imgWidthPx != scannedAttrs.getImgWidth()
				|| imgHeightPx != scannedAttrs.getImgHeight()
				|| imgBitsPerPixel != scannedAttrs.getImgBitsPerPixel()
				|| imgNumIndexedColors != scannedAttrs.getImgIndexedColors()) {
			this.log(Level.WARNING, "FLAC picture metadata disagrees with image data: ("
					+imgHeightPx+"x"+imgWidthPx+"px, "+imgBitsPerPixel+"bpp, "+imgNumIndexedColors+"ixColors) != ("
					+scannedAttrs.getImgWidth()+"x"+scannedAttrs.getImgHeight()+"px, "
						+scannedAttrs.getImgBitsPerPixel()+"bpp, "+scannedAttrs.getImgIndexedColors()+"ixColors)");
		}
		
		return frameData;
	}

	@Override
	public Object getValue() {
		return this.embeddedImageLink;
	}
	
}
