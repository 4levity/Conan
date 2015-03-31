package info.jlibrarian.stringutils; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

/**
 * An automatically-resized byte array which can implement the ID3 "unsynchronization" encoding scheme.
 * Can be used to generate ID3 tags which use unsynchronization. It might not be a good idea to use it.
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public class UnsynchronizingByteBuffer extends ResizingByteBuffer {
	// TODO: consider deprecating/disabling creation of unsynchronized tags
	int numBytesPut=0;

	@Override
	public int getNumBytesPut() {
		return this.numBytesPut;
	}

	int unsynchronizingEnabledOffset = 1;

	public UnsynchronizingByteBuffer(int len) {
		super(len);
	}
	
	public UnsynchronizingByteBuffer enableUnsynchronizing(boolean en) {
		if(en)
			this.unsynchronizingEnabledOffset=index+1;
		else
			this.unsynchronizingEnabledOffset=0;
		return this;
	}
	public boolean isUnsynchronizingEnabled() {
		return this.unsynchronizingEnabledOffset != 0;
	}

	@Override
	public ResizingByteBuffer put(byte b) {
		this.numBytesPut ++;
		
		if(this.unsynchronizingEnabledOffset>0 && (this.index >= this.unsynchronizingEnabledOffset) && ( ((b & 0xE0) != 0) || (b == 0) )) {
			if(this.buf[index-1]==0xFF) {
				makeRoom(2);
				this.buf[index]=0x00;
				this.buf[index+1]=b;
				index+=2;
				return this;
			}
		}
		makeRoom(1);
		this.buf[index]=b;
		index++;
		return this;
	}

	@Override
	public ResizingByteBuffer put(byte[] bs,int start,int end) {
		if(bs == null)
			return this;
		if(start<0 || end>bs.length || end<start)
			throw new ArrayIndexOutOfBoundsException("AutoAllocatingByteBuffer.put : start/end out of range");
		
		makeRoom(bs.length-(end-start));
		for(int i=start;i<end;i++)
			put(bs[i]);
		return this;
	}

}
