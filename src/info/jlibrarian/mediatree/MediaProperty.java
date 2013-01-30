package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.propertytree.Property;
import info.jlibrarian.specialtypes.FileMetadata;
import info.jlibrarian.specialtypes.Id3v2RawFrameContents;
import info.jlibrarian.specialtypes.Id3v2TagHeader;
import info.jlibrarian.specialtypes.SequencePosition;
import info.jlibrarian.specialtypes.StringMap;

import java.io.File;
import java.net.URL;

public enum MediaProperty implements Property {
	// file system objects..
    FOLDER ("Folder",File.class),
    OTHERFILE ("Other file ",FileMetadata.class),
//    IMAGEFILE ("Image file",FileMetadata.class), just use "PICTURE"
    AUDIOFILE ("Audio file",FileMetadata.class),
    SYMLINKFILE ("Symbolic file link",File.class),
    SYMLINKFOLDER ("Symbolic folder link",File.class),
            
// file tag/attachment objects..
    FLACMETADATATAG ("FLAC Metadata",String.class),// TODO: should this be unique?
    FLACMETADATABLOCK ("FLAC Metadata block",String.class),
    VORBISCOMMENTBLOCK ("Vorbis Comment block",String.class),// TODO: should this be unique?
    ID3V2TAG ("ID3 v2 Tag",Id3v2TagHeader.class),
    ID3V22TAG ("ID3 v2.2 Tag",MediaProperty.ID3V2TAG),
    ID3V23TAG ("ID3 v2.3 Tag",MediaProperty.ID3V2TAG),
    ID3V24TAG ("ID3 v2.4 Tag",MediaProperty.ID3V2TAG),
    ID3V2XTAG ("ID3 v2.5+ Tag (untested)",MediaProperty.ID3V2TAG),

// generic media properties..
    ALBUM ("Album",String.class,true),
    ALBUM_SORTORDER ("Album sort order",String.class,true),
    ALBUMARTIST ("Album artist",String.class,true),
    ALBUMARTIST_SORTORDER ("Album artist sort order",String.class,true),
    ARTIST ("Artist",String.class,true),
    ARTIST_SORTORDER ("Artist sort order",String.class,true),
    BEATSPERMINUTE ("Beats per minute (BPM)",Long.class,true),
    COMMENTS ("Comments",StringMap.class,true),
    COMPOSER ("Composer",String.class,true),
    COMPOSER_SORTORDER ("Composer sort order",String.class,true),
    CONDUCTOR ("Conductor",String.class,true),
    CONTENTGROUP ("Content group",String.class,true),
    COPYRIGHTMESSAGE ("Copyright message",String.class,true),
    DISC_SEQUENCE ("Disc sequence",SequencePosition.class,true),
    DISCSUBTITLE ("Disc subtitle",String.class,true),
    PICTURE ("Picture",FileMetadata.class,true),
    ENCODEDBY ("Encoded by",String.class,true),
    ENCODER ("Encoder/settings",String.class,true),
    FILEOWNER ("File owner",String.class,true),
    GENRE ("Genre",String.class,true),
    INITIALKEY ("Initial key",String.class,true),
    INTERNETRADIOSTATION_NAME ("Internet radio station name",String.class,true),
    INTERNETRADIOSTATION_OWNER ("Internet radio station owner",String.class,true),
    INVOLVEDPEOPLE ("Involved People",StringMap.class,true),
    LENGTH_MS ("Length (ms)",Long.class,true),
    LYRICIST ("Lyricist",String.class,true),
    LYRICS ("Lyrics",StringMap.class,true),
    MOOD ("Mood",String.class,true),
    MUSICIANS ("Musicians",StringMap.class,true),
    ORIGINAL_ALBUM ("Original album",String.class,true),
    ORIGINAL_ARTIST ("Original artist",String.class,true),
    ORIGINAL_FILENAME ("Original filename",String.class,true),
    ORIGINAL_LYRICIST ("Original lyricist",String.class,true),
    ORIGINAL_MEDIA ("Original media",String.class,true),
    PLAYLISTDELAY_MS ("Playlist delay (ms)",Long.class,true),
    PRODUCTIONNOTICE ("Production notice",String.class,true),
    PUBLISHER ("Publisher",String.class,true),
    REMIXER ("Remixer",String.class,true),
    TERMSOFUSE ("Terms of Use",String.class,true),
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
    USERTEXT ("User text",StringMap.class,true),

// generic Vorbis Comment fields..
    VORBISFIELD_UNKNOWN ("Unknown Vorbis Comment",String.class),

// Vorbis-specific fields.. 
    VORBISFIELD_ENCODERSOFTWARE ("Vorbis field/Encoder software",String.class,true),
    VORBISFIELD_ENCODERSETTINGS ("Vorbis field/Encoder settings",String.class,true),
    VORBISFIELD_TRACKNUMBER ("Vorbis field/Track number",Integer.class,true),
    VORBISFIELD_TRACKTOTAL ("Vorbis field/Track total",Integer.class,true),
    VORBISFIELD_DISCNUMBER ("Vorbis field/Disc number",Integer.class,true),
    VORBISFIELD_DISCTOTAL ("Vorbis field/Disc total",Integer.class,true),

// Id3v2-specific frame types..
    ID3V2_RECORDINGDATES ("Id3v2 recording dates",String.class),
    ID3V2_SIZEINFO ("Id3v2 size info",String.class),
    ID3V2_FILETYPE ("Id3v2 file type",String.class),
    
// generic or unsupported Id3v2 frame types..
    ID3V2TEXTFRAME ("Id3v2 unknown text frame",String.class),
    ID3V2UNKNOWNFRAME ("Id3v2 unknown frame",Id3v2RawFrameContents.class),
    ID3V2URLFRAME ("Id3v2 unknown URL frame",URL.class),
    ID3V22UNKNOWNFRAME ("Id3v2 unknown frame (v2.2)",Id3v2RawFrameContents.class),
    ID3V2FRAME_AUDIOENCRYPTION ("Id3v2 frame/Audio encryption",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_AUDIOSEEKPOINTINDEX ("Id3v2 frame/Audio seek point index",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_COMMERCIAL ("Id3v2 frame/Commercial",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_ENCRYPTEDMETAFRAME ("Id3v2 frame/Encrypted meta frame",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_ENCRYPTIONMETHODREGISTRATION ("Id3v2 frame/Encryption method registration",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_EQUALIZATION ("Id3v2 frame/Equalization",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_EQUALIZATION_2 ("Id3v2 frame/Equalization (2)",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_EVENTTIMINGCODES ("Id3v2 frame/Event timing codes",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_EXPERIMENTAL_RVA2_CLASS3 ("Id3v2 frame/Experimental relative volume adjustment (2)",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_GROUPIDREGISTRATION ("Id3v2 frame/Group ID registration",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_LINKEDINFO ("Id3v2 frame/Linked info",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_MPEGLOCATIONLOOKUPTABLE ("Id3v2 frame/MPEG location lookup table",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_MUSICCDIDENTIFIER ("Id3v2 frame/Music CD identifier",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_MUSICMATCH ("Id3v2 frame/MusicMatch info",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_NEXTTAGOFFSET ("Id3v2 frame/Next tag offset",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_OWNERSHIP ("Id3v2 frame/Ownership",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_POPULARIMETER ("Id3v2 frame/Popularimeter",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_POSITIONSYNC ("Id3v2 frame/Position sync",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_PRIVATE ("Id3v2 frame/Private",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_RECOMMENDEDBUFFERSIZE ("Id3v2 frame/Recommended buffer size",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT ("Id3v2 frame/Relative volume adjustment",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT_2 ("Id3v2 frame/Relative volume adjustment (2)",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_REPLAYGAINADJUSTMENT ("Id3v2 frame/Replay gain adjustment",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_REVERB ("Id3v2 frame/Reverb",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_SIGNATATURE ("Id3v2 frame/Signature",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_SYNCLYRICS ("Id3v2 frame/Synchronized lyrics/text",Id3v2RawFrameContents.class,true),
    ID3V2FRAME_SYNCTEMPOCODES ("Id3v2 frame/Synchronized tempo codes",Id3v2RawFrameContents.class,true);
    
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

}
