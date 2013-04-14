package info.jlibrarian.stringutils;

/**
 * interface for a data object that is not a string, but can be created
 * from a string.  
 * 
 * objects implementing this interface must offer a default constructor 
 * with no parameters, and should implement toString() so that it outputs
 * text which is the same as the input value string, or is a canonicalized
 * version of it.
 * 
 * @author C. Ivan Cooper (ivan@4levity.net)
 *
 */
public interface SettableFromString {
	boolean setFromString(String value); 
}
