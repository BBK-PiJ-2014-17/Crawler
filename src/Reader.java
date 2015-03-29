import java.io.InputStream;

/**
 * Created by Basil on 29/03/2015.
 */
public interface Reader {

    public boolean readUntil(InputStream in, char ch1, char ch2);

    public char skipSpace(InputStream in, char ch);

    public String readString(InputStream in, char ch1, char ch2);

}
