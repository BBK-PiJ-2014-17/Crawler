import java.io.InputStream;

/**
 * Created by Basil on 29/03/2015.
 */
public interface Reader {

    /**
     * <code>readUntil()</code>
     * <p>
     *     This method accepts an instance of an InputStream and two chars as parameters.
     *     The method consumes characters from the InputStream and stops when a character
     *     that equals either of the chars provided is encountered, ignoring case. If the
     *     character equals the first char, the value returned is true; otherwise, false
     *     is returned.
     * </p>
     *
     * @param in an InputStream to be read
     * @param ch1 a char to find in the input stream; the method will stop reading when
     *            encountered and return true.
     * @param ch2 a char to find in the input stream; the method will stop reading when
     *            encountered and return false.
     * @return true if ch1 found in InputStream
     */
    public boolean readUntil(InputStream in, char ch1, char ch2);

    /**
     * <code>skipSpace()</code>
     * <p>
     *     This method accepts an instance of an InputStream and a char as parameters.
     *     The method consumes up to and and including the first non-whitespace
     *     character from the InputStream, or up to and including the given char.
     *     If the method stopped reading characters from the InputStream because
     *     the char was encountered, it returns the smallest possible value of
     *     the class char. Otherwise, the method returns the non-whitespace character
     *     that was read.
     * </p>
     *
     * @param in an InputStream to be read
     * @param ch a char to find in the input stream; the method will stop reading when
     *           encountered and return the first non-whitespace character read, or
     *           the smallest possible value of char.
     * @return char from input stream
     */
    public char skipSpace(InputStream in, char ch);

    /**
     * <code>readString()</code>
     * <p>
     *     This method accepts an instance of an InputStream and two chars as parameters.
     *     The method consumes characters from the InputStream and stops when a character
     *     that equals either of the chars provided is encountered, ignoring case. If the
     *     character equals the first char, a String containing the characters read,
     *     in-order, is returned. Otherwise, if the terminating character equals the
     *     second character provided, the special String value null is returned.
     * </p>
     *
     * @param in an InputStream to be read
     * @param ch1 a char to find in the input stream; the method will stop reading when
     *            encountered and return a String containing the characters read.
     * @param ch2 a char to find in the input stream; the method will stop reading when
     *            encountered and return the special String value null.
     * @return a String from the input or the null String
     */
    public String readString(InputStream in, char ch1, char ch2);

}
