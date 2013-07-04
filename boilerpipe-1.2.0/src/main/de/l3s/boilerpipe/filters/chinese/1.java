/**
 * boilerpipe
 *
 * Copyright (c) 2013 nkzhenhua
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.filters.chinese;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;
import java.util.regex.*;
/**
 * Finds blocks which are potentially indicating the start of an article text and
 * marks them with {@link DefaultLabels#INDICATES_START_OF_TEXT}. This can be used
 * in conjunction with a downstream {@link IgnoreBlocksAfterContentFilter}.
 * 
 * @author nkzhenhua@gmail.com
 * @see IgnoreBlocksAfterContentFilter
 */
public class ChineseContentBlocksFinder implements BoilerpipeFilter {
	public static final ChineseContentBlocksFinder INSTANCE = new ChineseContentBlocksFinder();
	private static final Pattern pattern_start = Pattern.compile("((正文|文章|内容).{0,4}(begin|开始|start))");

	/**
	 * Returns the singleton instance for ChineseContentBlocksFinder.
	 */
	public static ChineseContentBlocksFinder getInstance() {
		return INSTANCE;
	}

	public static void main(String argv[])
	{
		if(ChineseContentBlocksFinder.getInstance().content_end("正文内容 begin"))
		{
			System.out.println("find it");
		}else
		{
			System.out.println("can't find it");
		}
	}
	public boolean content_end(String exttext)
	{
		if( exttext.length() > 3 && exttext.length()<10)
		{
			if( exttext.equals("正文")|| exttext.equals("正文内容"))
			{
				return true;
			}
			Matcher m = pattern_start.matcher(exttext);
			if( m.find())
			{
				return true;
			}
		}
		return false;
	}
	public boolean process(TextDocument doc)
			throws BoilerpipeProcessingException {
		boolean changes = false;
        /*
         * some content end tag is in html content like:many tag in sina.com 
         */
		boolean content_begin=false;
		for (TextBlock tb : doc.getTextBlocks()) {
			int blocktype=tb.getBlocktype();
			if( blocktype == 1)
			{
				//this block is comment block, get the info from comment
				final String exttext=tb.getExtText().toLowerCase().trim();
				if( content_end(exttext))
				{
					content_begin=true;
				}
				continue;// next block
			}
			if( tb.hasLabel(DefaultLabels.INDICATES_END_OF_TEXT) )
			{
				return changes;
			}
			if(content_begin)
			{
				tb.addLabel(DefaultLabels.MIGHT_BE_CONTENT);
				changes = true;
				continue;
			}
		}
		// timeSpent += System.currentTimeMillis() - t;
		return changes;
	}

	/**
	 * Checks whether the given text t starts with a sequence of digits,
	 * followed by one of the given strings.
	 * 
	 * @param t
	 *            The text to examine
	 * @param len
	 *            The length of the text to examine
	 * @param str
	 *            Any strings that may follow the digits.
	 * @return true if at least one combination matches
	 */
	private static boolean startsWithNumber(final String t, final int len,
			final String... str) {
		int j = 0;
		while (j < len && isDigit(t.charAt(j))) {
			j++;
		}
		if (j != 0) {
			for (String s : str) {
				if (t.startsWith(s, j)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isDigit(final char c) {
		return c >= '0' && c <= '9';
	}

}
