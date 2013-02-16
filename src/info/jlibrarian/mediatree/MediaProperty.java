package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.propertytree.Property;
import info.jlibrarian.specialtypes.FileMetadata;
import info.jlibrarian.specialtypes.FlacHeader;
import info.jlibrarian.specialtypes.GenreList;
import info.jlibrarian.specialtypes.Id3v2TagHeader;
import info.jlibrarian.specialtypes.ReplayGain;
import info.jlibrarian.specialtypes.SequencePosition;
import info.jlibrarian.specialtypes.StringMap;
import info.jlibrarian.specialtypes.StringMapWithLocale;
import info.jlibrarian.specialtypes.VariablePrecisionTime;
import info.jlibrarian.stringutils.ResizingByteBuffer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

public enum MediaProperty implements Property {
	// file system objects. (note: image files are stored as MediaProperty.PICTURE)
    FOLDER ("Folder",File.class),
    OTHERFILE ("Other file ",FileMetadata.class),
    AUDIOFILE ("Audio file",FileMetadata.class),
    SYMLINKFILE ("Symbolic file link",File.class),
    SYMLINKFOLDER ("Symbolic folder link",File.class),
            
// file tag/attachment objects..
    FLACMETADATA ("FLAC Metadata",FlacHeader.class,true),
    VORBISCOMMENTBLOCK ("Vorbis Comment block",String.class,true),
    ID3V2TAG ("ID3 v2 Tag",Id3v2TagHeader.class),
    ID3V22TAG ("ID3 v2.2 Tag",MediaProperty.ID3V2TAG),
    ID3V23TAG ("ID3 v2.3 Tag",MediaProperty.ID3V2TAG),
    ID3V24TAG ("ID3 v2.4 Tag",MediaProperty.ID3V2TAG),
    ID3V2XTAG ("ID3 v2.5+ Tag (untested)",MediaProperty.ID3V2TAG),

    // TODO: each picture context (e.g. "cover art" or "a brightly coloured fish") should have unique property
    PICTURE ("Picture",FileMetadata.class,true),

// general music/media properties..
    ALBUM ("Album",String.class,true),
    ALBUM_SORTORDER ("Album sort order",String.class,true),
    ALBUMARTIST ("Album artist",String.class,true),
    ALBUMARTIST_SORTORDER ("Album artist sort order",String.class,true),
    ARTIST ("Artist",String.class,true),
    ARTIST_SORTORDER ("Artist sort order",String.class,true),
    BEATSPERMINUTE ("Beats per minute (BPM)",Long.class,true),
    COMMENTS ("Comments",StringMapWithLocale.class,true),
    COMPOSER ("Composer",String.class,true),
    COMPOSER_SORTORDER ("Composer sort order",String.class,true),
    CONDUCTOR ("Conductor",String.class,true),
    CONTENTGROUP ("Content group",String.class,true),
    COPYRIGHTMESSAGE ("Copyright message",String.class,true),
    DISC_SEQUENCE ("Disc sequence",SequencePosition.class,true),
    DISCSUBTITLE ("Disc subtitle",String.class,true),
    ENCODEDBY ("Encoded by",String.class,true),
    ENCODER ("Encoder/settings",String.class,true),
    FILEOWNER ("File owner",String.class,true),
    GENRES ("Genre or genres",GenreList.class,true),
    INITIALKEY ("Initial key",String.class,true),
    INTERNETRADIOSTATION_NAME ("Internet radio station name",String.class,true),
    INTERNETRADIOSTATION_OWNER ("Internet radio station owner",String.class,true),
    INVOLVEDPEOPLE ("Involved People",StringMap.class,true),
    LENGTH_MS ("Length (ms)",Long.class,true),
    LYRICIST ("Lyricist",String.class,true),
    LYRICS ("Lyrics",StringMapWithLocale.class,true),
    MOOD ("Mood",String.class,true),
    MUSICIANS ("Musicians",StringMap.class,true),
    ORIGINAL_ALBUM ("Original album",String.class,true),
    ORIGINAL_ARTIST ("Original artist",String.class,true),
    ORIGINAL_FILENAME ("Original filename",String.class,true),
    ORIGINAL_LYRICIST ("Original lyricist",String.class,true),
    ORIGINAL_MEDIA ("Original media",String.class,true),
    PLAY_COUNTER ("Play counter",Long.class,true),
    PLAYLISTDELAY_MS ("Playlist delay (ms)",Long.class,true),
    PRODUCTIONNOTICE ("Production notice",String.class,true),
    PUBLISHER ("Publisher",String.class,true),
    RECORDING_DATE ("Recording date",VariablePrecisionTime.class,true),
    RELEASE_DATE ("Release date",VariablePrecisionTime.class,true),
    REMIXER ("Remixer",String.class,true),
    REPLAYGAIN ("ReplayGain Track Data",ReplayGain.class,true),
    TERMSOFUSE ("Terms of Use",StringMapWithLocale.class,true),
    TITLE ("Title",String.class,true),
    TITLE_SORTORDER ("Title sort order",String.class,true),
    TRACK_SEQUENCE ("Track sequence",SequencePosition.class,true),
    TRACKSUBTITLE ("Track subtitle",String.class,true),
    URL_COMMERCIAL ("Commercial URL",URL.class,true),
    URL_COPYRIGHT ("Copyright URL",URL.class,true),
    URL_OFFICIALARTIST ("Official artist URL",URL.class,true),
    URL_OFFICIALAUDIOFILE ("Official audio file URL",URL.class,true),
    URL_OFFICIALAUDIOSRC ("Official audio source URL",URL.class,true),
    URL_OFFICIALINTERNETRADIOSTATION ("Official Internet radio station URL",URL.class,true),
    URL_PAYMENT ("Payment URL",URL.class,true),
    URL_PUBLISHER ("Publisher URL",URL.class,true),
    URL_USER ("User URL map",StringMap.class,true),
    USERTEXT ("User text",String.class,true),

