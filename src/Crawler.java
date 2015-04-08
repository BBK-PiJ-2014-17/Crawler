import java.net.URL;
import java.sql.Connection;

/**
 * Created by Basil on 29/03/2015.
 *
 * The <code>Crawler</code> interface contains methods for constructing a database of
 * URL links given a base URL.
 *
 */
public interface Crawler {

    /**
     * <code>crawl()</code>
     *
     * This method takes a base URL, a connection to a database and the table to use, and the limits
     * on how long to crawl for; depth, max. The method opens a HTTP connection to the base URL and
     * scans the webpage for other URL links. These are then stored in a temporary database until they
     * are processed by the search function also present in this class. If the search terms are met,
     * the URL is moved to the main results table.
     *
     * Each webpage parsed is stored in the database with a priority indicator that relates to the
     * depth of the URL from the base. The base URL will have priority 0, any link found in the base
     * URL will have priority 1, any link found in the URLs with priority 1 will have priority 2, and
     * so on.
     *
     * The crawl method will iteratively cycle through all links in the database until either of the
     * limiting criteria are met. The depth is the maximum numbers of levels from the base URL the
     * crawl should go. Max is the maximum number of links that should be processed.
     *
     * @param webUrl    a base URL from which to start the crawl
     * @param db        a connection to a database to store the results
     * @param table     the database table to store results in
     * @param depth     the maximum depth from the base URL the crawl should go
     * @param max       the maximum number of links that should be processed
     */
    void crawl(URL webUrl, Connection db, String table, int depth, int max);

    /**
     * <code>search()</code>
     *
     * The search method scans the given URL for certain criteria and returns true if passed.
     *
     * By default, the method returns true on all links unless overridden in the implementation.
     *
     * @param webUrl    a URL to scan
     * @return          true if criteria met
     */
    boolean search(URL webUrl);

}
