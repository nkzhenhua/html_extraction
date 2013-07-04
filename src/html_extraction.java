import java.net.URL;

import de.l3s.boilerpipe.extractors.*;
public class html_extraction {

	/**
	 * @param args
	 * https://code.google.com/p/boilerpipe/issues/list
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//        final URL url = new URL("http://abcnews.go.com/Business/11-dumb-things-email/story?id=19523155");
//      final URL url = new URL("http://blog.sina.com.cn/s/blog_71a56b7d0101ktba.html?tj=1");
		String html="<html> <title> this is my first </title> <body> first body text <div> <font size=\"4\"> font size set </font> </div> haha haha</body></html>";
      final URL url = new URL("http://blog.qq.com/qzone/622002994/1372648834.htm");

        // This can also be done in one line:
        System.out.println(ArticleExtractor.INSTANCE.getText(url));
        System.out.println("*****************");
 //       System.out.println(KeepEverythingWithMinKWordsExtractor.getText(url));
	}

}