    // Vorbis-specific fields.. 
    VORBISFIELD_ENCODERSOFTWARE ("Vorbis field/Encoder software",String.class,true),
    VORBISFIELD_ENCODERSETTINGS ("Vorbis field/Encoder settings",String.class,true),
    VORBISFIELD_TRACKNUMBER ("Vorbis field/Track number",Integer.class,true),
    VORBISFIELD_TRACKTOTAL ("Vorbis field/Track total",Integer.class,true),
    VORBISFIELD_DISCNUMBER ("Vorbis field/Disc number",Integer.class,true),
    VORBISFIELD_DISCTOTAL ("Vorbis field/Disc total",Integer.class,true),
    VORBISFIELD_DATE ("Vorbis field/Date",VariablePrecisionTime.class,true),
    VORBIS_REPLAYGAIN_TRACK_GAIN ("ReplayGain Track Gain",Float.class,true),
    VORBIS_REPLAYGAIN_TRACK_PEAK ("ReplayGain Track Peak",Double.class,true),
    VORBIS_REPLAYGAIN_ALBUM_GAIN ("ReplayGain Album Gain",Float.class,true),
    VORBIS_REPLAYGAIN_ALBUM_PEAK ("ReplayGain Album Peak",Double.class,true),
    VORBIS_REPLAYGAIN_REFERENCE_LOUDNESS ("ReplayGain Reference Loudness",Float.class,true),
    
