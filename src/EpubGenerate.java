import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import de.l3s.boilerpipe.extractors.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;
import de.l3s.boilerpipe.document.*;

public class EpubGenerate {

	private HashMap<String,String> epub_source;
	private String epub_title;
	private String epub_author;

	public EpubGenerate(HashMap<String,String> urls, String title, String author) {
		epub_source = urls;
		epub_title = title;
		epub_author = author;
	}
	public EpubGenerate(String title, String author)
	{
		epub_source=new HashMap<String,String>();
		epub_title = title;
		epub_author = author;
	}
	public void addItem(String blog_item,String blog_title )
	{
		if( blog_item!=null && !blog_item.equals("") )
		{
			epub_source.put(blog_item, blog_title);
		}
	}
    public String getHtmlText(TextDocument doc) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>")
        .append(doc.getTitle()).append("</title></head><body>");
        int text_length=0;
        for (TextBlock block : doc.getTextBlocks()) {
            if(!block.isContent()) {
               continue;
            }
            text_length+=block.getText().trim().length();
            if( block.getText().trim().length() > 1 && text_length <30)
            {
				sb.append("&nbsp;");
				sb.append(block.getText());
				sb.append("<&nbsp;");
			} else {
                sb.append("<p>");
                sb.append(block.getText());
                sb.append("</p>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

	public boolean generateEpubBook(String outFileName) {
		if (outFileName == null || outFileName.equals(""))
			return false;

		Book book = new Book();
		book.getMetadata().addTitle(epub_title);
		book.getMetadata().addAuthor(new Author(epub_author, "zzh"));

		int resource_id = 1;
		if( epub_source.size() ==0)
			return false;
		for (String blog_url : epub_source.keySet()) {
			try {
				URL url = new URL(blog_url);

				String content = ChineseBlogExtractor.INSTANCE.getHtmlText(url);
				TextDocument tdoc = ChineseBlogExtractor.INSTANCE.getDoc();
				book.addSection(
						epub_source.get(blog_url) == null ? epub_source.get(blog_url) : tdoc
								.getTitle(),
						new Resource(getHtmlText(tdoc).getBytes("utf-8"), "html"
								+ resource_id + ".html"));
				resource_id++;
				for (String rurl : tdoc.getAllResource().keySet()) {
					String turl = tdoc.getAllResource().get(rurl);
					book.addResource(getEpubResource(turl, rurl,resource_id++));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		try {
			EpubWriter epubWriter = new EpubWriter();
			epubWriter.write(book, new FileOutputStream(outFileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Resource getEpubResource(String urlstr, String res_ref,
			int res_id) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String content_type = null;
		try {
			URL url = new URL(urlstr);
			URLConnection conn = url.openConnection();
			content_type = conn.getContentType();
			InputStream in = conn.getInputStream();

			byte[] buf = new byte[4096];
			int r;
			while ((r = in.read(buf)) != -1) {
				bos.write(buf, 0, r);
			}
			in.close();
		} catch (Exception e) {
		}
		MediaType mt = MediatypeService.getMediaTypeByName(content_type);
		return new Resource(res_id + "", bos.toByteArray(), res_ref, mt);
	}

	/**
	 * @param args
	 *            https://code.google.com/p/boilerpipe/issues/list
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		final URL url = new URL(
				"http://blog.sina.com.cn/s/blog_4b49c4be0102e492.html?tj=1");

		// This can also be done in one line:
		String content = ChineseBlogExtractor.INSTANCE.getHtmlText(url);
		TextDocument tdoc = ChineseBlogExtractor.INSTANCE.getDoc();
		System.out.println(content);
		try {
			Book book = new Book();
			book.getMetadata().addTitle("KindleBlogRss");
			book.getMetadata().addAuthor(new Author("Joe", "Tester"));
			book.addSection(tdoc.getTitle(),
					new Resource(content.getBytes("utf-8"), "html1.html"));
			int id = 0;
			for (String rurl : tdoc.getAllResource().keySet()) {
				String turl = tdoc.getAllResource().get(rurl);
				book.addResource(EpubGenerate.getEpubResource(turl, rurl, id));
				id++;
			}
			EpubWriter epubWriter = new EpubWriter();
			epubWriter.write(book, new FileOutputStream(
					"/Users/zzh/test1_book3.epub"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
