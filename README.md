# **Crawler**
## CW5 - Web Crawler

### _Summary_

This Web Crawler application parses HTML URLs to locate links found in the href attribute of anchor tags, <a href="link"></a>.

These links are temporarily saved in a staging table to be parsed if required. Each page that is parsed is also searched for a
certain criteria and only on passing this search is committed to the main table. More details on the crawl and search methods in
the Crawler section below.

This application has been developed around the java Apache Derby database implementation. The required library can be
found at the following location:

https://db.apache.org/derby/releases/release-10.11.1.1.cgi
db-derby-10.11.1.1-lib.zip

However, the main WebCrawler implementation should also work with any SQL database that is provided. See the Crawler
section for details.

The structure of this applications is as follows:

- Crawler interface, Crawler.java
- Crawler implementation, WebCrawler.java
- Crawler main application, WebCrawlerMain.java
- Reader interface, Reader.java
- Reader implementation, HTMLRead.java
- Crawler unit tests, CrawlerTest.java
- Reader unit tests, ReaderTest.java

To demonstrate the use of the WebCrawler class, the WebCrawlerMain application has been included. In this class, a
temporary runtime database is created, using Apache Derby. A connection to that database is established and a main table
for storing the results of the crawl is created. A URL to crawl is then declared and given to the WebCrawler class,
along with a reference to the connection to the database and table to write to, and the crawler parses the given URL
until the given parameters are reached.

The crawler temporarily creates a staging table for storage during processing, but ultimately writes all results to the
table provided by the user of the class. The WebCrawlerMain application then outputs the results stored in the table to
the console.

Finally, the main table is dropped and the database connection is closed.

#### _Reader Utilities_



#### _The Crawler_

#### _Tests_

