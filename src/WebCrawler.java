//package com.pij.crawler;

import javax.lang.model.element.VariableElement;
import javax.swing.text.ChangedCharSetException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Basil on 28/03/2015.
 */
public class WebCrawler implements Crawler {

    private char    OPEN_TAG = '<',
                    CLOSE_TAG = '>',
                    SPACE = ' ',
                    FORWARD_SLASH = '/',
                    EQUALS = '=',
                    QUOTES = '"';

    @Override
    public void crawl(URL webUrl, Connection db, String table, int depth) {

        /*
            HTML tag structure assumed to be:
            <tag class="value"></tag> or </tag>
         */

        // setup reader object
        Reader reader = new HTMLread();

        // flags for controlling crawl depth
        int curDepth = 0, maxDepth = depth;

        try {

            // urls to crawl
            URL baseURL = webUrl, curURL;

            // add base url to table
            db.createStatement().execute("insert into WC_URL values "
                                            + "("
                                            + curDepth
                                            + ", '"
                                            + baseURL.toString()
                                            + "')");

            // get URL and start crawl. Current depth zero, baseURL to be selected first
            while (curDepth < maxDepth) {

                // get URL query
                String sql = "select * from "
                        + "WC_URL "
                        + "where priority = "
                        + curDepth;

                // get URL execute
                Statement statement = db.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                // for each url found
                while(resultSet.next()) {

                    // set url and read into inputStream
                    curURL = new URL(resultSet.getString(2));
                    InputStream inputStream = curURL.openStream();

                    // read inputStream
                    while(true) {

                        // 1. find an opening tag
                        if (reader.readUntil(inputStream, OPEN_TAG, CLOSE_TAG)) {

                            StringBuilder sb = new StringBuilder();

                            //2. skip whitespace until character read or closing tag found
                            sb.append(reader.skipSpace(inputStream, CLOSE_TAG));    // single tag check?

                            // 3. read tag until next whitespace or closing tag
                            String wholeTag = reader.readString(inputStream, CLOSE_TAG, Character.MIN_VALUE);
                            sb.append(wholeTag);

                            // split string into array of attributes
                            String[] split = sb.toString().split("\\s+"); // regex split on consecutive spaces

                            // word tag is element
                            String element = split[0];

                            // anchor always goes '<a ', space
                            // end tag reached before space, tag is not an anchor
                            if (element == null) {
                                continue;
                            } else {
                                // element candidate for anchor
                                //sb.append(element);

                                // need check on script before anchor because otherwise continuing could find '<', '>' chars out of
                                // html context
                                // 4. check if script element, contains '<', '>' in wrong context
                                if (element.equals("script")){

                                    // scan until closing </script> tag
                                    while(true) {

                                        // look for consecutive '<' and '/'
                                        if (reader.readUntil(inputStream, OPEN_TAG, Character.MIN_VALUE)) {
                                            if (reader.skipSpace(inputStream, FORWARD_SLASH) != Character.MIN_VALUE) {
                                                // next character not FORWARD_SLASH, continue
                                                continue;
                                            } else {
                                                // sequence '</' found, check is script tag
                                                if (reader.readString(inputStream, CLOSE_TAG, OPEN_TAG).equals("script"))
                                                    break;

                                            }


                                        }

                                    }

                                } else if (element.equals("a")){

                                    String href;

                                    for (String attribute : split) {

                                        if (attribute.length() > 3) {

                                            if (attribute.substring(0,4).equals("href")) {
                                                href = attribute.substring(6, attribute.length() - 1);

                                                // construct URL from URL class using base and relative
                                                // add base to any relative urls
                                                if (href.substring(0, 1).equals("/")) {
                                                    if (baseURL.toString().substring(baseURL.toString().length() - 1, baseURL.toString().length()).equals("/")) {
                                                        href = baseURL.toString() + href.substring(1, href.length());
                                                    } else {
                                                        href = baseURL.toString() + href;
                                                    }
                                                }

                                                // add link to db
                                                db.createStatement().execute("insert into WC_URL values "
                                                        + "("
                                                        + (curDepth + 1)
                                                        + ", '"
                                                        + href
                                                        + "')");

                                            }

                                        }

                                    }

                                }

                            }

                        } else {

                            break;

                        }

                    }
                    
                }

                curDepth++;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean search() {
        return false;
    }

}
