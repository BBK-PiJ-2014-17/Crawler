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
        return 0;
    }

    @Override
    public String readString(InputStream in, char ch1, char ch2) {
        return null;
    }
}
