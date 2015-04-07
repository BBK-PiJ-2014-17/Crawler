import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <code>ReaderTest</code>
 *
 * A set of test for the HTMLread implementation of the Reader interface.
 *
 * For the purpose of these tests, the URL "http://www.google.co.uk/" has been chosen. The expected opening
 * of this URL is as follows:
 *
 * <code><!doctype html><html itemscope="" itemtype="http://schema.org/WebPage" lang="en-GB"><head><meta...</code>
 *
 * NB: JUnit tests only compile with Java 6 / bytecode target 1.6, whilst main application runs Java 8.
 *
 */
public class ReaderTest {

    private HTMLread reader;            // reader object
    private InputStream inputStream;    // inputStream object

    /**
     * Setup the reader and inputStream objects for testing.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        reader = new HTMLread();    // use the HTMLRead implementation

        // MalformedURLException handling
        try {

            // setup test URL and inputStream object
            URL testURl = new URL("http://www.google.co.uk/");
            inputStream = testURl.openStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        // close inputStream
        inputStream.close();
    }

    /**
     * <code>testReadUntil</code> tests method {@link HTMLread#readUntil(InputStream, char, char) readUntil()}.
     *
     * @throws Exception
     */
    @Test
    public void testReadUntil() throws Exception {

        char ch1, ch2;                          // char arguments
        int s = 3;                              // inputStream snippet length for testing
        byte[] b;                               // byte array from inputStream to test
        String nextChars, nextCharsExpected;    // strings to check position of inputStream

        // case 1.
        // first char found. Expects true and input stream read to correct point
        ch1 = 't';                                              // char at 6th position
        ch2 = Character.MIN_VALUE;                              // default, not relevant in this test
        nextCharsExpected = "ype";                              // next 3 characters of remaining inputStream
        assertTrue(reader.readUntil(inputStream, ch1, ch2));    // readUntil expected to return true
        b = new byte[s];
        inputStream.read(b, 0, s);                              // read next 3 characters and store in byte array
        nextChars = new String(b);                              // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));        // compare strings

        // case 2.
        // second char found. Expects false and input stream read to correct point
        ch1 = Character.MIN_VALUE;                              // default, not relevant in this test
        ch2 = 'h';                                              // char at 11th position
        nextCharsExpected = "tml";                              // next 3 characters of remaining inputStream
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // readUntil expected to return false
        b = new byte[s];
        inputStream.read(b, 0, s);                              // read next 3 characters and store in byte array
        nextChars = new String(b);                              // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));        // compare strings

        // case 3
        // neither char found. Expects false and empty stream remaining
        int i;                                                  // expected return from empty inputStream
        ch1 = Character.MIN_VALUE;                              // default, not relevant in this test
        ch2 = Character.MIN_VALUE;                              // default, not relevant in this test
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // readUntil expected to return false
        assertTrue((i=inputStream.read()) == -1);               // check inputStream is empty

        // case 4
        // ignoring case

    }

    /**
     * <code>testSkipSpace</code> tests method {@link HTMLread#skipSpace(InputStream, char) skipSpace()}.
     *
     * @throws Exception
     */
    @Test
    public void testSkipSpace() throws Exception {

        char ch, expected;                      // input and expected chars
        int s = 3;                              // inputStream snippet length for testing
        byte[] b;                               // byte array from inputStream to test
        String nextChars, nextCharsExpected;    // strings to check position of inputStream

        // case 1
        // first non-whitespace found and returned
        ch = Character.MIN_VALUE;                                       // default, not relevant in this test
        expected = '<';                                                 // expected to be '<'
        nextCharsExpected = "!do";                                      // next 3 characters of remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // skipSpace returns char equal to expected
        b = new byte[s];
        inputStream.read(b, 0, s);                                      // read next 3 characters and store in byte array
        nextChars = new String(b);                                      // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));                // compare strings

        // case 2
        // input on whitespace, check skipped.
        // read inputStream until on character before whitespace
        reader.readUntil(inputStream, 'e', Character.MIN_VALUE);

        // input stream is now on whitespace and should be skipped
        expected = 'h';                                                 // expected to be 'h'
        nextCharsExpected = "tml";                                      // next 3 characters of remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // skipSpace should skip whitespace and
                                                                        // return char equal to expected
        b = new byte[s];
        inputStream.read(b, 0, s);                                      // read next 3 characters and store in byte array
        nextChars = new String(b);                                      // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));                // compare strings

        // case 3
        // char found. Expect Character.MIN_VALUE returned
        ch = '>';                                                       // next non-whitespace char
        expected = Character.MIN_VALUE;                                 // expected return value
        nextCharsExpected = "<ht";                                      // next 3 characters of remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // Character.MIN_VALUE expected
        b = new byte[s];
        inputStream.read(b, 0, s);                                      // read next 3 characters and store in byte array
        nextChars = new String(b);                                      // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));                // compare strings

    }

    /**
     * <code>testReadString</code> tests method {@link HTMLread#readString(InputStream, char, char) readString()}.
     *
     * @throws Exception
     */
    @Test
    public void testReadString() throws Exception {

        char ch1, ch2;                                      // char arguments
        int s = 3;                                          // inputStream snippet length for testing
        byte[] b;                                           // byte array from inputStream to test
        String expected, nextChars, nextCharsExpected;      // strings to check position of inputStream and return

        // case 1.
        // first char found. Expect string of chars read and input stream read to correct point
        ch1 = 'c';                                              // char at 5th position
        ch2 = Character.MIN_VALUE;                              // default, not relevant in this test
        expected = "<!do";                                      // expected string of chars read so far
        nextCharsExpected = "typ";                              // next 3 characters of remaining inputStream
        assertTrue(reader.readString(inputStream, ch1, ch2).equals(expected));      // readString expected to return
                                                                                    // string of read chars
        b = new byte[s];
        inputStream.read(b, 0, s);                              // read next 3 characters and store in byte array
        nextChars = new String(b);                              // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));        // compare strings

        // case 2
        // second char found. Expect null and input stream read to correct point
        ch1 = Character.MIN_VALUE;                              // default, not relevant in this test
        ch2 = 's';                                              // char to find
        expected = null;                                        // expect null return
        nextCharsExpected = "cop";                              // next 3 characters of remaining inputStream
        assertTrue(reader.readString(inputStream, ch1, ch2) == null);  // readString expected to return null
        b = new byte[s];
        inputStream.read(b, 0, s);                              // read next 3 characters and store in byte array
        nextChars = new String(b);                              // construct string from byte array
        assertTrue(nextChars.equals(nextCharsExpected));        // compare strings

        // case 3
        // neither char found. Expect null and empty stream remaining
        int i;                      // expected return from empty inputStream
        ch1 = Character.MIN_VALUE;  // default, not relevant in this test
        ch2 = Character.MIN_VALUE;  // default, not relevant in this test
        assertTrue(reader.readString(inputStream, ch1, ch2) == null);   // readString expected to return null
        assertTrue((i=inputStream.read()) == -1);               // check inputStream is empty

        // case 4
        // ignoring case

    }
}