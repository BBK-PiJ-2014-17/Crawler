import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

/**
 * Created by Basil on 28/03/2015.
 *
 * This is an implementation of the interface <code>Crawler</code>.
 *
 * The WebCrawler implements mehods <code>crawl()</code> and <code>search()</code>.
 *
 * The main parsing is carries out in internal method <code>parseUrlAndGetLinks()</code>, which makes use of the
 * Reader class.
 *
 */
public class WebCrawler implements Crawler {

    private char    OPEN_TAG = '<',
                    CLOSE_TAG = '>',
                    FORWARD_SLASH = '/';

    Connection db;
    URL baseURL;

    /**
     * <code>crawl()</code>
     * {@inheritDoc}
     *
     * This implementation scans URLs for the HTML anchor tag <a></a> and locates the 'href'
     * attribute. All HTML tags are assumed to be of the following forms:
     *
     * <element attribute="value"></element>
     *
     *      or
     *
     * </element>
     *
     * Special attention is paid to the <script> element as this contains Javascript that uses the diamond brackets
     * outside of the HTML context. All content from an opening script tag is skipped.
     *
     * @param webUrl    a base URL from which to start the crawl
     * @param db        a connection to a database to store the results
     * @param table     the database table to store results in
     * @param depth     the maximum depth from the base URL the crawl should go
     * @param max       the maximum number of links that should be processed
     */
    @Override
    public void crawl(URL webUrl, Connection db, String table, int depth, int max) {

        this.db = db;

        // setup database table names
        String mainTable = table;
        String stagingTable = table + "_STAGING";

        // initialise database querying elements
        ResultSet resultSet = null;
        Statement statement = null;

        // initialise URLs to crawl
        URL curURL;
        baseURL = webUrl;

        // flags for controlling crawl duration
        int curDepth = 0, maxDepth = depth, curLinks = 0, maxLinks = max;

        // SQLException handling
        try {

            // 1. create temporary staging table for initial scan
            sqlCreateTable(stagingTable);

            // 2. add base url to staging table with priority 0 (curDepth)
            sqlAddRecordToUrlTable(stagingTable, curDepth, baseURL.toString());

            // 3. start crawl
            // loop until crawler has parsed the maximum number of links, or reached the maximum depth.
            // get URL and start crawl. Current depth zero, baseURL to be selected first
            while (curDepth < maxDepth && curLinks < maxLinks) {

                // on each iteration, get all URLs from the staging table with the current priority level.
                // For each URL, scan for links and store these with priority incremented by 1.

                // 4. get URLs
                resultSet = sqlGetUrlsFromTable(stagingTable, curDepth);

                try {

                    // 5. for each URL of the current priority
                    // carry out scan for links
                    while(resultSet.next() && curLinks < maxLinks) {

                        // 6. set url and read into inputStream
                        String s = null;

                        try {
                            s = resultSet.getString(2);
                            curURL = new URL(s);       // second column in table
                        } catch (MalformedURLException e) {
                            System.out.println("Unable to create URL from string '" + s + "'.");
                            System.out.println(e.getMessage());
                            continue;
                        } catch (SQLException e) {
                            System.out.println("Unable to access resultSet.");
                            System.out.println(e.getMessage());
                            continue;
                        }

                        InputStream inputStream;
                        try {
                            inputStream = curURL.openStream();
                        } catch (IOException e) {
                            System.out.println("Unable to open connection to URL '" + curURL.toString() + "'.");
                            System.out.println(e.getMessage());
                            continue;
                        }

                        // 7. open and parse inputStream
                        parseUrlAndGetLinks(stagingTable, inputStream, curDepth);

                        // 8. conduct search on current URL before storing in main database table
                        if(search(curURL)) {

                            // if search criteria met..
                            ResultSet dupResultSet;

                            // 9. check if the link exists in the table already
                            dupResultSet = sqlGetUrlFromTable(mainTable, curDepth, curURL.toString());

                            // 10. if no record found, add to main table
                            if (dupResultSet == null) {
                                continue;
                            } else {
                                dupResultSet.next();
                            }

                            if(!(dupResultSet.getInt(1) > 0)) {

                                // 11. add link to db
                                sqlAddRecordToUrlTable(mainTable, curDepth, curURL.toString());
                                curLinks++;

                            }

                        }

                    }

                } catch (SQLException e) {
                    System.out.println("Unable to open resultSet.");
                    System.out.println(e.getMessage());
                    continue;
                }

                // 12. all links at current depth visited, increment to next level
                curDepth++;

            }

            // 13. clean database and close connection
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            db.createStatement().execute("drop table " + stagingTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean search(URL webUrl) {
        return true;
    }

    /* I N T E R N A L   M E T H O D S */

    private void sqlCreateTable(String table) {

        String sql;

        sql =   "create table "
                + table
                + "(priority int, url varchar(2084))";  // max url length

        try {
            db.createStatement().execute(sql);
        } catch (SQLException e) {
            System.out.println("Unable to create temporary staging table " + table);
            System.out.println(e.getMessage());
        }

    }

    private void sqlAddRecordToUrlTable(String table, int depth, String url) {

        String sql;

        sql =   "insert into "
                + table
                + " values ("
                + depth
                + ", '"
                + url
                + "')";

        try {
            db.createStatement().execute(sql);
        } catch (SQLException e) {
            System.out.println("Unable to add URL: '" + url + "' with priority "
                    + depth + ", to " + table + ".");
            System.out.println(e.getMessage());
        }

    }

    private ResultSet sqlGetUrlFromTable(String table, int depth, String url) {

        String sql;
        Statement statement;
        ResultSet resultSet;

        sql = "select count(*) from "
                + table
                + " where url = '"
                + url
                + "'";

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Unable to retrieve URL " + url + " with priority " + depth + ", from "
                    + table + ".");
            System.out.println(e.getMessage());
            return null;
        }

        return resultSet;

    }

    private ResultSet sqlGetUrlsFromTable(String table, int depth) {

        String sql;
        Statement statement;
        ResultSet resultSet;

        sql = "select * from "
                + table
                + " where priority = "
                + depth;

        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Unable to retrieve URLs with priority " + depth + ", from "
                    + table + ".");
            System.out.println(e.getMessage());
            return null;
        }

