
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
 
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
	private static Logger log = LoggerFactory.getLogger(RssExtraction.class);
    public static SyndFeed getSyndFeed(String url) {
        URL feedUrl = null;
        try {
            feedUrl = new URL(url);
        } catch (MalformedURLException e) {
            log.error("fetche url is not available,cause:" + e.getMessage());
        }
        FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
        FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
         
        fetcher.addFetcherEventListener(new FetcherListener() {
            @Override
            public void fetcherEvent(FetcherEvent event) {
                String eventType = event.getEventType();
                if (log.isDebugEnabled()) {
                    if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
                        log.debug("\\tEVENT: Feed Polled. URL = " + event.getUrlString());
                    } else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                        log.debug("\\tEVENT: Feed Retrieved. URL = " + event.getUrlString());
                    } else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                        log.debug("\\tEVENT: Feed Unchanged. URL = " + event.getUrlString());
                    }
                }
            }
        });
        SyndFeed sfd = null;
        try {
            sfd = fetcher.retrieveFeed(feedUrl);
        } catch (IllegalArgumentException e) {
            log.error("Rss fetching IllegalArgumentException error,cause:" + e.getMessage());
        } catch (IOException e) {
            log.error("Rss fetching IOException error,cause:" + e.getMessage());
        } catch (FeedException e) {
            log.error("Rss fetching FeedException error,cause:" + e.getMessage());
        } catch (FetcherException e) {
            log.error("Rss fetching FetcherException error,cause:" + e.getMessage());
        }
        return sfd;
    }
	/**
	 * @param args
	 * https://code.google.com/p/boilerpipe/issues/list
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}

}