 // generic Vorbis Comment fields..
    VORBISFIELD_UNKNOWN ("Unknown Vorbis Comment",String.class),

// Id3v2-specific frame types..
    ID3_YEAR ("ID3 year (YYYY)",VariablePrecisionTime.class,true),
    ID3V2_DATE ("ID3v2 date (MMDD)",Integer.class,true),
    ID3V2_TIME ("ID3v2 time (HHMM)",Integer.class,true),
    ID3V2_RECORDINGDATES ("Id3v2 recording date list",String.class,true),
    ID3V2_RECORDINGDATE ("Id3v2 recording date",VariablePrecisionTime.class,true),
    ID3V2_RELEASEDATE ("Id3v2 release date",VariablePrecisionTime.class,true),
    ID3V2_SIZEINFO ("ID3v2 size info",String.class,true),
    ID3V2_FILETYPE ("ID3v2 file type",String.class,true),
    ID3V2_ORIGINALYEAR ("ID3v2 original release year (YYYY)",VariablePrecisionTime.class,true),
    ID3V2_ORIGINALDATE ("ID3v2 original release date",VariablePrecisionTime.class,true),
    
// generic or unsupported Id3v2 frame types..
    ID3V2TEXTFRAME ("Id3v2 unknown text frame",String.class),
    ID3V2UNKNOWNFRAME ("Id3v2 unknown frame",ResizingByteBuffer.class),
    ID3V2URLFRAME ("Id3v2 unknown URL frame",URL.class),
    ID3V22UNKNOWNFRAME ("Id3v2 unknown frame (v2.2)",ResizingByteBuffer.class),
    ID3V2FRAME_AUDIOENCRYPTION ("Id3v2 frame/Audio encryption",ResizingByteBuffer.class,true),
    ID3V2FRAME_AUDIOSEEKPOINTINDEX ("Id3v2 frame/Audio seek point index",ResizingByteBuffer.class,true),
    ID3V2FRAME_COMMERCIAL ("Id3v2 frame/Commercial",ResizingByteBuffer.class,true),
    ID3V2FRAME_ENCAPSULATEDFILE ("Id3v2 frame/Encapsulated file",ResizingByteBuffer.class,true),
    ID3V2FRAME_ENCRYPTEDMETAFRAME ("Id3v2 frame/Encrypted meta frame",ResizingByteBuffer.class,true),
    ID3V2FRAME_ENCRYPTIONMETHODREGISTRATION ("Id3v2 frame/Encryption method registration",ResizingByteBuffer.class,true),
    ID3V2FRAME_EQUALIZATION ("Id3v2 frame/Equalization",ResizingByteBuffer.class,true),
    ID3V2FRAME_EQUALIZATION_2 ("Id3v2 frame/Equalization (2)",ResizingByteBuffer.class,true),
    ID3V2FRAME_EVENTTIMINGCODES ("Id3v2 frame/Event timing codes",ResizingByteBuffer.class,true),
    ID3V2FRAME_EXPERIMENTAL_RVA2_CLASS3 ("Id3v2 frame/Experimental relative volume adjustment (2)",ResizingByteBuffer.class,true),
    ID3V2FRAME_GROUPIDREGISTRATION ("Id3v2 frame/Group ID registration",ResizingByteBuffer.class,true),
    ID3V2FRAME_LINKEDINFO ("Id3v2 frame/Linked info",ResizingByteBuffer.class,true),
    ID3V2FRAME_MPEGLOCATIONLOOKUPTABLE ("Id3v2 frame/MPEG location lookup table",ResizingByteBuffer.class,true),
    ID3V2FRAME_MUSICCDIDENTIFIER ("Id3v2 frame/Music CD identifier",ResizingByteBuffer.class,true),
    ID3V2FRAME_MUSICMATCH ("Id3v2 frame/MusicMatch info",ResizingByteBuffer.class,true),
    ID3V2FRAME_NEXTTAGOFFSET ("Id3v2 frame/Next tag offset",ResizingByteBuffer.class,true),
    ID3V2FRAME_OWNERSHIP ("Id3v2 frame/Ownership",ResizingByteBuffer.class,true),
    ID3V2FRAME_POPULARIMETER ("Id3v2 frame/Popularimeter",ResizingByteBuffer.class,true),
    ID3V2FRAME_POSITIONSYNC ("Id3v2 frame/Position sync",ResizingByteBuffer.class,true),
    ID3V2FRAME_PRIVATE ("Id3v2 frame/Private",ResizingByteBuffer.class,true),
    ID3V2FRAME_RECOMMENDEDBUFFERSIZE ("Id3v2 frame/Recommended buffer size",ResizingByteBuffer.class,true),
    ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT ("Id3v2 frame/Relative volume adjustment",ResizingByteBuffer.class,true),
    ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT_2 ("Id3v2 frame/Relative volume adjustment (2)",ResizingByteBuffer.class,true),
    ID3V2FRAME_REPLAYGAINADJUSTMENT ("Id3v2 frame/Replay gain adjustment",ResizingByteBuffer.class,true),
    ID3V2FRAME_REVERB ("Id3v2 frame/Reverb",ResizingByteBuffer.class,true),
    ID3V2FRAME_SIGNATATURE ("Id3v2 frame/Signature",ResizingByteBuffer.class,true),
    ID3V2FRAME_SYNCLYRICS ("Id3v2 frame/Synchronized lyrics/text",ResizingByteBuffer.class,true),
    ID3V2FRAME_SYNCTEMPOCODES ("Id3v2 frame/Synchronized tempo codes",ResizingByteBuffer.class,true),
    ID3V2FRAME_UNIQUEFILEID ("Id3v2 frame/Unique file identifier",ResizingByteBuffer.class,true);
    
