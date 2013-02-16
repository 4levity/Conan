package info.jlibrarian.mediatree; /* Original source code (c) 2013 C. Ivan Cooper. Licensed under GPLv3, see file COPYING for terms. */

import info.jlibrarian.propertytree.Property;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MediaFileUtil {
    public static long convert32bitsToUnsignedInt(byte[] d) {
    	return convert32bitsToUnsignedInt(d,0);
    }
    public static long convert32bitsToUnsignedInt(byte[] d,int offset) {
        return ((0xff & d[offset+0])<<24) + ((0xff & d[offset+1])<<16) 
        		+ ((0xff & d[offset+2])<<8) + (0xff & d[offset+3]);
    }
    public static byte[] convertUnsignedIntTo32bits(long i) {
    	byte[] b=new byte[4];
    	b[0]=(byte)(((int)(i>>24))&0xFF);
    	b[1]=(byte)(((int)(i>>16))&0xFF);
    	b[2]=(byte)(((int)(i>>8))&0xFF);
    	b[3]=(byte)(((int)(i))&0xFF);
    	return b;
    }
    public static byte[] convertUnsignedIntTo24bits(long i) {
    	byte[] b=new byte[3];
    	b[0]=(byte)(((int)(i>>16))&0xFF);
    	b[1]=(byte)(((int)(i>>8))&0xFF);
    	b[2]=(byte)(((int)(i))&0xFF);
    	return b;
    }
    public static long convert32bitsLittleEndianToUnsignedInt(byte[] d) {
        return ((0xff & d[3])<<24) + ((0xff & d[2])<<16) + ((0xff & d[1])<<8) + (0xff & d[0]);
    }

    public static long read32bitUnsignedInt(RandomAccessFile f) throws IOException {
        Integer d0,d1,d2,d3;
        d0=0xff & read_sure(f);
        d1=0xff & read_sure(f);
        d2=0xff & read_sure(f);
        d3=0xff & read_sure(f);
        return (d0<<24) + (d1<<16) + (d2<<8) + d3;
    }

    public static boolean isLink(File f) throws IOException {
        if (f.getName().toUpperCase().endsWith(".LNK"))
            return true;
        return ! (f.getAbsolutePath().equals(f.getCanonicalPath()));
        // TODO: this doesn't seem to work on relative paths
    }
    
    public static long read32bitLittleEndianUnsignedInt(RandomAccessFile f) throws IOException {
        Integer d0,d1,d2,d3;
        d0=0xff & read_sure(f);
        d1=0xff & read_sure(f);
        d2=0xff & read_sure(f);
        d3=0xff & read_sure(f);
        return (long)(d3<<24) + (d2<<16) + (d1<<8) + d0;
    }
    public static int read_sure(RandomAccessFile f) throws IOException {
        int r=( 0xff & f.read());
        if(r<0) {
            throw new IOException("unexpected end of file, offset "+Long.toString(f.getFilePointer()));
        }
        //else
        return r;                    
    }
    public static void read_sure(RandomAccessFile f,byte[] buf) throws IOException {
        int r=f.read(buf);
        if(r != buf.length) {
            throw new IOException("unexpected end of file, offset "+Long.toString(f.getFilePointer()));
        }
    }
    public static String getFileExtension(File f) {
        String filename=f.getName();
        return (filename.lastIndexOf(".")==-1)?"":
            filename.substring(filename.lastIndexOf(".")+1,filename.length())
                .toLowerCase();
    }

    private static class Sorter implements Comparator<Property> {
        public int compare(Property arg0, Property arg1) {
            return arg0.getName().compareTo(arg1.getName());
        }
    }
    public static String supportReport(boolean abbrev) {
        ArrayList<Property> props= new ArrayList<Property>();
        for(Property p : MediaProperty.values() ) {
            props.add(p);
        }
        Collections.sort(props, new Sorter());
        String fmt;
        if(abbrev) {
        	fmt="%-40.40s|%-38.38s\n";
        } else {
        	fmt="%-40.40s|%-43.43s|%-20.20s|%-25.25s|%-33.33s\n";
        }
        
        String r=String.format(fmt,"Property","Description","Data type","Id3v2 field(s)","Vorbis field(s)");
        for(Property p : props ) {
            r+=String.format(fmt,
                    p.getName(),
                    p.getDescription(),
                    p.getDataType().getSimpleName(),
                    Registry.describeId3v2Support(p),
                    Registry.describeVorbisCommentSupport(p));
        }
        return r;
    }
}
