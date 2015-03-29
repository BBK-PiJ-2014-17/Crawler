/**
 * Created by Basil on 29/03/2015.
 */
public interface Crawler {

    public void crawl(String webUrl,
                      String dbType,
                      String dbUrl,
                      String dbUsername,
                      String dbPassword);

    public boolean search();

}
