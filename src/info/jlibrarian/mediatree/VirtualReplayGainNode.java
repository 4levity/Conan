package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.ReplayGain;

public class VirtualReplayGainNode extends PropertyTree<MediaProperty> {

	public VirtualReplayGainNode(MediaProperty property,
			PropertyTree<MediaProperty> parent) {
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
		MediaProperty parent=this.getParent().getNodeProperty();
		
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
		PropertyTree<MediaProperty> parent=this.getParent();
		PropertyTree<MediaProperty> n;
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
