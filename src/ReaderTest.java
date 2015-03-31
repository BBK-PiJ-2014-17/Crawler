import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        // expected beginning of inputStream = '<!doctype html>'

        char ch1, ch2;
        String expected, remainder;

        // first char found expects true and input stream read to point
        ch1 = 't';                  // char at 6th position
        ch2 = Character.MIN_VALUE;  // default, not relevant
        expected = "type html>";    // first 10 characters of expected remaining inputStream
        assertTrue(reader.readUntil(inputStream, ch1, ch2));    // reader expected to return true
        remainder = inputStream.toString().substring(0, 10);    // get portion of remaining input stream
        assertTrue(remainder.equals(expected));                 // compare strings

        // second char found expects false and input stream read to point
        ch1 = Character.MIN_VALUE;  // default, not relevant
        ch2 = 'h';                  // char at 11th position
        expected = "html>";         // first 6 characters of expected remaining inputStream
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // reader expected to return false
        remainder = inputStream.toString().substring(0, 6);     // get portion of remaining input stream
        assertTrue(remainder.equals(expected));                 // compare strings


        // neither char found expects false and empty stream remaining
        int i;                      // expected return from empty inputStream
        ch1 = Character.MIN_VALUE;  // default, not relevant
        ch2 = Character.MIN_VALUE;  // default, not relevant
        assertTrue(!reader.readUntil(inputStream, ch1, ch2));   // reader expected to return false
        assertTrue((i=inputStream.read()) == -1);               // check inputStream is empty

    }

    @Test
    public void testSkipSpace() throws Exception {

    }

    @Test
    public void testReadString() throws Exception {

    }
}