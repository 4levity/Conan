package info.jlibrarian.stringutils;

/*
 * interface for an object that is not a string but can be created
 * from a string.  
 * 
 * objects implementing this interface should offer a default constructor 
 * with no parameters and should implement toString() so that it outputs
 * something similar to the input. 
 */
public interface SettableFromString {
	boolean setFromString(String value); 
}
