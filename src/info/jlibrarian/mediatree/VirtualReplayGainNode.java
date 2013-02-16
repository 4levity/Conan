package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.ReplayGain;

public class VirtualReplayGainNode extends PropertyTree {

	public VirtualReplayGainNode(Property property,
			PropertyTree parent) {
		super(property, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Object o) {
		throw new UnsupportedOperationException("set replaygain not supported");
	}

	@Override
	public Object getValue() {
		ReplayGain rg=null;
		Property parent=this.getParent().getNodeProperty();
		
		if(parent.isTypeOf(MediaProperty.ID3V2TAG)) {
			rg = getId3v2ReplayGain();
		} else if(parent.isTypeOf(MediaProperty.VORBISCOMMENTBLOCK)) {
			rg = getVorbisReplayGain();
		}
		if(rg==null)
			return null;
		return rg.isEmpty()?null:rg;
	}

	private ReplayGain getVorbisReplayGain() {
		PropertyTree parent=this.getParent();
		PropertyTree n;
		
		n=parent.queryBestResultNode(MediaProperty.VORBIS_REPLAYGAIN_TRACK_GAIN);
		Float gainTrack=(n==null?null:n.getFloat());
		n=parent.queryBestResultNode(MediaProperty.VORBIS_REPLAYGAIN_ALBUM_GAIN);
		Float gainAlbum=(n==null?null:n.getFloat());
		n=parent.queryBestResultNode(MediaProperty.VORBIS_REPLAYGAIN_REFERENCE_LOUDNESS);
		Float referenceLoudness=(n==null?null:n.getFloat());
		n=parent.queryBestResultNode(MediaProperty.VORBIS_REPLAYGAIN_TRACK_PEAK);
		Double peakTrack=(n==null?null:n.getDouble());
		n=parent.queryBestResultNode(MediaProperty.VORBIS_REPLAYGAIN_ALBUM_PEAK);
		Double peakAlbum=(n==null?null:n.getDouble());
		
		if(gainTrack==null && gainAlbum==null && referenceLoudness==null && peakTrack==null && peakAlbum==null) {
			return null;
		}
		return new ReplayGain(gainTrack,gainAlbum,peakTrack,peakAlbum,referenceLoudness);
	}

	private ReplayGain getId3v2ReplayGain() {
		Float gainTrack=null;
		Float gainAlbum=null;
		Float referenceLoudness=null;
		Double peakTrack=null;
		Double peakAlbum=null;
		// only method supported is via USERTEXT (TXXX) frames
		//Id3v2Tag parent=(Id3v2Tag) this.getParent();


		if(gainTrack==null && gainAlbum==null && referenceLoudness==null && peakTrack==null && peakAlbum==null) {
			return null;
		}
		return new ReplayGain(gainTrack,gainAlbum,peakTrack,peakAlbum,referenceLoudness);
	
	}

}
