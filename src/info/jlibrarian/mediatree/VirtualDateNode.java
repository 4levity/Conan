package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.PropertySearchResults;
import info.jlibrarian.propertytree.PropertySearchResults.Result;
import info.jlibrarian.propertytree.PropertyTree;
import info.jlibrarian.specialtypes.VariablePrecisionTime;

/* 
 * defines a virtual value node representing some sort of date; 
 * value is defined by querying parent for date component values and assembling.
 * here is where we will define how date values are interpreted as release/recording etc.
 *
 * 
 * On "date" fields: The concept of "Recording Year" is different than "Release Year". So what?
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
 * 		It consists of an ISO 8601 variable precision date optionally followed by space & text. Additional
 * 		text indicates the meaning of the date, e.g. "2004-01-21 released" or "2003-11-13 (recorded date)"
 *		If no qualifier is given after the date, one might reasonably assume it means "release date".
 *		
 *		Note we may see alternate field names for date such as "RELEASE" or "RELEASEDATE" which contain a 
 *		date likely w/o qualifier; these dates should be contextualized by their field name, rather than
 *		by text following the date. 
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
 * 		PRO: Tags created by compliant 2.4 readers will be interpreted correctly. But "common"
 * 			behavior of using TYE/TYER for release date in 2.2/2.3 is respected.
 * 		CON: not compliant with 2.2/2.3 spec. Tags upconverted to 2.4 by compliant 2.4 taggers
 * 			will sometimes be misinterpreted since TDRC will contain release date.  
 * 
 * 3. "TYE/TYER is release date, TDRC might be": In ALL ID3v2 versions, interpret the frames 
 * 		TYE/TYER/TDRC as release date (common usage), although the spec says otherwise. Except in 
 * 		ID3v2.4 if BOTH the TDRC and TDRL frames are present, or if the tag
 * 		was created by Conan, interpret according to spec. If writing v2.4, use TDRL for release date
 * 		(and make sure that Conan will somehow recognize that the tag was created by Conan.)
 * 
 * 		PRO: Simple approach that reflects expected behavior by "most" users that there is
 * 			just one date associated with each track. Tags converted to 2.4 or created in compliant
 * 			2.4 taggers will probably be interpreted correctly*.
 * 		CON: not compliant with ANY ID3v2 spec! *=if a file is tagged with recording date only and
 * 			no release date in v2.4, Conan will assume it was mistagged due to upconversion and
 * 			will interpret the date as a release date.
 *
 * 
 * Solution for now:
 * 		- Every tag gets a "smart" virtual node for recording date and release date
 * 		- value-generation behavior of these nodes could be configurable
 */
public class VirtualDateNode extends PropertyTree<MediaProperty> {
	
	public VirtualDateNode(MediaProperty property,
			PropertyTree<MediaProperty> parent) {
		super(property, parent);
	}

	@Override
	public void setValue(Object o) {
		throw new UnsupportedOperationException("cannot yet set virtual "+this.getNodeProperty().getShortName());
	}

	@Override
	public Object getValue() {
		// find and assemble date appropriately depending on property at this node and type of parent
		// TODO: behavior should be configurable via registry (see comment at top of file)
		
		// VirtualDateNode may only be used for datatype VariablePrecisionTime
		VariablePrecisionTime result=null;
		
		MediaProperty parent=this.getParent().getNodeProperty();

		if(parent.isTypeOf(MediaProperty.ID3V2TAG)) {
			if(this.getNodeProperty().isTypeOf(MediaProperty.RELEASE_DATE)) {
				result = getId3v2ReleaseDate();
			} else if(this.getNodeProperty().isTypeOf(MediaProperty.RECORDING_DATE)) {
				result = getId3v2RecordingDate();
			}
		} else if(parent.isTypeOf(MediaProperty.VORBISCOMMENTBLOCK)) {
			if(this.getNodeProperty().isTypeOf(MediaProperty.RELEASE_DATE)) {
				result = getVorbisReleaseDate();
			} else if(this.getNodeProperty().isTypeOf(MediaProperty.RECORDING_DATE)) {
				result = getVorbisRecordingDate();
			}
		}
		// TODO: handle ORIGINAL DATE, other types of dates, user configurable, default/unknown?
		
		return result;
	}

