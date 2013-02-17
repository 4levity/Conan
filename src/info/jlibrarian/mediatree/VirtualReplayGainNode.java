package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.ReplayGain;
import info.jlibrarian.stringutils.StringUtils;

public class VirtualReplayGainNode extends PropertyTree {

	public VirtualReplayGainNode(Property property,
			PropertyTree parent) {
		super(property, parent);
	}

	@Override
	public void setValue(Object o) {
		throw new UnsupportedOperationException("set replaygain not yet supported");
	}

	@Override
	public Object getValue() {
		PropertyTree parent=this.getParent();
		
		Object gt=parent.queryBestResult(MediaProperty.REPLAYGAIN_TRACK_GAIN);
		Float gainTrack=StringUtils.toFloat(gt);
		
		Object ga=parent.queryBestResult(MediaProperty.REPLAYGAIN_ALBUM_GAIN);
		Float gainAlbum=StringUtils.toFloat(ga);
		
		Object rl=parent.queryBestResult(MediaProperty.REPLAYGAIN_REFERENCE_LOUDNESS);
		Float referenceLoudness=StringUtils.toFloat(rl);
		
		Object pt=parent.queryBestResult(MediaProperty.REPLAYGAIN_TRACK_PEAK);
		Double peakTrack=StringUtils.toDouble(pt);
		
		Object pa=parent.queryBestResult(MediaProperty.REPLAYGAIN_ALBUM_PEAK);
		Double peakAlbum=StringUtils.toDouble(pa);
		
		if(gainTrack==null && gainAlbum==null && referenceLoudness==null && peakTrack==null && peakAlbum==null) {
			return null;
		}
		/*
		if(gainTrack!=null && gainAlbum==null) {
			log(Level.WARNING,"incomplete ReplayGain data");
		}*/
		
		return new ReplayGain(gainTrack,gainAlbum,peakTrack,peakAlbum,referenceLoudness);
	}

	@Override
	public String toString() {
		return "{VirtualReplayGain}";
	}

}
