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
/db-derby-10.11.1.1-lib.zip

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

The Reader interface defines three methods for parsing an InputStream.

- readUntil(InputStream in, char ch1, char ch2)
- skipSpace(InputStream in, char ch)
- readString(InputStream in, char ch1, char ch2)

These are implemented in the HTMLRead class. The three methods are used in combination by the crawler to parse an
HTML URL.

See interface and implementation javadoc for full details.

#### _The Crawler_

The Crawler interface defines two methods.

- crawl(URL webUrl, Connection db, String table, int depth, int max)
- search()

The crawl method uses the Reader utilities to parse HTML URLs for links to other URLs, and stores the links found in the
given database and table. The crawler will continue to search through links until the max depth or number of links are
reached. By default, these are level 2 and max links 20.

The search method is applied to each URL parsed and tests for a certain criteria. This methods is setup as a default
method to return true on all pages, but could be implemented for something more specific, e.g. searching for malicious
javascript in a page.

See interface and implementation javadoc for full details.

#### _Tests_

There are a set of tests for the Reader and a set of tests for the Crawler, both based on the URL http://www.google.co.uk/.

The Reader tests parse this document and test the expected returns. The Crawler tests expect certain links to be found.

See interface and implementation javadoc for full details.

