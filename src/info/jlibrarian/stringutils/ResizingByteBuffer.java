package info.jlibrarian.stringutils; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import java.util.Arrays;
public class ResizingByteBuffer {
	protected byte[] buf;
	protected int index;
	protected int initialCapacity;

	protected void _allocate(int cap) {
		initialCapacity=cap;
		buf=new byte[cap];
		index=0;
	}
	
	public ResizingByteBuffer(int cap) {
		_allocate(cap);
	}
	
	public ResizingByteBuffer() {
		_allocate(1024);
	}
	
	public ResizingByteBuffer(byte[] bytes) {
		_allocate(bytes.length);
		put(bytes);
	}

	protected void _reallocate(int newCapacity) {
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
	
	public ResizingByteBuffer compact() {
		if (this.index != this.buf.length)
			this.buf = Arrays.copyOf(this.buf, this.index);
		
		return this;
	}
	
	public ResizingByteBuffer put(byte b) {
		makeRoom(1);
		this.buf[this.index]=b;
		this.index++;
		return this;
	}

	public ResizingByteBuffer put(byte[] bs,int start,int end) {
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

	public ResizingByteBuffer put(byte[] bs) {
		put(bs,0,bs.length);
		return this;
	}
	
	public ResizingByteBuffer put(ResizingByteBuffer frameData) {
		put(frameData.getBuffer(),0,frameData.getLength());
		return this;
	}

	public ResizingByteBuffer put(char c) {
		return put((byte)c);
	}

	public ResizingByteBuffer put(int i) {
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

	public ResizingByteBuffer setLength(int targetLength) {
		if(targetLength>buf.length) {
			buf = Arrays.copyOf(buf, targetLength);
		}
		this.index=targetLength;
		return this;
	}


	public ResizingByteBuffer setRange(int destination, byte[] newData) {
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

	public ResizingByteBuffer fill(int numBytes, byte b) {
		for(;numBytes>0;numBytes--) {
			this.put(b);
		}
		return this;
	}

	@Override
	public String toString() {
		String s="\"";
		if(buf.length<15) {			
			s+=StringUtils.stripControlCharacters(new String(buf))+"\"";
		} else {
			s+=StringUtils.stripControlCharacters(new String(Arrays.copyOfRange(buf, 0, 12)))
					+"...\" ("+buf.length+" bytes)";
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(buf);
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
		ResizingByteBuffer other = (ResizingByteBuffer) obj;
		if (!Arrays.equals(buf, other.buf))
			return false;
		return true;
	}
	
	
}
