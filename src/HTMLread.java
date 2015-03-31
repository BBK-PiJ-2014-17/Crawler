import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Basil on 28/03/2015.
 */
public class HTMLread implements Reader {

    /* I N T E R F A C E   M E T H O D S */

    /**
     * <code>readUntil</code>
     * {@inheritDoc}
     * <p>
     *
     * </p>
     */
    @Override
    public boolean readUntil(InputStream in, char ch1, char ch2) {

        int b;

        try {
            while((b=in.read()) != -1) {

                if ((char) b == ch1)
                    return true;

                if ((char) b == ch2)
                    return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public char skipSpace(InputStream in, char ch) {

        int b;

        try {
            while((b=in.read()) != -1) {

                // test all whitespace

                if (!Character.isWhitespace((char) b)) {
                    if ((char) b == ch) {
                        return Character.MIN_VALUE;
                    } else {
                        return (char) b;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Character.MIN_VALUE;

    }

    @Override
    public String readString(InputStream in, char ch1, char ch2) {

        StringBuilder sb = new StringBuilder();
        int b;

        try {
            while((b=in.read()) != -1) {

                char c = (char) b;
                sb.append(c);

                if (c == ch1)
                    return sb.toString();

                if (c == ch2)
                    return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
