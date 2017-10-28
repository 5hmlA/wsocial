package jiang.wsocial.emoji.parser;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import jiang.wsocial.emoji.ColorTextSpan;
import jiang.wsocial.emoji.EmojiManager;

import static jiang.wsocial.emoji.EmojiManager.LOG;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public class FaceWorldEmojiParser extends EmoticonParser {

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
        //是否匹配到
        boolean whetherMatches = false;
        if(after>0) {
            //解析输入的当前变化输入的文字
            int changedEnd = start+after;
            Editable editableText = editText.getEditableText();
            String changedStr = text.toString().substring(start, changedEnd);
            //找到表情映射表中的 颜文字
            String faceWorld = EmojiManager.sEmojiMapping.get(changedStr);//无法转换连续 颜文字
            if(!TextUtils.isEmpty(faceWorld) && start+faceWorld.length()<=editableText.length() && !TextUtils
                    .isEmpty(editableText)) {
                LOG("颜文字表情解析==成功");
                whetherMatches = true;
                editableText.setSpan(new ColorTextSpan(), start, start+faceWorld.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return whetherMatches;
    }
}
