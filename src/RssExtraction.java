
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.*;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class RssExtraction {
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd");
    public static HashMap<String,String> getBlogItem(String url,boolean latest) {
    	
    	HashMap feeds=new HashMap<String,String>();
        URL feedUrl = null;
        try {
            feedUrl = new URL(url);
        } catch (MalformedURLException e) {
        }
        FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
        FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
        SyndFeed feed = null;
        try {
            feed = fetcher.retrieveFeed(feedUrl);
            String title=new String(feed.getTitle().getBytes("iso8859-1"), "utf-8");
            Date latestdate=feed.getPublishedDate();
            System.out.println(feedUrl + " has a title: " + new String(feed.getTitle().getBytes("iso8859-1"), "utf-8") 
            + " and contains "     + feed.getEntries().size() + " entries.");
            for (Iterator iter = feed.getEntries().iterator(); iter.hasNext(); ) {
                SyndEntry entry = (SyndEntry) iter.next();

                Date entryTime=entry.getPublishedDate();
                if( entryTime == null)
                {
                	entryTime=entry.getUpdatedDate();
                }
                if(latest && entryTime != null)
                {
                	String et=FORMAT.format(entryTime);
                	String pt=FORMAT.format(latestdate);
                	if( pt.compareTo(et) <= 0)
                	{
                    	feeds.put(entry.getLink(),entry.getTitle());
                        System.out.println("<a href=" + entry.getLink() + ">" + entry.getTitle() + "</a>[" + entry.getPublishedDate() + "]"
                        		);
                	}
                }else
                {
                	feeds.put(entry.getLink(),entry.getTitle());
                    System.out.println("<a href=" + entry.getLink() + ">" + entry.getTitle() + "</a>[" + entry.getPublishedDate() + "]"
                    		);
                }
            }
        } catch (Exception e) {
//            log.error("Rss fetching FetcherException error,cause:" + e.getMessage());
        	System.out.println("Rss fetching FetcherException error,cause:" + e.getMessage());

        }
        return feeds;
    }
	/**
	 * @param args
	 * https://code.google.com/p/boilerpipe/issues/list
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(RssExtraction.getBlogItem("http://blog.sina.com.cn/rss/1412969690.xml",true));
	}

}