    private String description;
    private Class<?> dataType;
    private MediaProperty superProperty;
    
    /* 
     * false if uniqueness/agreement doesn't matter for this property
     */
    private boolean isUniqueAttribute; 

    MediaProperty(String description, Class<?> dataType) {
        this.description = description;
        this.dataType = dataType;
        this.superProperty = null;
        this.isUniqueAttribute = false; //default not expected to be unique
    }
    MediaProperty(String description, Class<?> dataType,boolean isUniqueAttribute) {
        this.description = description;
        this.dataType = dataType;
        this.superProperty = null;
        this.isUniqueAttribute = isUniqueAttribute;
    }
    MediaProperty(String description, MediaProperty superProperty) {
        this.description = description;
        this.dataType = null;
        this.superProperty = superProperty;
        this.isUniqueAttribute =  superProperty.getIsUniqueAttribute(); // default same as super
    }
    MediaProperty(String description, MediaProperty superProperty,boolean isUniqueAttribute) {
        this.description = description;
        this.dataType = null;
        this.superProperty = superProperty;
        this.isUniqueAttribute = isUniqueAttribute;
    }
    private MediaProperty() {
        this.description = null;
        this.dataType = Object.class;
        this.superProperty = null;
        this.isUniqueAttribute = false;
    }    

    public Class<?> getDataType() {
    	if(superProperty == null) {
    		if(dataType == null) {
    			return null;
    		}
    		return dataType;
    	}
        return superProperty.getDataType();
    }

    public String getDescription() {
        if(description==null)
            return super.toString();
        return description;
    }

    public String getShortName() {
        return super.toString();
    }

    @Override
    public String toString() {
        return getDescription();
    }
	@Override
	public boolean isTypeOf(Property p) {
		if(p==null)
			return true;
		if(equals(p))
			return true;
		if(this.superProperty!=null) {
			return this.superProperty.isTypeOf(p);
		}
		return false;
	}
	@Override
	public boolean getIsUniqueAttribute() {
		return this.isUniqueAttribute;
	}
	@Override
	public String getName() {
		return getShortName();
	}

	private static HashMap<String,Property> extendedProperties=new HashMap<String,Property>(); 
	public static Property getPropertyByName(String name) {
		return getPropertyByName(name,null);
	}
	public static Property getPropertyByName(String name, Property createUnderProperty) {
		Property p=null;
		try {
			p=MediaProperty.valueOf(name);
		} catch (IllegalArgumentException e) {
			String uppercaseName=name.toUpperCase();
			p=extendedProperties.get(uppercaseName);
			if(createUnderProperty!=null && p==null) {
				p=new ExtendedProperty(name,createUnderProperty);
				extendedProperties.put(uppercaseName,p);
			}
		}
		return p;
	}
}
