package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */


import info.jlibrarian.stringutils.VersionString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This is the global registry of filetype/tag/frame/version/class.
 * 
 * To support or enable handling of a specific frame/field/tag, register it here.
 *
 * see MediaProperty.java for properties that can be represented & datatypes
 *
 * @author ivan
 */

// TODO: implement mp3 structure, Xing/VBRI headers, length/bitrate analysis
// TODO: implement id3v1 tag
// TODO: implement APE tag
// TODO: implement Lyrics3 tag
// TODO: detect MusicMatch tag

public class Registry {
    private static HashMap<String,FileType> fileTypes=new HashMap<String,FileType>();
    private static ArrayList<Id3v2FrameConfig> id3v2Fields=new ArrayList<Id3v2FrameConfig>();
    private static ArrayList<VorbisCommentConfig> vorbisFields=new ArrayList<VorbisCommentConfig>();
    //private static ArrayList<VirtualTagField> virtualFields=new ArrayList<VirtualTagField>();
    static {
/**
 * Here is where file "extensions" (the last part of the name following the 
 * last dot, or "" if there is no dot) are registered to MediaFile node
 * classes and MediaTag node classes.  Each extension maps to one
 * MediaFile class, and up to several tag node classes.  Classes
 * should be concrete/instantiateable.
 * 
 * all file name extensions are converted to lower case.
 */
fileTypes.put("flac",new FileType(MediaFile.class,MediaProperty.AUDIOFILE)
                            .addTagConfig(FlacMetadataBlock.class,MediaProperty.FLACMETADATA,null));
fileTypes.put("mp3", new FileType(MediaFile.class,MediaProperty.AUDIOFILE)
                            .addTagConfig(Id3v2Tag.class,MediaProperty.ID3V22TAG,"2.2.*")
                            .addTagConfig(Id3v2Tag.class,MediaProperty.ID3V23TAG,"2.3.*")
                            .addTagConfig(Id3v2Tag.class,MediaProperty.ID3V24TAG,"2.4.*")
                            .addTagConfig(Id3v2Tag.class,MediaProperty.ID3V2XTAG,"2.5+"));
fileTypes.put("wav",new FileType(MediaFile.class,MediaProperty.AUDIOFILE));
fileTypes.put("jpg", new FileType(MediaFile.class,MediaProperty.PICTURE));
fileTypes.put("jpeg", new FileType(MediaFile.class,MediaProperty.PICTURE));
fileTypes.put("png", new FileType(MediaFile.class,MediaProperty.PICTURE));
// MediaFolder loader will instantiate nodes for unrecognized files w/ MediaProperty.OTHERFILE 

registerId3v2("TAL","2.2.*",Id3v2TextFrame.class,MediaProperty.ALBUM,false);
registerId3v2("TALB","2.3+",Id3v2TextFrame.class,MediaProperty.ALBUM,false);
registerVorbis("ALBUM",VorbisTextField.class,MediaProperty.ALBUM);

registerId3v2("TSOA","2.4+",Id3v2TextFrame.class,MediaProperty.ALBUM_SORTORDER,false);
registerId3v2("XSOA","2.3.*",Id3v2TextFrame.class,MediaProperty.ALBUM_SORTORDER,false);
registerVorbis("ALBUMSORT",VorbisTextField.class,MediaProperty.ALBUM_SORTORDER);

registerId3v2("TP2","2.2.*",Id3v2TextFrame.class,MediaProperty.ALBUMARTIST,false);
registerId3v2("TPE2","2.3+",Id3v2TextFrame.class,MediaProperty.ALBUMARTIST,false);
registerVorbis("ALBUMARTIST",VorbisTextField.class,MediaProperty.ALBUMARTIST);

registerId3v2("TSO2","2.3+",Id3v2TextFrame.class,MediaProperty.ALBUMARTIST_SORTORDER,false);
registerVorbis("ALBUMARTISTSORT",VorbisTextField.class,MediaProperty.ALBUMARTIST_SORTORDER);

registerId3v2("TP1","2.2.*",Id3v2TextFrame.class,MediaProperty.ARTIST,false);
registerId3v2("TPE1","2.3+",Id3v2TextFrame.class,MediaProperty.ARTIST,false);
registerVorbis("ARTIST",VorbisTextField.class,MediaProperty.ARTIST);

registerId3v2("TSOP","2.4+",Id3v2TextFrame.class,MediaProperty.ARTIST_SORTORDER,false);
registerId3v2("XSOP","2.3.*",Id3v2TextFrame.class,MediaProperty.ARTIST_SORTORDER,false);
registerVorbis("ARTISTSORT",VorbisTextField.class,MediaProperty.ARTIST_SORTORDER);

registerId3v2("TBP","2.2.*",Id3v2TextFrame.class,MediaProperty.BEATSPERMINUTE,false);
registerId3v2("TBPM","2.3+",Id3v2TextFrame.class,MediaProperty.BEATSPERMINUTE,false);
registerVorbis("BPM",VorbisTextField.class,MediaProperty.BEATSPERMINUTE); 

registerId3v2("COM","2.2.*",Id3v2CommentFrame.class,MediaProperty.COMMENTS);
registerId3v2("COMM","2.3+",Id3v2CommentFrame.class,MediaProperty.COMMENTS);

registerId3v2("TCM","2.2.*",Id3v2TextFrame.class,MediaProperty.COMPOSER,false);
registerId3v2("TCOM","2.3+",Id3v2TextFrame.class,MediaProperty.COMPOSER,false);
registerVorbis("COMPOSER",VorbisTextField.class,MediaProperty.COMPOSER);

registerId3v2("TSOC","2.3+",Id3v2TextFrame.class,MediaProperty.COMPOSER_SORTORDER,false);
registerVorbis("COMPOSERSORT",VorbisTextField.class,MediaProperty.COMPOSER_SORTORDER);

registerId3v2("TP3","2.2.*",Id3v2TextFrame.class,MediaProperty.CONDUCTOR,false);
registerId3v2("TPE3","2.3+",Id3v2TextFrame.class,MediaProperty.CONDUCTOR,false);
registerVorbis("CONDUCTOR",VorbisTextField.class,MediaProperty.CONDUCTOR);

registerId3v2("TIT1","2.3+",Id3v2TextFrame.class,MediaProperty.CONTENTGROUP,false);
registerId3v2("TT1","2.2.*",Id3v2TextFrame.class,MediaProperty.CONTENTGROUP,false);
registerVorbis("GROUPING",VorbisTextField.class,MediaProperty.CONTENTGROUP);

registerId3v2("TCOP","2.3+",Id3v2TextFrame.class,MediaProperty.COPYRIGHTMESSAGE,false);
registerId3v2("TCR","2.2.*",Id3v2TextFrame.class,MediaProperty.COPYRIGHTMESSAGE,false);
registerVorbis("COPYRIGHT",VorbisTextField.class,MediaProperty.COPYRIGHTMESSAGE);

registerId3v2("TPA","2.2.*",Id3v2TextFrame.class,MediaProperty.DISC_SEQUENCE,false);
registerId3v2("TPOS","2.3+",Id3v2TextFrame.class,MediaProperty.DISC_SEQUENCE,false);

registerId3v2("TSST","2.4+",Id3v2TextFrame.class,MediaProperty.DISCSUBTITLE,false);
registerVorbis("DISCSUBTITLE",VorbisTextField.class,MediaProperty.DISCSUBTITLE);

registerId3v2("PIC","2.2.*",Id3v2PictureFrame.class,MediaProperty.PICTURE,true);
registerId3v2("APIC","2.3+",Id3v2PictureFrame.class,MediaProperty.PICTURE,true);

registerId3v2("TEN","2.2.*",Id3v2TextFrame.class,MediaProperty.ENCODEDBY,false);
registerId3v2("TENC","2.3+",Id3v2TextFrame.class,MediaProperty.ENCODEDBY,false);
registerVorbis("ENCODEDBY",VorbisTextField.class,MediaProperty.ENCODEDBY,true);

registerId3v2("TSS","2.2.*",Id3v2TextFrame.class,MediaProperty.ENCODER,false);
registerId3v2("TSSE","2.3+",Id3v2TextFrame.class,MediaProperty.ENCODER,false);
registerVorbis("ENCODER",VorbisTextField.class,MediaProperty.VORBISFIELD_ENCODERSOFTWARE);
registerVorbis("ENCODING",VorbisTextField.class,MediaProperty.VORBISFIELD_ENCODERSETTINGS);

registerId3v2("TOWN","2.3+",Id3v2TextFrame.class,MediaProperty.FILEOWNER,false);

registerId3v2("TFLT","2.3+",Id3v2TextFrame.class,MediaProperty.ID3V2_FILETYPE,false);

/* On "date" fields: The concept of "Recording Year" is different than "Release Year". So what?
 *  
 * ID3v1 specifies a 4 digit "year" which seems to have been generally used as "release year".
 * 
 * ID3v2.2.0 specifies frame "TYE" which is "year of the recording" and generally used for "release year."
 * 		Another frame TRD is labeled as "recording dates" and meant as a "complement to" TYE, implying
 * 		that TYE is actually meant to be recording year. Another frame TOR specifies the "Original Release 
 * 		Year" (as in a cover song) but there is no frame specifically meant for release year. It appears
 * 		that most tags use TYE to represent release year.
 *
 * ID3v2.3.0 has new frameIDs TYER (TYE), TRDA (TRD), TORY (TOR) etc and defines them similarly to 2.2.0.
 * 		There is still no frame for release year and it seems to me that it is still common practice to 
 * 		use TYER for release year.
 * 		
 * The ID3v2.4.0 spec states that the new frame TDRC "Recording date" replaces TYER.  It also introduces 
 * 		another new frame TDRL "release date" (finally!). However, many tags converted from older 
 * 		versions of ID3 would have the release year stored in ID3V1-Year, TYE or TYER frames originally. So 
 * 		after conversion they would have the release date in TDRC instead of TDRL. I am not sure how many 
 * 		people are using the new "release date" field but I have definitely	seen plenty of example 2.4 tags 
 * 		where TDRC "recording date" is used to store the release date, and TDRL "release date" is not used.
 *  
 * Vorbis Comment (FLAC/Vorbis) spec provides suggestions/recommendations for field names and contents. One
 * 		fairly universal field is "DATE". This field may be repeated multiple times but usually it is not.
 * 		It consists of an ISO 8601 date optionally followed by a space and additional text. The additional
 * 		text indicates the meaning of the date, e.g. "2004-01-21 released" or "2003-11-13 (recorded date)"
 *		If no qualifier is given after the date, on
 * 
 * In order to do high-level processing of the tag data and convert tags between formats, Conan needs to 
 * associate each value or set of values with a particular property - for instance, if TYER=2004, Conan 
 * needs to know whether that is the "recording date" as the ID3v2 spec would imply, or the release date
 * as one might reasonably assume based on common usage (unless the recording has not been released).
 * 
 * The question therefore is, when interpreting tag data from any combination of the above tags, how 
 * should it be internally represented? NOTE: Probably needs to be user configurable. Options:
 * 
 * 1. "Specs-compliant": Release date is not available in ID3v2.2/2.3 (can be custom frame or 
 * 		comment). ID3v1 year and VorbisComment DATE (no qualifier present) are interpreted 
 * 		as release year. In ID3v2.4, use TDRL for release year. Writing FLAC, include the 
 * 		"release" qualifier after the date when writing release year as DATE.
 * 
 * 		PRO: compliant/compatible with standard tagging specs
 * 		CON: I think most people/programs use TYE/TYER to mean release date, so interpreting it as 
 * 			recording date in existing files is very likely to result in factually incorrect reads.
 * 			also this means release date cannot be written to ID3v2.2/2.3 without using custom
 * 			fields or just putting it in a comment.
 *
 * 2. "TYE/TYER are release date": Follow the specs as above, except on ID3v2.2/v2.3, interpret the 
 * 		TYE/TYER dates (and associated frames) to mean "Release Date" instead of "Recording
 * 		date" and pretend that these tag versions don't actually have frames for recording date.
 * 		But in ID3v2.4 use the specified frames (TDRC=recording, TDRL=release).  
 * 
 * 		PRO: Tags created by compliant 2.4 readers will be interpreted correctly. But normal
 * 			behavior of using TYE/TYER for release date in 2.2/2.3 is respected.
 * 		CON: not compliant with 2.2/2.3 spec. Tags upconverted to 2.4 by compliant 2.4 taggers
 * 			will sometimes be misinterpreted since TDRC will contain release date.  
 * 
 * 3. "TYE/TYER is release date, TDRC might be": In ALL ID3v2 versions, interpret the frames 
 * 		TYE/TYER/TDRC as release date (common usage), although the spec says otherwise. Except in 
 * 		ID3v2.4 if BOTH the TDRC and TDRL frames are present and have different values, or if the tag
 * 		was created by Conan, interpret according to spec. If writing v2.4, use TDRL for release date.
 * 
 * 		PRO: Simple approach that reflects expected behavior by 80-90% of users that there is
 * 			just one date associated with each track. Tags converted to 2.4 or created in compliant
 * 			2.4 taggers will be interpreted correctly*.
 * 		CON: not compliant with any ID3v2 spec. *=if a file is tagged with recording date only and
 * 			no release date in v2.4, Conan will assume it was mistagged due to upconversion and
 * 			will interpret the date as a release date.
 *
 * 
 * Solution for now:
 * 		- Every ID3v2 tag gets a virtual recording date and release date field
 * 		- 
 */

registerId3v2("TYE","2.2.*", Id3v2TextFrame.class,MediaProperty.ID3_YEAR,false);
registerId3v2("TYER","2.3+", Id3v2TextFrame.class,MediaProperty.ID3_YEAR,false);
registerId3v2("TDA","2.2.*",Id3v2TextFrame.class,MediaProperty.ID3V2_DATE,false);
registerId3v2("TDAT","2.3+",Id3v2TextFrame.class,MediaProperty.ID3V2_DATE,false);
registerId3v2("TIM","2.2.*",Id3v2TextFrame.class,MediaProperty.ID3V2_TIME,false);
registerId3v2("TIME","2.3+",Id3v2TextFrame.class,MediaProperty.ID3V2_TIME,false);
registerId3v2("TRD","2.2.*",Id3v2TextFrame.class,MediaProperty.ID3V2_RECORDINGDATES,false);
registerId3v2("TRDA","2.3+",Id3v2TextFrame.class,MediaProperty.ID3V2_RECORDINGDATES,false);

registerId3v2("TOR","2.2.*",Id3v2TextFrame.class,MediaProperty.ID3V2_ORIGINALYEAR,false);
registerId3v2("TORY","2.3+",Id3v2TextFrame.class,MediaProperty.ID3V2_ORIGINALYEAR,false);

registerId3v2("TDRC","2.4+",Id3v2TextFrame.class,MediaProperty.ID3V2_RECORDINGDATE,false);
registerId3v2("TDRL","2.4+",Id3v2TextFrame.class,MediaProperty.ID3V2_RELEASEDATE,false);
registerId3v2("TDOR","2.4+",Id3v2TextFrame.class,MediaProperty.ID3V2_ORIGINALDATE,false);


/*
 * Content type fields 
 */
registerId3v2("TCO","2.2.*",Id3v2TextFrame.class,MediaProperty.GENRES,false);
registerId3v2("TCON","2.3+",Id3v2TextFrame.class,MediaProperty.GENRES,false);

registerId3v2("TSI","2.2.*",Id3v2TextFrame.class,MediaProperty.ID3V2_SIZEINFO,false);
registerId3v2("TSIZ","2.3.*",Id3v2TextFrame.class,MediaProperty.ID3V2_SIZEINFO,false);

registerId3v2("AENC","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_AUDIOENCRYPTION);

registerId3v2("ASPI","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_AUDIOSEEKPOINTINDEX,false);

registerId3v2("COMR","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_COMMERCIAL);

registerId3v2("ENCR","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_ENCRYPTEDMETAFRAME);

registerId3v2("EQUA","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_EQUALIZATION,false);

registerId3v2("EQU2","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_EQUALIZATION_2);

registerId3v2("ETCO","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_EVENTTIMINGCODES,false);

registerId3v2("XRVA","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_EXPERIMENTAL_RVA2_CLASS3,false);

registerId3v2("GRID","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_GROUPIDREGISTRATION);

registerId3v2("LINK","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_LINKEDINFO);

registerId3v2("MLLT","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_MPEGLOCATIONLOOKUPTABLE,false);

registerId3v2("MCDI","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_MUSICCDIDENTIFIER,false);

registerId3v2("NCON","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_MUSICMATCH);

registerId3v2("OWNE","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_OWNERSHIP,false);

registerId3v2("POPM","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_POPULARIMETER);

registerId3v2("POSS","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_POSITIONSYNC,false);

registerId3v2("PRIV","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_PRIVATE);

registerId3v2("RBUF","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_RECOMMENDEDBUFFERSIZE,false);

registerId3v2("RVAD","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT,false);

registerId3v2("RVA2","2.4+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_RELATIVEVOLUMEADJUSTMENT_2);

registerId3v2("RGAD","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_REPLAYGAINADJUSTMENT,false);

registerId3v2("RVRB","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_REVERB,false);

registerId3v2("SIGN","2.4+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_SIGNATATURE);

registerId3v2("SYLT","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_SYNCLYRICS);

registerId3v2("SYTC","2.3+",Id3v2RawFrame.class,MediaProperty.ID3V2FRAME_SYNCTEMPOCODES,false);

registerId3v2("TKE","2.2.*",Id3v2TextFrame.class,MediaProperty.INITIALKEY,false);
registerId3v2("TKEY","2.3+",Id3v2TextFrame.class,MediaProperty.INITIALKEY,false);

registerId3v2("TRSN","2.3+",Id3v2TextFrame.class,MediaProperty.INTERNETRADIOSTATION_NAME,false);

registerId3v2("TRSO","2.3+",Id3v2TextFrame.class,MediaProperty.INTERNETRADIOSTATION_OWNER,false);

registerId3v2("IPL","2.2.*",Id3v2TextMapFrame.class,MediaProperty.INVOLVEDPEOPLE,false);
registerId3v2("IPLS","2.3.*",Id3v2TextMapFrame.class,MediaProperty.INVOLVEDPEOPLE,false);
registerId3v2("TIPL","2.4+",Id3v2TextMapFrame.class,MediaProperty.INVOLVEDPEOPLE,false);

registerId3v2("TLE","2.2.*",Id3v2TextFrame.class,MediaProperty.LENGTH_MS,false);
registerId3v2("TLEN","2.3+",Id3v2TextFrame.class,MediaProperty.LENGTH_MS,false);

registerId3v2("TEXT","2.3+",Id3v2TextFrame.class,MediaProperty.LYRICIST,false);
registerId3v2("TXT","2.2.*",Id3v2TextFrame.class,MediaProperty.LYRICIST,false);
registerVorbis("LYRICIST",VorbisTextField.class,MediaProperty.LYRICIST);

registerId3v2("ULT","2.2.*",Id3v2CommentFrame.class,MediaProperty.LYRICS);
registerId3v2("USLT","2.3+",Id3v2CommentFrame.class,MediaProperty.LYRICS);

registerId3v2("TMOO","2.4+",Id3v2TextFrame.class,MediaProperty.MOOD,false);
registerVorbis("MOOD",VorbisTextField.class,MediaProperty.MOOD);

registerId3v2("TMCL","2.4+",Id3v2TextMapFrame.class,MediaProperty.MUSICIANS,false);

registerId3v2("TOAL","2.3+",Id3v2TextFrame.class,MediaProperty.ORIGINAL_ALBUM,false);
registerId3v2("TOT","2.2.*",Id3v2TextFrame.class,MediaProperty.ORIGINAL_ALBUM,false);
registerId3v2("TOA","2.2.*",Id3v2TextFrame.class,MediaProperty.ORIGINAL_ARTIST,false);

registerId3v2("TOPE","2.3+",Id3v2TextFrame.class,MediaProperty.ORIGINAL_ARTIST,false);

registerId3v2("TOF","2.2.*",Id3v2TextFrame.class,MediaProperty.ORIGINAL_FILENAME,false);
registerId3v2("TOFN","2.3+",Id3v2TextFrame.class,MediaProperty.ORIGINAL_FILENAME,false);

registerId3v2("TOL","2.2.*",Id3v2TextFrame.class,MediaProperty.ORIGINAL_LYRICIST,false);
registerId3v2("TOLY","2.3+",Id3v2TextFrame.class,MediaProperty.ORIGINAL_LYRICIST,false);

registerId3v2("TMED","2.3+",Id3v2TextFrame.class,MediaProperty.ORIGINAL_MEDIA,false);
registerId3v2("TMT","2.2.*",Id3v2TextFrame.class,MediaProperty.ORIGINAL_MEDIA,false);
registerVorbis("MEDIA",VorbisTextField.class,MediaProperty.ORIGINAL_MEDIA);

registerId3v2("TDLY","2.3+",Id3v2TextFrame.class,MediaProperty.PLAYLISTDELAY_MS,false);
registerId3v2("TDY","2.2.*",Id3v2TextFrame.class,MediaProperty.PLAYLISTDELAY_MS,false);

registerId3v2("TPRO","2.4+",Id3v2TextFrame.class,MediaProperty.PRODUCTIONNOTICE,false);

registerId3v2("TPB","2.2.*",Id3v2TextFrame.class,MediaProperty.PUBLISHER,false);
registerId3v2("TPUB","2.3+",Id3v2TextFrame.class,MediaProperty.PUBLISHER,false);
registerVorbis("LABEL",VorbisTextField.class,MediaProperty.PUBLISHER,true); // alt
registerVorbis("ORGANIZATION",VorbisTextField.class,MediaProperty.PUBLISHER);

registerId3v2("TP4","2.2.*",Id3v2TextFrame.class,MediaProperty.REMIXER,false);
registerId3v2("TPE4","2.3+",Id3v2TextFrame.class,MediaProperty.REMIXER,false);
registerVorbis("REMIXER",VorbisTextField.class,MediaProperty.REMIXER);

registerId3v2("TIT2","2.3+",Id3v2TextFrame.class,MediaProperty.TITLE,false);
registerId3v2("TT2","2.2.*",Id3v2TextFrame.class,MediaProperty.TITLE,false);
registerVorbis("TITLE",VorbisTextField.class,MediaProperty.TITLE);

registerId3v2("TSOT","2.4+",Id3v2TextFrame.class,MediaProperty.TITLE_SORTORDER,false);
registerId3v2("XSOT","2.3.*",Id3v2TextFrame.class,MediaProperty.TITLE_SORTORDER,false);
registerVorbis("TITLESORT",VorbisTextField.class,MediaProperty.TITLE_SORTORDER);

registerId3v2("TRCK","2.3+",Id3v2TextFrame.class,MediaProperty.TRACK_SEQUENCE,false);
registerId3v2("TRK","2.2.*",Id3v2TextFrame.class,MediaProperty.TRACK_SEQUENCE,false);
// VorbisComment gets TRACK_SEQUENCE by instantiating a SequenceView child of the tag

registerId3v2("TIT3","2.3+",Id3v2TextFrame.class,MediaProperty.TRACKSUBTITLE,false);
registerId3v2("TT3","2.2.*",Id3v2TextFrame.class,MediaProperty.TRACKSUBTITLE,false);
registerVorbis("SUBTITLE",VorbisTextField.class,MediaProperty.TRACKSUBTITLE);
registerVorbis("VERSION",VorbisTextField.class,MediaProperty.TRACKSUBTITLE,true); // alt?

//registerId3v2("DATE","2.2.*", Id3v2TextFrame.class,MediaProperty.RECORDINGDATE);
// id3 gets recording date by instantiating a DateView child of the tag

registerId3v2("WCM","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_COMMERCIAL,false);
registerId3v2("WCOM","2.3+",Id3v2URLFrame.class,MediaProperty.URL_COMMERCIAL,false);

registerId3v2("WCOP","2.3+",Id3v2URLFrame.class,MediaProperty.URL_COPYRIGHT,false);
registerId3v2("WCP","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_COPYRIGHT,false);

registerId3v2("WAS","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALARTIST,false);
registerId3v2("WOAR","2.3+",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALARTIST,false);

registerId3v2("WAF","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALAUDIOFILE,false);
registerId3v2("WOAF","2.3+",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALAUDIOFILE,false);

registerId3v2("WAS","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALAUDIOSRC,false);
registerId3v2("WOAS","2.3+",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALAUDIOSRC,false);

registerId3v2("WORS","2.3+",Id3v2URLFrame.class,MediaProperty.URL_OFFICIALINTERNETRADIOSTATION,false);

registerId3v2("WPAY","2.3+",Id3v2URLFrame.class,MediaProperty.URL_PAYMENT,false);

registerId3v2("WPB","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_PUBLISHER,false);
registerId3v2("WPUB","2.3+",Id3v2URLFrame.class,MediaProperty.URL_PUBLISHER,false);

registerId3v2("WPB","2.2.*",Id3v2URLFrame.class,MediaProperty.URL_PUBLISHER,false);
registerId3v2("WPUB","2.3+",Id3v2URLFrame.class,MediaProperty.URL_PUBLISHER,false);

registerId3v2("WXX","2.2.*",Id3v2TextMapFrame.class,MediaProperty.URL_USER,false);
registerId3v2("WXXX","2.3+",Id3v2TextMapFrame.class,MediaProperty.URL_USER,false);

registerId3v2("TXX","2.2.*",Id3v2TextMapFrame.class,MediaProperty.USERTEXT);
registerId3v2("TXXX","2.3+",Id3v2TextMapFrame.class,MediaProperty.USERTEXT);

registerId3v2("USER","2.3+",Id3v2CommentFrame.class,MediaProperty.TERMSOFUSE);

registerVorbis("DISCNUMBER",VorbisTextField.class,MediaProperty.VORBISFIELD_DISCNUMBER);
registerVorbis("DISC",VorbisTextField.class,MediaProperty.VORBISFIELD_DISCNUMBER,true); //alt
registerVorbis("DISCC",VorbisTextField.class,MediaProperty.VORBISFIELD_DISCNUMBER,true); //alt

registerVorbis("DISCTOTAL",VorbisTextField.class,MediaProperty.VORBISFIELD_DISCTOTAL);
registerVorbis("TOTALDISCS",VorbisTextField.class,MediaProperty.VORBISFIELD_DISCTOTAL,true); // alt

registerVorbis("TRACKNUMBER",VorbisTextField.class,MediaProperty.VORBISFIELD_TRACKNUMBER);

registerVorbis("TRACKTOTAL",VorbisTextField.class,MediaProperty.VORBISFIELD_TRACKTOTAL);
registerVorbis("TOTALTRACKS",VorbisTextField.class,MediaProperty.VORBISFIELD_TRACKTOTAL,true); // alt
    }
    public static class FileType {
        public static class TagConfig {
            public Class<? extends MediaTag> tagClass;
            public MediaProperty tagProperty;
            public String tagVersions;
            public TagConfig(Class<? extends MediaTag> tagClass, MediaProperty tagProperty, String tagVersions) {
                this.tagClass = tagClass;
                this.tagProperty = tagProperty;
                this.tagVersions = tagVersions;
            }
        }
        public final Class<? extends MediaFile> fileClass;
        public final MediaProperty fileProperty;
        public List<TagConfig> tagInfo=new ArrayList<TagConfig>(1);
        public FileType(Class<? extends MediaFile> fileCl, MediaProperty fileProp) {
            this.fileClass = fileCl;
            this.fileProperty = fileProp;
        }
        public FileType addTagConfig(Class<? extends MediaTag> tagNodeClass,MediaProperty tagProp,String tagOpt) {
            this.tagInfo.add(new TagConfig(tagNodeClass,tagProp,tagOpt));
            return this;
        }
    }
    public static class VorbisCommentConfig {
        String fieldName;
        Class<? extends FrameNode> fieldClass;
        MediaProperty fieldProperty;
        boolean alternate;
        public VorbisCommentConfig(String fieldName, Class<? extends FrameNode> fieldClass, MediaProperty fieldProperty) {
            this.fieldName = fieldName;
            this.fieldClass = fieldClass;
            this.fieldProperty = fieldProperty;
            alternate=false;
        }

        public VorbisCommentConfig(String fieldName, Class<? extends FrameNode> fieldClass, MediaProperty fieldProperty, boolean alternate) {
            this.fieldName = fieldName;
            this.fieldClass = fieldClass;
            this.fieldProperty = fieldProperty;
            this.alternate = alternate;
        }
        
    }
    public static class Id3v2FrameConfig {
        public String frameID;
        public String versionMatch;
        public Class <? extends Id3v2Frame> frameClass;
        public MediaProperty frameProperty;
        public boolean canOccurMultiple;

        public Id3v2FrameConfig(String frameID, String versionMatch, Class<? extends Id3v2Frame> frameClass, MediaProperty frameProperty, boolean canOccurMultiple) {
            this.frameID = frameID;
            this.versionMatch = versionMatch;
            this.frameClass = frameClass;
            this.frameProperty = frameProperty;
            this.canOccurMultiple = canOccurMultiple;
        }
    }
    
    private static void registerId3v2(String frame,String verMatch,Class<? extends Id3v2Frame> frameClass,MediaProperty frameProp) {
        id3v2Fields.add(new Id3v2FrameConfig(frame,verMatch,frameClass,frameProp,true));
    }
    private static void registerId3v2(String frame,String verMatch,Class<? extends Id3v2Frame> frameClass,MediaProperty frameProp,boolean canOccurMultiple) {
        id3v2Fields.add(new Id3v2FrameConfig(frame,verMatch,frameClass,frameProp,canOccurMultiple));
    }
    public static FileType getFileType(File f) {
        return fileTypes.get(MediaFileUtil.getFileExtension(f));
    }
    public static List<FileType.TagConfig> getTagsByFile(File f) {
        FileType ft=fileTypes.get(MediaFileUtil.getFileExtension(f).toLowerCase());
        if(ft==null) 
        	return null;
        return ft.tagInfo;
    }
    public static Id3v2FrameConfig getId3v2FrameConfig(String frameID,String vers) {
        for(Id3v2FrameConfig cfg : id3v2Fields) {
            if(cfg.frameID.equals(frameID) 
                    && 0==VersionString.compareVersions(cfg.versionMatch,vers)) {
                return cfg;
            }
        }
        return null;
    }
    // alternate: get frame config searching by property and tag version
	public static Id3v2FrameConfig getId3v2FrameConfig(MediaProperty frameProperty,
			Id3v2Tag targetTag) {
		Id3v2FrameConfig match=null;
        for(Id3v2FrameConfig cfg : id3v2Fields) {
            if(cfg.frameProperty.equals(frameProperty) 
                    && 0==VersionString.compareVersions(cfg.versionMatch,targetTag.getVersion())) {
                if(match==null) {
                	match=cfg;
                } else {
                	// TODO: make sure this should never happen because there aren't multiple ways to represent a property within ID3 tag
                	// (or if this is allowed, handle better)
                	targetTag.log(Level.SEVERE, "Internal error, can't decide how to represent property "+frameProperty+"("+match.frameID+", "+cfg.frameID+")");
                	return null;
                }
            }
        }
        return match;
	}

    public static String describeId3v2Support(MediaProperty p) {
        String supp="";
        for(Id3v2FrameConfig cfg : id3v2Fields) {
            if(cfg.frameProperty == p) {
                if(supp.length()>0)
                    supp += ", ";
                supp += cfg.frameID + (cfg.versionMatch.equals("*")?"":" (" + cfg.versionMatch+")");
            }
        }
        return supp.length()>0?supp:"n/a";
    }
    
    private static void registerVorbis(String field, Class<? extends FrameNode> type, MediaProperty prop) {
        vorbisFields.add(new VorbisCommentConfig(field,type,prop));
    }
    private static void registerVorbis(String field, Class<? extends FrameNode> type, MediaProperty prop,boolean alt) {
        vorbisFields.add(new VorbisCommentConfig(field,type,prop,alt));
    }
    public static String describeVorbisCommentSupport(MediaProperty p) {
        String supp="";
        for(VorbisCommentConfig cfg : vorbisFields) {
            if(cfg.fieldProperty==p) {
                if(supp.length()>0)
                    supp+=", ";
                supp+=cfg.fieldName
                        +(cfg.alternate?" (alt)":"");
            }
        }
        return supp.length()>0?supp:"n/a";
    }
    public static VorbisCommentConfig getConfigByField(String field) {
        for(VorbisCommentConfig cfg : vorbisFields) {
            if(cfg.fieldName.equals(field)) {
                return cfg;
            }
        }
        return null;
    }
}
