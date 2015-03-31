package info.jlibrarian.specialtypes;

import java.util.Arrays;

/* contains info about FLAC metadata blocks and stream
 */
public class FlacHeader {
	private int sampleRateHz;
	private byte numChannels;
	private byte bitsPerSample;
	private long numSamples;
	private byte[] md5;
	private int numMetadataBlocks;
	private long frameDataStart;
	
	public FlacHeader(int sampleRateHz, byte numChannels, byte bitsPerSample,
			long numSamples,byte[] md5,int numMetadataBlocks, long frameDataStart) {
		super();
		this.sampleRateHz = sampleRateHz;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;
		this.numSamples = numSamples;
		this.numMetadataBlocks = numMetadataBlocks;
		this.frameDataStart = frameDataStart;
		
		if(md5.length != 16 ) {
			throw new RuntimeException("internal error, flac stream must have 128-bit md5");
		}
		this.md5=md5;
	}

	public int getSampleRateHz() {
		return sampleRateHz;
	}

	public byte getNumChannels() {
		return numChannels;
	}

	public byte getBitsPerSample() {
		return bitsPerSample;
	}

	public long getNumSamples() {
		return numSamples;
	}
	
	public int getSeconds() {
		return (int) (this.numSamples / this.sampleRateHz);
	}

	public byte[] getMd5() {
		return md5;
	}

	public int getNumMetadataBlocks() {
		return numMetadataBlocks;
	}

	public long getFrameDataStart() {
		return frameDataStart;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bitsPerSample;
		result = prime * result
				+ (int) (frameDataStart ^ (frameDataStart >>> 32));
		result = prime * result + Arrays.hashCode(md5);
		result = prime * result + numChannels;
		result = prime * result + numMetadataBlocks;
		result = prime * result + (int) (numSamples ^ (numSamples >>> 32));
		result = prime * result + sampleRateHz;
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
		FlacHeader other = (FlacHeader) obj;
		if (bitsPerSample != other.bitsPerSample)
			return false;
		if (frameDataStart != other.frameDataStart)
			return false;
		if (!Arrays.equals(md5, other.md5))
			return false;
		if (numChannels != other.numChannels)
			return false;
		if (numMetadataBlocks != other.numMetadataBlocks)
			return false;
		if (numSamples != other.numSamples)
			return false;
		if (sampleRateHz != other.sampleRateHz)
			return false;
		return true;
	}

	@Override
	public String toString() {
		int secs=this.getSeconds();
		int mins=secs/60;
		secs=secs%60;
		return "FLAC[" + sampleRateHz + "Hz/"
				+ numChannels + "ch/" + bitsPerSample
				+ "bit/"+mins+":"+(secs<10?"0":"")+secs+"]";
	}

	
}
