import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Basil on 28/03/2015.
 *
 * This is an implementation of the interface <code>Reader</code>.
 */
public class HTMLread implements Reader {

    /**
     * <code>readUntil</code>
     * {@inheritDoc}
     */
    @Override
    public boolean readUntil(InputStream in, char ch1, char ch2) {

        int b;  // byte from inputStream

        // IOException handling
        try {

            // while there is something to read from the inputStream
            while((b=in.read()) != -1) {

                // check if the byte equals the first argument
                if ((char) b == ch1)
                    return true;    // return true

                // check if the byte equals the second argument
                if ((char) b == ch2)
                    return false;   // return false
            }

        } catch (IOException e) {
            System.out.println("Error reading inputStream in HTMLRead#readUntil");
            e.printStackTrace();
        }

        // if neither argument matched any of the inputStream, return false
        return false;
    }

    /**
     * <code>skipSpace</code>
     * {@inheritDoc}
     */
    @Override
    public char skipSpace(InputStream in, char ch) {

        int b;  // byte from inputStream

        // IOException handling
        try {

            // while there is something to read from the inputStream
            while((b=in.read()) != -1) {

                // test the byte for all known whitespace characters
                if (!Character.isWhitespace((char) b)) {

                    // if the byte is not whitespace and matches the argument
                    if ((char) b == ch) {
                        return Character.MIN_VALUE; // return char min value
                    } else {
                        return (char) b;            // otherwise, return the byte read from inputStream
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // if no non-whitespace characters are found, return char min value
        return Character.MIN_VALUE;

    }

    /**
     * <code>readString</code>
     * {@inheritDoc}
     */
    @Override
    public String readString(InputStream in, char ch1, char ch2) {

        StringBuilder sb = new StringBuilder(); // stringBuilder to compile return string
        int b;  // byte from inputStream

        // IOException handling
        try {

            // while there is something to read from the inputStream
            while((b=in.read()) != -1) {

                // check if the byte equals the first argument
                if ((char) b == ch1)
                    return sb.toString();   // return bytes read so far as string

                // check if the byte equals the second argument
                if ((char) b == ch2)
                    return null;            // return null

                sb.append((char) b);        // append byte to stringBuilder for return

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // if neither argument found, return null
        return null;
    }

}