	private VariablePrecisionTime getId3v2ReleaseDate() {
		/* id3v1, v2.2, v2.3 */
		VariablePrecisionTime id3year_yyyy=(VariablePrecisionTime)(getParent().queryBestResult(MediaProperty.ID3_YEAR));
		Integer id3v2date_mmdd=(Integer)getParent().queryBestResult(MediaProperty.ID3V2_DATE);
		Integer id3v2time_hhmm=(Integer)getParent().queryBestResult(MediaProperty.ID3V2_TIME);
		String id3v2recordingdates_text=(String)getParent().queryBestResult(MediaProperty.ID3V2_RECORDINGDATES);
		/* id3v2.4+ */
		VariablePrecisionTime id3v2recordingdate=(VariablePrecisionTime)getParent().queryBestResult(MediaProperty.ID3V2_RECORDINGDATE);
		VariablePrecisionTime id3v2releasedate=(VariablePrecisionTime)getParent().queryBestResult(MediaProperty.ID3V2_RELEASEDATE);
		
		VariablePrecisionTime result=null;
		
		if(id3v2recordingdate != null && id3v2releasedate != null) {
			result = id3v2releasedate;
		} else if (id3v2recordingdate != null) {
			result = id3v2recordingdate;
		} else if (id3year_yyyy != null) {
			result = new VariablePrecisionTime(id3year_yyyy);
			if(id3v2date_mmdd != null) {
				result.set_MMDD(id3v2date_mmdd.toString());
				if(id3v2time_hhmm != null) {
					result.set_hhmm(id3v2time_hhmm.toString());
				}
			}
			if(id3v2recordingdates_text != null) {
				result.setExtraData(id3v2recordingdates_text);
			}
		}
		
		return result;
	}

	private VariablePrecisionTime getId3v2RecordingDate() {
		return (VariablePrecisionTime)getParent().queryBestResult(MediaProperty.ID3V2_RECORDINGDATE);
	}

	private VariablePrecisionTime getVorbisReleaseDate() {
		return vorbisDate(false);
	}
	private VariablePrecisionTime getVorbisRecordingDate() {
		return vorbisDate(true);
	}


	// recordingDate = false : look for release date
	private VariablePrecisionTime vorbisDate(boolean recordingDate) {
		PropertySearchResults<MediaProperty> query=getParent().query(MediaProperty.VORBISFIELD_DATE);
		if(query==null)
			return null;
		
		VariablePrecisionTime dateUnspecified=null;
		VariablePrecisionTime dateRelease=null;
		VariablePrecisionTime dateRecorded=null;
		VariablePrecisionTime dateOther=null;
		
		// TODO: PropertySearchResults should give us an iterator maybe?
		// go through all vorbis date fields and find best ones
		int numResults=query.getNumResults();
		for(int ix=0;ix<numResults && (dateUnspecified==null || dateRelease==null || dateRecorded==null);ix++) {
			PropertySearchResults<MediaProperty>.Result candidate=query.getResult(ix);
			VariablePrecisionTime t =(VariablePrecisionTime) candidate.getValue();
			String extra=t.getExtraData();
			if(extra==null && dateUnspecified==null) {
				dateUnspecified=t;
			} else if(extra!=null) {
				// TODO: better guessing at meaning of qualifer? language support?
				extra=extra.toLowerCase();
				boolean isRelease=extra.indexOf("release")>=0;
				boolean isRecorded=extra.indexOf("recorded")>=0;
				
				if(dateRelease==null && isRelease) {
					dateRelease=t;
				} else if(dateRecorded==null && !isRelease && isRecorded) {
					dateRecorded=t;
				} else if(dateOther==null && !isRelease && !isRecorded) {
					dateOther=t;
				}
			}
		}
		
		if(recordingDate) {
			return dateRecorded;
		} // else guess at release date
		if(dateRelease != null) {
			return dateRelease;
		}
		if(dateUnspecified != null) {
			return dateUnspecified;
		}
		return dateOther;
	}

}
