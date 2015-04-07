import java.net.URL;
import java.sql.Connection;

/**
 * Created by Basil on 29/03/2015.
 */
public interface Crawler {

    void crawl(URL webUrl,Connection db, String table, int depth);

    boolean search(URL webUrl);
}
