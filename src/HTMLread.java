import java.io.InputStream;

/**
 * Created by Basil on 28/03/2015.
 */
public class HTMLread implements Reader {
    @Override
    public boolean readUntil(InputStream in, char ch1, char ch2) {
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
