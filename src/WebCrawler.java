//package com.pij.crawler;

import javax.lang.model.element.VariableElement;
import javax.swing.text.ChangedCharSetException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        Reader reader = new HTMLread();

        try {
            URL myURL = webUrl;
            InputStream inputStream = myURL.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            // find anchors '<a'
            // avoid script tags

            while(true) {

                // 1. find opening tag
                if (reader.readUntil(inputStream, OPEN_TAG, CLOSE_TAG)) {

                    //StringBuilder sb = new StringBuilder("" + OPEN_TAG);
                    StringBuilder sb = new StringBuilder();

                    // 2. skip whitespace until character read or closing tag found
                    sb.append(reader.skipSpace(inputStream, CLOSE_TAG));    // single tag check?

                    // 3. read tag until next whitespace or closing tag
                    String element = reader.readString(inputStream, SPACE, CLOSE_TAG);

                    // anchor always goes '<a ', space
                    // end tag reached before space, tag is not an anchor
                    if (element == null) {
                        continue;
                    } else {
                        // element candidate for anchor
                        sb.append(element);

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

                                // is anchor, get href
                                while(true) {

                                    // scan attributes
                                    if (reader.skipSpace(inputStream, 'h') != Character.MIN_VALUE) {
                                        // next character not FORWARD_SLASH, continue
                                        continue;
                                    } else {
                                        // href attribute found
                                        if (reader.readString(inputStream, QUOTES, CLOSE_TAG).equals("ref="))
                                            break;
                                    }

                                }

                                // read href
                                href = reader.readString(inputStream, QUOTES, CLOSE_TAG);

                        } else {

                            // scan to closing bracket
                            reader.readUntil(inputStream, CLOSE_TAG, Character.MIN_VALUE);

                        }

                        sb.append("" + CLOSE_TAG);

                    }



                    System.out.println(sb.toString());

                    //char c = Character.MAX_VALUE;

                    //while(c != Character.MIN_VALUE) {

                      //  c = reader.skipSpace(inputStream, '>');

                    //}

                    // check if anchor
                    //if (reader.skipSpace(inputStream, '>') == 'a') {
                        //StringBuilder sb = new StringBuilder("<a ");
                        //sb.append(reader.readString(inputStream, '>', '<'));
                        //System.out.println(sb.toString());
                        //break;
                    //}
                }

                //break;

            }
/*

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
*/

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean search() {
        return false;
    }

}
