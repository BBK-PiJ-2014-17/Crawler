import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Created by Basil on 02/04/2015.
 */
public class WebCrawlerMain {

    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:testdb;create=true";
    private Connection connection;
    private Crawler wc;

    public static void main(String[] args) {
        new WebCrawlerMain().launch();
    }

    private void launch() {

        // setup web crawler
        wc = new WebCrawler();

        try {

            // setup db
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(JDBC_URL);

            String mainTable = "WC_URL";
            String stagingTable = "WC_URL_STAGING";
            //connection.createStatement().execute("drop table WC_URL");
            connection.createStatement().execute("create table WC_URL(priority int, url varchar(20))");

            // crawl web address
            wc.crawl(new URL("http://www.google.co.uk/"), connection, mainTable, 3);

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
                    System.out.format("%20s", resultSet.getString(i) + " | ");
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
