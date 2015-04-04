import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <code>Crawler</code> Tests
 *
 * Initial analysis carried out on URL: http://www.google.co.uk/. Expected first crawl results below
 * , 22 URLs expected.
 *
 *  PRIORITY    |               URL |
 *  0           | http://www.google.co.uk/
 *  1           | http://www.google.co.uk/imghp?hl=en&tab=wi |
 *  1           | http://maps.google.co.uk/maps?hl=en&tab=wl |
 *  1           | https://play.google.com/?hl=en&tab=w8 |
 *  1           | http://www.youtube.com/?gl=GB&tab=w1 |
 *  1           | http://news.google.co.uk/nwshp?hl=en&tab=wn |
 *  1           | https://mail.google.com/mail/?tab=wm |
 *  1           | https://drive.google.com/?tab=wo |
 *  1           | http://www.google.co.uk/intl/en/options/ |
 *  1           | http://www.google.co.uk/history/optout?hl=en |
 *  1           | http://www.google.co.uk/preferences?hl=en |
 *  1           | https://accounts.google.com/ServiceLogin?hl=en&continue=http://www.google.co.uk/ |
 *  1           | http://www.google.co.uk/chrome/index.html?hl=en&amp;brand=CHNG&amp;utm_source=en-hpp&amp;utm_medium=hpp&amp;utm_campaign=en |
 *  1           | http://www.google.co.uk/advanced_search?hl=en-GB&amp;authuser=0 |
 *  1           | http://www.google.co.uk/language_tools?hl=en-GB&amp;authuser=0 |
 *  1           | http://www.google.co.uk/intl/en/ads/ |
 *  1           | http://www.google.co.uk/services/ |
 *  1           | https://plus.google.com/103583604759580854844 |
 *  1           | http://www.google.co.uk/intl/en/about.html |
 *  1           | http://www.google.co.uk/setprefdomain?prefdom=US&amp;sig=0_gEWOoF34e1XplPwakWM8syH4sPE%3D |
 *  1           | http://www.google.co.uk/intl/en/policies/privacy/ |
 *  1           | http://www.google.co.uk/intl/en/policies/terms/ |
 *
 * First set of tests written for depth one
 *
 */

public class CrawlerTest {

    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:testdb;create=true";
    public static final String MAIN = "WC_URL";
    public static final String STAGING = "WC_URL_STAGING";
    private Connection connection;
    private Crawler wc;

    @Before
    public void setUp() throws Exception {

        wc = new WebCrawler();

        try {

            // setup db
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(JDBC_URL);

            //connection.createStatement().execute("drop table WC_URL");
            connection.createStatement().execute("create table "
                                                    + MAIN
                                                    + "(priority int, url varchar(2084))");   // max url length

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @After
    public void tearDown() throws Exception {

        try {

            connection.createStatement().execute("drop table WC_URL");
            if (connection != null) connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCrawl() throws Exception {

        try {

            // crawl web address
            wc.crawl(new URL("http://www.google.co.uk/"), connection, MAIN, 1);

            // expected number of URLS
            int expected = 21;
            List<URL> urls = new ArrayList<URL>(expected);

            // check results

            // 1. check count = 21
            String sql = "select count(*) from "
                            + MAIN;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            assertTrue(resultSet.getInt(1) == expected);

            // 2. spot check results
            sql = "select * from "
                    + MAIN;

            resultSet = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            // push URLs into list
            while(resultSet.next()) {
                urls.add(new URL(resultSet.getString(1)));  // url column
            }

            // check list or URLs
            URL third = new URL("http://maps.google.co.uk/maps?hl=en&tab=wl");
            URL twelfth = new URL("https://accounts.google.com/ServiceLogin?hl=en&continue=http://www.google.co.uk/");
            URL twentieth = new URL("http://www.google.co.uk/intl/en/policies/privacy/");

            assertTrue(urls.get(2).equals(third));
            assertTrue(urls.get(2).equals(twelfth));
            assertTrue(urls.get(2).equals(twentieth));

            // clean database and close connection
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSearch() throws Exception {

    }
}