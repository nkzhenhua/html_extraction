import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
public class rss_feed {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
        SyndFeedInput input = new SyndFeedInput();
        //System.out.println(warmedFeed);
        SyndFeed feed = input.build(new XmlReader(new URL("http://blog.sina.com.cn/rss/1654715720.xml")));

        // Iterate through feed items, adding a footer each item
        Iterator entryIter = feed.getEntries().iterator();
        while (entryIter.hasNext()) {
            SyndEntry entry = (SyndEntry) entryIter.next();
            System.out.println(entry.getPublishedDate());
            System.out.println(entry.getTitle());
            //System.out.println(entry.getDescription());
//            System.out.println(entry.getAuthor());
            System.out.println(entry.getLink());
        }
            
	}

}
