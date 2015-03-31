package info.jlibrarian.specialtypes;

// Metadata for ReplayGain 1.0 http://www.replaygain.org
public class ReplayGain {
	private Float gainTrack_dB;
	private Float gainAlbum_dB;
	private Double peakTrack;
	private Double peakAlbum;

	// nonstandard?
	private Float gainReference_dB;

	public ReplayGain(Float gainTrack_dB, Float gainAlbum_dB, Double peakTrack,
			Double peakAlbum, Float gainReference_dB) {
		super();
		this.gainTrack_dB = gainTrack_dB;
		this.gainAlbum_dB = gainAlbum_dB;
		this.peakTrack = peakTrack;
		this.peakAlbum = peakAlbum;
		this.gainReference_dB = gainReference_dB;
	}

	public ReplayGain(Float gainTrack_dB, Float gainAlbum_dB, Double peakTrack,
			Double peakAlbum) {
		super();
		this.gainTrack_dB = gainTrack_dB;
		this.gainAlbum_dB = gainAlbum_dB;
		this.peakTrack = peakTrack;
		this.peakAlbum = peakAlbum;
		this.gainReference_dB = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gainAlbum_dB == null) ? 0 : gainAlbum_dB.hashCode());
		result = prime
				* result
				+ ((gainReference_dB == null) ? 0 : gainReference_dB.hashCode());
		result = prime * result
				+ ((gainTrack_dB == null) ? 0 : gainTrack_dB.hashCode());
		result = prime * result
				+ ((peakAlbum == null) ? 0 : peakAlbum.hashCode());
		result = prime * result
				+ ((peakTrack == null) ? 0 : peakTrack.hashCode());
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
		ReplayGain other = (ReplayGain) obj;
		if (gainAlbum_dB == null) {
			if (other.gainAlbum_dB != null)
				return false;
		} else if (!gainAlbum_dB.equals(other.gainAlbum_dB))
			return false;
		if (gainReference_dB == null) {
			if (other.gainReference_dB != null)
				return false;
		} else if (!gainReference_dB.equals(other.gainReference_dB))
			return false;
		if (gainTrack_dB == null) {
			if (other.gainTrack_dB != null)
				return false;
		} else if (!gainTrack_dB.equals(other.gainTrack_dB))
			return false;
		if (peakAlbum == null) {
			if (other.peakAlbum != null)
				return false;
		} else if (!peakAlbum.equals(other.peakAlbum))
			return false;
		if (peakTrack == null) {
			if (other.peakTrack != null)
				return false;
		} else if (!peakTrack.equals(other.peakTrack))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[gainTrack_dB=" + gainTrack_dB + ", gainAlbum_dB="
				+ gainAlbum_dB + ", peakTrack=" + peakTrack + ", peakAlbum="
				+ peakAlbum + ", gainReference_dB=" + gainReference_dB + "]";
	}

	public Float getGainTrack_dB() {
		return gainTrack_dB;
	}

	public Float getGainAlbum_dB() {
		return gainAlbum_dB;
	}

	public Double getPeakTrack() {
		return peakTrack;
	}

	public Double getPeakAlbum() {
		return peakAlbum;
	}

	public Float getGainReference_dB() {
		return gainReference_dB;
	}
	public boolean isEmpty() {
		return (this.gainAlbum_dB==null && this.gainReference_dB==null && this.gainTrack_dB == null
				&& this.peakAlbum == null && this.peakTrack==null);
	}
}
