import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
    - does inputStream to string consume inputStream
    - check case
    - check multiple types of whitespace
 */

public class ReaderTest {

    private HTMLread reader;
    private InputStream inputStream;

    @Before
    public void setUp() throws Exception {

        reader = new HTMLread();

        try {
            URL testURl = new URL("http://www.google.co.uk/");
            inputStream = testURl.openStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testReadUntil() throws Exception {

        // check case

        /*
            ** expected beginning of inputStream = '<!doctype html>'
         */
        char ch1, ch2;
        String expected, remainder;

        // first char found expects true and input stream read to point
        ch1 = 't';                  // char at 6th position
        ch2 = Character.MIN_VALUE;  // default, not relevant in this test
        expected = "ype html>";     // first 9 characters of expected remaining inputStream
        assertTrue(reader.readUntil(inputStream, ch1, ch2));    // readUntil expected to return true
        remainder = inputStream.toString().substring(0, 9);    // get portion of remaining input stream
        assertTrue(remainder.equals(expected));                 // compare strings

        // second char found expects false and input stream read to point
        ch1 = Character.MIN_VALUE;  // default, not relevant in this test
        ch2 = 'h';                  // char at 11th position
        expected = "tml>";          // first 5 characters of expected remaining inputStream
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // readUntil expected to return false
        remainder = inputStream.toString().substring(0, 5);     // get portion of remaining input stream
        assertTrue(remainder.equals(expected));                 // compare strings

        // neither char found expects false and empty stream remaining
        int i;                      // expected return from empty inputStream
        ch1 = Character.MIN_VALUE;  // default, not relevant in this test
        ch2 = Character.MIN_VALUE;  // default, not relevant in this test
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // readUntil expected to return false
        assertTrue((i=inputStream.read()) == -1);               // check inputStream is empty

    }

    @Test
    public void testSkipSpace() throws Exception {

        // test with various types of whitespace


        /*
            ** expected beginning of inputStream = '<!doctype html>'
         */
        char ch, expected;
        String remainder, remainderExpected;

        // first non-whitespace found returned
        ch = Character.MIN_VALUE;               // default, not relevant in this test
        expected = '<';                         // expected to be '<'
        remainderExpected = "!doctype html>";   // first 14 characters of expected remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // skipSpace returns char equal to expected
        remainder = inputStream.toString().substring(0, 14);            // get portion of remaining input stream
        assertTrue(remainder.equals(remainderExpected));                // compare strings

        // check whitespace skipped
        // read inputStream until on character before whitespace
        reader.readUntil(inputStream, 'e', Character.MIN_VALUE);

        // input stream is now on whitespace and should be skipped
        expected = 'h';                         // expected to be 'h'
        remainderExpected = "tml>";             // first 4 characters of expected remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // skipSpace should skip whitespace and
                                                                        // return char equal to expected
        remainder = inputStream.toString().substring(0, 4);             // get portion of remaining input stream
        assertTrue(remainder.equals(remainderExpected));                // compare strings

        // check car found and Character.MIN_VALUE returned
        ch = 'm';                               // 2nd letter of what remains
        expected = Character.MIN_VALUE;         // expected return value
        remainderExpected = "l>";               // first 2 characters of expected remaining inputStream
        assertTrue(reader.skipSpace(inputStream, ch) == expected);      // Character.MIN_VALUE expected
        remainder = inputStream.toString().substring(0, 2);             // get portion of remaining input stream
        assertTrue(remainder.equals(remainderExpected));                // compare strings

    }

    @Test
    public void testReadString() throws Exception {

        // check case

        /*
            ** expected beginning of inputStream = '<!doctype html>'
         */
        char ch1, ch2;
        String expected, remainder, remainderExpected;

        // first char found expects string of chars read returned
        ch1 = 'c';                          // char at 6th position
        ch2 = Character.MIN_VALUE;          // default, not relevant in this test
        expected = "<!do";                  // expected chars read
        remainderExpected = "type html>";   // first 10 characters of remaining inputStream
        assertTrue(reader.readString(inputStream, ch1, ch2).equals(expected));      // readString expected to return
                                                                                    // string of read chars
        remainder = inputStream.toString().substring(0, 10);            // get portion of remaining input stream
        assertTrue(remainder.equals(remainderExpected));                // compare strings

        // second char found expects null and input stream read to point
        ch1 = Character.MIN_VALUE;  // default, not relevant in this test
        ch2 = 'l';                  // char at 9th position
        expected = null;            // expect null return
        remainderExpected = ">";    // next character of inputStream
        assertTrue(reader.readString(inputStream, ch1, ch2).equals(expected));      // readString expected to return null
        remainder = String.valueOf(inputStream.read());                         // get next byte as String
        assertTrue(remainder.equals(remainderExpected));                        // compare strings

        // neither char found expects null and empty stream remaining
        int i;                      // expected return from empty inputStream
        ch1 = Character.MIN_VALUE;  // default, not relevant in this test
        ch2 = Character.MIN_VALUE;  // default, not relevant in this test
        assertTrue(reader.readString(inputStream, ch1, ch2).equals(expected));   // readUntil expected to return false
        assertTrue((i=inputStream.read()) == -1);               // check inputStream is empty

    }
}