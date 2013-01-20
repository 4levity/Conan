package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


public class UnsynchronizingByteBuffer extends AutoAllocatingByteBuffer {
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
	public AutoAllocatingByteBuffer put(byte b) {
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
	public AutoAllocatingByteBuffer put(byte[] bs,int start,int end) {
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
