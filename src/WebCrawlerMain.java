import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Created by Basil on 02/04/2015.
 *
 * This application demonstrates the use of the WebCrawler class.
 *
 * Here, the java Apache Derby database implementation is used, but the WebCrawler class would work with
 * any SQL database.
 *
 * download - https://db.apache.org/derby/releases/release-10.11.1.1.cgi
 * file     - db-derby-10.11.1.1-lib.zip
 *
 */
public class WebCrawlerMain {

    // database setup information
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:testdb;create=true";
    private Connection connection;

    // the crawler object
    private Crawler wc;

    // main application
    public static void main(String[] args) {
        new WebCrawlerMain().launch();
    }

    // processing
    private void launch() {

        // setup web crawler
        wc = new WebCrawler();

        try {

            // setup db
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(JDBC_URL);

            String mainTable = "WC_URL";

            /* for testing ONLY. When an error has occurred, a table may need to be dropped manually.
            //String stagingTable = "WC_URL_STAGING";
            //connection.createStatement().execute("drop table WC_URL");
            //connection.createStatement().execute("drop table WC_URL_STAGING"); */

            // create table for results
            connection.createStatement().execute("create table WC_URL(priority int, url varchar(2084))");   // max url length

            // crawl web address
            wc.crawl(new URL("http://www.google.co.uk/"), connection, mainTable, 2, 20);
            //wc.crawl(new URL("http://tfl.gov.uk/"), connection, mainTable, 2, 20);
            //wc.crawl(new URL("https://www.facebook.com/"), connection, mainTable, 2, 1);

            // check results
            String sql = "select * from WC_URL";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            // print column headers
            for (int i = 1; i <= columnCount; i++)
                System.out.format("%20s", resultSetMetaData.getColumnName(i) + " | ");

            // print table
            while(resultSet.next()) {
                System.out.println("");
                for (int i = 1; i <= columnCount; i++)
                    System.out.print(resultSet.getString(i) + " | ");
                    //System.out.format("%20s", resultSet.getString(i) + " | ");
            }

            // clean database and close connection
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            connection.createStatement().execute("drop table WC_URL");
            if (connection != null) connection.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
