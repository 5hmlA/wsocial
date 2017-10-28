package jiang.wsocial.emoji.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jiang.wsocial.emoji.GoogleEmoji;

/**
 * @another 江祖赟
 * @date 2017/9/29 0029.
 */
public class RefuseEmojiInputFilter implements InputFilter {

    public static Pattern pattern = Pattern
            .compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE);

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
        Matcher m = GoogleEmoji.getEmojiMatcher(source.toString());
        if(m != null) {
            while(m.find()) {
                return "";
            }
        }
        return null;//返回原数据
    }
}
