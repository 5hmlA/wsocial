package jiang.wsocial.emoji.parser;

import android.text.Editable;
import android.widget.TextView;

import static jiang.wsocial.emoji.EmojiManager.LOG;
import static jiang.wsocial.emoji.help.EmojiDisplay.picEmojiDisplay;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public class PicEmojiParser extends EmoticonParser {

    /**
     * @param editText
     * @param text
     * @param start
     * @param lengthBefore
     *         删除了多少个字符
     * @param after
     *         增加了几个字符
     */
    @Override
    public boolean parserEmoji(TextView editText, CharSequence text, int start, int lengthBefore, int after){
        LOG("图片表情解析==");
        //是否匹配到
        boolean whetherMatches = false;
        if(after>0) {
            //解析输入的当前变化输入的文字
            int changedEnd = start+after;
            Editable editableText = editText.getEditableText();
            String changedStr = text.toString().substring(start, changedEnd);
            //将转换后的emoji占位符 正常显示
            whetherMatches = picEmojiDisplay(changedStr, editableText, start);
//            Matcher picEmojiMatcher = CUSPIC_EMOJI_PATTEN.matcher(changedStr);
//            if(picEmojiMatcher != null && !TextUtils.isEmpty(editableText)) {
//                while(picEmojiMatcher.find()) {
//                    EmoticonSpan picEmojiSpan = getPicEmojiSpan(0, picEmojiMatcher.group());
//                    if(picEmojiSpan != null) {
//                        LOG("图片表情解析==成功");
//                        whetherMatches = true;
//                        int startMatcher = start+picEmojiMatcher.start();
//                        int endMatcher = start+picEmojiMatcher.end();
//                        clearSpan(editableText, startMatcher, endMatcher);
//                        editableText.setSpan(picEmojiSpan, startMatcher, endMatcher, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//            }
        }
        return whetherMatches;
    }
}
