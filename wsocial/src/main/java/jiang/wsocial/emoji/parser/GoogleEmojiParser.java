package jiang.wsocial.emoji.parser;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.regex.Matcher;

import jiang.wsocial.emoji.EmojiManager;
import jiang.wsocial.emoji.GoogleEmoji;

import static jiang.wsocial.emoji.EmojiManager.LOG;
import static jiang.wsocial.emoji.GoogleEmoji.emoji2HexPlaceholder;
import static jiang.wsocial.emoji.help.EmojiDisplay.googleEmojiDisplay;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public class GoogleEmojiParser extends EmoticonParser {

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
        LOG("google表情解析==");
        //是否匹配到
        boolean whetherMatches = false;
        if(after>0) {
            //解析输入的当前变化输入的文字
            int changedEnd = start+after;
            Editable editableText = editText.getEditableText();
            String changedStr = text.toString().substring(start, changedEnd);
            if(!EmojiManager.sFaceWorlds.contains(changedStr)) {
                //排除颜文字
                Matcher emojiMatcher = GoogleEmoji.getEmojiMatcher(changedStr);
                if(emojiMatcher != null) {
                    //不是颜文字 草 颜文字部分会被匹配成emoji
                    while(emojiMatcher.find()) {
                        //先将所有的 原始Google表情转换
                        String emojiHex = emoji2HexPlaceholder(emojiMatcher.group());
                        //部分颜文字会被匹配进来
                        if(!TextUtils.isEmpty(emojiHex)) {
                            LOG("google表情解析==发现原始谷歌表情进行转换");
                            whetherMatches = true;
                            changedStr = changedStr.replace(emojiMatcher.group(), emojiHex);
                        }
                    }
                    if(whetherMatches && !TextUtils.isEmpty(editableText))
                    //替换掉 原始Google表情
                    {//把输入的 原始Google表情 转为 表情占位符
                        editableText.replace(start, changedEnd, changedStr);
                        return whetherMatches;
                    }
                }
                //将转换后的emoji占位符 正常显示
                whetherMatches = googleEmojiDisplay(changedStr, editableText, start);
                //            Matcher cusEmojiMatcher = GOOGLE_EMOJI_PATTEN.matcher(changedStr);
                //            if(cusEmojiMatcher != null) {
                //                while(cusEmojiMatcher.find()) {
                //                    String emojiStr = EmojiManager.sEmojiMapping.get(cusEmojiMatcher.group());
                //                    if(!TextUtils.isEmpty(emojiStr)) {
                //                        LOG("google表情解析==显示谷歌表情");
                //                        int matcherStart = start+cusEmojiMatcher.start();
                //                        int matcherEnd = start+cusEmojiMatcher.end();
                //                        clearSpan(editableText, matcherStart, matcherEnd);
                //                        whetherMatches = true;
                //                        EmojiReplaceSpan emojiReplaceSpan = new EmojiReplaceSpan(emojiStr);
                //                        if(!TextUtils.isEmpty(editableText)) {
                //                            editableText.setSpan(emojiReplaceSpan, matcherStart, matcherEnd,
                //                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //                        }else {
                //                            ( (SpannableString)editText.getText() ).setSpan(emojiReplaceSpan, matcherStart, matcherEnd,
                //                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //                        }
                //                    }
                //                }
                //            }
            }
        }
        return whetherMatches;
    }
}
