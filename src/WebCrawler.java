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
                    FORWARD_SLASH = '/';

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

                    StringBuilder sb = new StringBuilder("" + OPEN_TAG);

                    // 2. skip whitespace until character read or closing tag found
                    sb.append(reader.skipSpace(inputStream, CLOSE_TAG));

                    // 3. read tag until next whitespace
                    sb.append(reader.readString(inputStream, SPACE, OPEN_TAG));

                    sb.append("" + CLOSE_TAG);

                    // 4. skip script element, contains '<', '>' in wrong context
                    if (sb.toString().substring(1,sb.length() - 1).equals("script")){

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

                break;

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
