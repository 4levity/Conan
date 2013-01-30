package info.jlibrarian.specialtypes;

public class Id3PictureType {
    private static String[] id3PictureTypeLabels;
    private static int defaultTypeId;
    private static Id3PictureType types[];
    static{
    	id3PictureTypeLabels=new String[] {
	        "Other",
	        "32x32 pixels 'file icon' (PNG only)",
	        "Other file icon",
	        "Cover (front)",
	        "Cover (back)",
	        "Leaflet page",
	        "Media (e.g. label side of CD)",
	        "Lead artist/lead performer/soloist",
	        "Artist/performer",
	        "Conductor",
	        "Band/Orchestra",
	        "Composer",
	        "Lyricist/text writer",
	        "Recording Location",
	        "During recording",
	        "During performance",
	        "Movie/video screen capture",
	        "A bright coloured fish",
	        "Illustration",
	        "Band/artist logotype",
	        "Publisher/Studio logotype"};
    	
    	// default is cover art
    	defaultTypeId=3;
    	
    	types=new Id3PictureType[id3PictureTypeLabels.length];
    	for(int ix=0;ix<id3PictureTypeLabels.length;ix++) {
    		types[ix]=new Id3PictureType(ix);
    	}
    }
    static public int getDefaultTypeId() {
    	return Id3PictureType.defaultTypeId;
    }
    static public Id3PictureType getDefaultType() {
    	return Id3PictureType.types[defaultTypeId];
    }
    static public int getNumTypes () {
    	return Id3PictureType.types.length;
    }

    private int typeId;
	private Id3PictureType(int typeId) {
		this.typeId = typeId;
	}
	public byte getTypeId() {
		return (byte)typeId;
	}
	public String getDescription() {
		return Id3PictureType.id3PictureTypeLabels[this.typeId];
	}
	public static Id3PictureType getId3PictureType(int typeId) {
		if(typeId < 0  ||  typeId>=getNumTypes()) {
			// TODO: reevaluate... should this return null? throw out of bounds?
			return types[defaultTypeId];
		}
		//else
		return types[typeId];
	}
	public static Id3PictureType getId3Picturetype(byte typeId) {
		return getId3PictureType((int)typeId);
	}
	@Override
	public String toString() {
		return getDescription();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + typeId;
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
		Id3PictureType other = (Id3PictureType) obj;
		if (typeId != other.typeId)
			return false;
		return true;
	}
	
	
}
