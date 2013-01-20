package info.jlibrarian.mediatree; /* Original files (c) by C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.Arrays;

public class AutoAllocatingByteBuffer {
	protected byte[] buf;
	protected int index;
	private int initialCapacity;
	
	private void _allocate(int cap) {
		initialCapacity=cap;
		buf=new byte[cap];
		index=0;
	}
	
	public AutoAllocatingByteBuffer(int cap) {
		_allocate(cap);
	}
	
	public AutoAllocatingByteBuffer() {
		_allocate(1024);
	}

	private void _reallocate(int newCapacity) {
		buf = Arrays.copyOf(buf, newCapacity);
	}
	
	protected void ensureSpaceFor(int entries) {
		if(entries < buf.length)
			return;

		int newCap = entries;
		if(initialCapacity > (newCap/2)) {
			newCap += initialCapacity;
		} else {
			newCap += (newCap/2);
		}
		_reallocate(newCap);
	}
	
	protected void makeRoom(int add) {
		ensureSpaceFor(index+add);
	}
	
	public AutoAllocatingByteBuffer compact() {
		if (this.index != this.buf.length)
			this.buf = Arrays.copyOf(this.buf, this.index);
		
		return this;
	}
	
	public AutoAllocatingByteBuffer put(byte b) {
		makeRoom(1);
		this.buf[this.index]=b;
		this.index++;
		return this;
	}

	public AutoAllocatingByteBuffer put(byte[] bs,int start,int end) {
		if(bs == null)
			return this;
		if(start<0 || end>bs.length || end<start)
			throw new ArrayIndexOutOfBoundsException("AutoAllocatingByteBuffer.put : start/end out of range");
		
		makeRoom(bs.length - (end-start));
		for(int i=start;i<end;i++) {
			this.buf[this.index]=bs[i];
			this.index++;
			i++;
		}
		return this;
	}

	public AutoAllocatingByteBuffer put(byte[] bs) {
		put(bs,0,bs.length);
		return this;
	}
	
	public AutoAllocatingByteBuffer put(AutoAllocatingByteBuffer frameData) {
		put(frameData.getBuffer(),0,frameData.getLength());
		return this;
	}

	public AutoAllocatingByteBuffer put(char c) {
		return put((byte)c);
	}

	public AutoAllocatingByteBuffer put(int i) {
		return put((byte)i);
	}

	public int getCapacity() {
		return buf.length;
	}
	
	public int getLength() {
		return index;
	}
	
	public int getNumBytesPut() {
		return index;
	}
	
	public byte[] getBuffer() {
		return buf;
	}
	
	public byte[] getAll() {
		return Arrays.copyOf(buf,index);
	}
	
	public byte getAt(int ix) {
		if((ix<0)||(ix>=this.index))
			throw new IndexOutOfBoundsException("AutoAllocatingByteBuffer.get() called for invalid buffer index");
		return this.buf[ix];
	}
	
	public byte[] getRange(int from,int to) {
		if((from < 0)||(to > this.index))
			throw new IndexOutOfBoundsException("AutoAllocatingByteBuffer.get() called for invalid buffer range");
		return Arrays.copyOfRange(buf, from, to);
	}

	public AutoAllocatingByteBuffer setLength(int targetLength) {
		if(targetLength>buf.length) {
			buf = Arrays.copyOf(buf, targetLength);
		}
		this.index=targetLength;
		return this;
	}


	public AutoAllocatingByteBuffer setRange(int destination, byte[] newData) {
		if(newData == null)
			return this;
		
		ensureSpaceFor(destination + newData.length);
		for(int i=0;i<newData.length;i++) {
			buf[destination+i]=newData[i];
		}
		if(destination + newData.length > this.index) {
			// if buffer grew, move index back to end of buffer.
			this.index = destination + newData.length;
		}
		return this;
	}

	public AutoAllocatingByteBuffer fill(int numBytes, byte b) {
		for(;numBytes>0;numBytes--) {
			this.put(b);
		}
		return this;
	}
}