        return resultSet;

    }

    private void parseUrlAndGetLinks(String table, InputStream input, int depth) {

        // setup reader object
        Reader reader = new HTMLread();

        // loop until no remaining closing tags found, i.e. end of HTML.
        while(true) {

            // for each inputStream, the loop continues until all tags read
            // on each cycle, the next opening tag is found and the element is
            // compared to the elements <a> and <script>.
            // if <a> is found, the link is processed.
            // if <script> is found, the inputStream is read until the matching closing tag
            // is found. This is to avoid diamond brackets outside of HTML context.
            // if element that does not match either of these is found, the inputStream continues.

            // find the next opening tag
            // e.g. '<'
            if (reader.readUntil(input, OPEN_TAG, CLOSE_TAG)) {

                StringBuilder sb = new StringBuilder();

                // skip any whitespace until character read or closing tag found
                // e.g. 'a'
                sb.append(reader.skipSpace(input, CLOSE_TAG));    // store first non-whitespace
                // character found

                // read element until closing tag
                // e.g. 'a class="1" href="www.example.co.uk">'
                sb.append(reader.readString(input, CLOSE_TAG, Character.MIN_VALUE));  // store element

                // split element into array of attributes
                // e.g. {'a', 'class="1"', 'href="www.example.co.uk'}
                String[] split = sb.toString().split("\\s+"); // regex split on consecutive spaces

                // element tag is first in split
                // e.g. 'a'
                String element = split[0];

                // check if element equals <script> tag
                if (element.equals("script")) {

                    // if the element is the <script> element, the following lines of data must
                    // be skipped to avoid parsing diamond brackets in a non-HTML context

                    // consume inputStream until closing </script> tag
                    while(true) {

                        // closing tag signalled by consecutive '<' and '/'
                        if (reader.readUntil(input, OPEN_TAG, Character.MIN_VALUE)) {
                            if (reader.skipSpace(input, FORWARD_SLASH) != Character.MIN_VALUE) {
                                // next character not FORWARD_SLASH, continue
                                continue;   // continue loop
                            } else {
                                // sequence '</' found, check is script tag
                                if (reader.readString(input, CLOSE_TAG, OPEN_TAG).equals("script"))
                                    break;  // break loop
                            }

                        }

                    }

                    // check if element equals <a> tag
                } else if (element.equals("a")) {

                    // if yes, an anchor has been found, scan the attributes for the link

                    String href;    // link to be found

                    // for each attribute found in the anchor tag
                    for (String attribute : split) {

                        // check the length could possibly be href to avoid
                        // error when constructing substring
                        if (attribute.length() > 3) {

                            try {

                                // check is href
                                if (attribute.substring(0,4).equals("href")) {

                                    // extract link from href
                                    // e.g. href="www.example.co.uk" -> www.example.co.uk
                                    href = attribute.substring(6, attribute.length() - 1);

                                    // add base url to any relative urls
                                    if (href.substring(0, 1).equals("/")) {
                                        if (baseURL.toString().substring(baseURL.toString().length() - 1, baseURL.toString().length()).equals("/")) {
                                            href = baseURL.toString() + href.substring(1, href.length());
                                        } else {
                                            href = baseURL.toString() + href;
                                        }
                                    }

                                    // add link to temporary staging database table
                                    sqlAddRecordToUrlTable(table, depth + 1, href);

                                }

                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("Unable to perform substring on '" + attribute + "'.");
                                System.out.println(e.getMessage());
                                continue;
                            }

                        }

                    }

                }

            } else {
                break;  // no remaining tags found, break loop
            }

        }

    }

}
