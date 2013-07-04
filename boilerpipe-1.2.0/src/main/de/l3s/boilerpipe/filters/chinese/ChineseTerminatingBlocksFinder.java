/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
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

import java.util.regex.*;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;

/**
 * Finds blocks which are potentially indicating the end of an article text and
 * marks them with {@link DefaultLabels#INDICATES_END_OF_TEXT}. This can be used
 * in conjunction with a downstream {@link IgnoreBlocksAfterContentFilter}.
 * 
 * @author nkzhenhua@gmail.com
 * @see IgnoreBlocksAfterContentFilter
 */
public class ChineseTerminatingBlocksFinder implements BoilerpipeFilter {
	public static final ChineseTerminatingBlocksFinder INSTANCE = new ChineseTerminatingBlocksFinder();
	Pattern pattern = Pattern.compile("((正文|内容|文章).{0,4}(end|结束))|欢迎发表评论|分享(到)?|相关阅读|的热评日志|(进行|发表)评论");


	/**
	 * Returns the singleton instance for ChineseTerminatingBlocksFinder.
	 */
	public static ChineseTerminatingBlocksFinder getInstance() {
		return INSTANCE;
	}

	// public static long timeSpent = 0;

	public boolean process(TextDocument doc)
			throws BoilerpipeProcessingException {
		boolean changes = false;
		
		boolean end_of_content=false;
		for (TextBlock tb : doc.getTextBlocks()) {
			if( tb.getBlocktype() == 1)
			{
				final String ext_text = tb.getExtText().toLowerCase().trim();
				if( ext_text.length()> 3 && ext_text.length()<20)
				{
					Matcher mt= pattern.matcher(ext_text);
					if( mt.find())
					{
						end_of_content=true;
					}
				}
				continue;
			}
			if( end_of_content)
			{
				tb.addLabel(DefaultLabels.INDICATES_END_OF_TEXT);
				changes=true;
				continue;
			}
			final int numWords = tb.getNumWords();
			if (numWords < 15) {
				final String text = tb.getText().trim();
				final int len = text.length();
				if (len >= 3) {
					final String textLC = text.toLowerCase();
					if (textLC.startsWith("comments")
							|| startsWithNumber(textLC, len, " comments",
									" users responded in")
							|| textLC.startsWith("© reuters")
							|| textLC.startsWith("please rate this")
							|| textLC.startsWith("post a comment")
							|| textLC.contains("what you think...")
							|| textLC.contains("add your comment")
							|| textLC.contains("add comment")
							|| textLC.contains("reader views")
							|| textLC.contains("have your say")
							|| textLC.contains("reader comments")
							|| textLC.contains("rätta artikeln")
							|| textLC.equals("thanks for your comments - this feedback is now closed")
							|| textLC.startsWith("分享到")
							|| textLC.startsWith("相关阅读")
							|| textLC.contains("的热评日志")
							|| textLC.contains("发表评论")
							) {
						tb.addLabel(DefaultLabels.INDICATES_END_OF_TEXT);
						changes = true;
					}
				}
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
