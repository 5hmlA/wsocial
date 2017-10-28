package jiang.wsocial.emoji.help;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jiang.wsocial.emoji.EmojiManager;
import jiang.wsocial.emoji.GoogleEmoji;
import jiang.wsocial.emoji.span.EmojiReplaceSpan;
import jiang.wsocial.emoji.span.EmoticonSpan;

import static jiang.wsocial.Utils.getTextHeight;
import static jiang.wsocial.emoji.EmojiManager.CUSPIC_EMOJI_PATTEN;
import static jiang.wsocial.emoji.EmojiManager.GOOGLE_EMOJI_PATTEN;
import static jiang.wsocial.emoji.EmojiManager.LOG;
import static jiang.wsocial.emoji.parser.EmoticonParser.getPicEmojiSpan;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public class EmojiDisplay {
    public static void emojiDisplay(Context context, Editable text, String emojiHexPlaceholder, float emojiSize, int start, int end){
        //根据占位符 寻找表情图片/emoji
        String emoji = EmojiManager.sEmojiMapping.get(emojiHexPlaceholder);
        if(emoji != null) {
            text.replace(start, end, emoji);
        }
    }

    public static void picEmojiDisplay(Context context, Editable text, String emojiHexPlaceholder, float emojiSize, int start, int end){
        SpannableString spannableString = new SpannableString(emojiHexPlaceholder);
        //根据占位符 寻找表情图片/emoji
        Drawable drawable = getDrawableAsset(EmojiManager.sEmojiMapping.get(emojiHexPlaceholder));
        if(drawable != null) {
            //重点 不设置bounds图片不会显示 坑啊
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            jiang.wsocial.emoji.span.EmoticonSpan imageSpan = new jiang.wsocial.emoji.span.EmoticonSpan(drawable);
            spannableString.setSpan(imageSpan, 0, emojiHexPlaceholder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.replace(start, end, spannableString);
        }
    }

    /**
     * 颜文字 添加颜色
     */
    public void getStringSpan(String s){
        //        int start = mEditor.getSelectionStart();
        //        int end = mEditor.getSelectionEnd();
        //        Editable editableText = mEditor.getEditableText();
        //
        //        editableText.replace(start, end, s);
        //        editableText.setSpan(new ColorTextSpan(), start, start+s.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * {}转为图片
     */
    public EmoticonSpan getSmiley(String emojiHolder){
        // 映射表
        Drawable drawableAsset = getDrawableAsset(EmojiManager.sEmojiMapping.get(emojiHolder));
        drawableAsset.invalidateSelf();
        return new EmoticonSpan(drawableAsset);
        //        int start = mEditor.getSelectionStart();
        //        int end = mEditor.getSelectionEnd();
        //        Editable editableText = mEditor.getEditableText();
        //        // Insert the emoticon.
        //        editableText.replace(start, end, s);
        //        editableText.setSpan(emoticonSpan, start, start+s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //        return emoticonSpan;
    }


    public static Drawable getDrawableAsset(String path){
        //这里才是重点
        AssetManager assetManager = EmojiManager.mApplicationContext.getAssets();
        InputStream in = null;
        try {
            in = assetManager.open(path);
            return Drawable.createFromStream(in, null);
        }catch(Exception e) {
            Log.e("xxx", Log.getStackTraceString(e));
        }finally {
            //if (in!=null)
        }
        return null;
    }


    public static CharSequence buildEmojiString(TextView textView, String smojiStr){
        float textHeight = getTextHeight(textView);
        SpannableString spannableString = new SpannableString(smojiStr);
        //        Pattern pattern = Pattern.compile("\\{.*?\\}");
        //        Pattern pattern = Pattern.compile("\\[[^\\]]+\\]");
        //        Matcher matcher = pattern.matcher(smojiStr);
        Matcher matcher = Pattern.compile("\\{.*?\\}").matcher(smojiStr);
        while(matcher.find()) {
            String value = matcher.group();
            Drawable drawable = getDrawableAsset(EmojiManager.sEmojiMapping.get(value));
            float ratio = drawable.getIntrinsicWidth()*1.0f/drawable.getIntrinsicHeight();
            //重点 不设置bounds图片不会显示 坑啊
            if(textView != null) {
                drawable.setBounds(0, 0, (int)( textHeight*ratio ), (int)( textHeight*ratio ));
            }else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            EmoticonSpan imageSpan = new EmoticonSpan(drawable);
            spannableString.setSpan(imageSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //todo 颜文字
        if(textView != null) {
            textView.setText(spannableString);
        }
        return spannableString;
    }

    public static CharSequence buildEmojiString(CharSequence smojiStr){
        return buildEmojiString(0, smojiStr);
    }

    public static CharSequence buildEmojiString(float textHeight, CharSequence smojiStr){
        SpannableString spannableString = new SpannableString(smojiStr);
        picEmojiDisplay(smojiStr, spannableString, 0);
        googleEmojiDisplay(smojiStr, spannableString, 0);
        return spannableString;
    }

    public static boolean googleEmojiDisplay(CharSequence emojiPlaceHolder, Spannable spannableString, int start){
        if(TextUtils.isEmpty(emojiPlaceHolder) || TextUtils.isEmpty(spannableString)) {
            return false;
        }
        //是否匹配到
        boolean whetherMatches = false;
        //google表情
        //将转换后的emoji占位符 正常显示
        Matcher cusEmojiMatcher = GOOGLE_EMOJI_PATTEN.matcher(emojiPlaceHolder);
        if(cusEmojiMatcher != null) {
            while(cusEmojiMatcher.find()) {
                //会匹配到颜文字
                String group = cusEmojiMatcher.group();
                String emojiStr = EmojiManager.sEmojiMapping.get(group);
                //寻找表情
                if(TextUtils.isEmpty(emojiStr)) {
                    //没有整理的其他谷歌表情
                    //取出 进制数
                    emojiStr = GoogleEmoji.newGoogleEmoji(group.substring(2, group.length()-2));
                    if(!GoogleEmoji.getEmojiMatcher(emojiStr).find()) {
                        //转换的表情如果不匹配emoji表情 默认为@
                        emojiStr = "\uD83C\uDE1A";
                    }
                }
                if(!TextUtils.isEmpty(emojiStr)) {
                    whetherMatches = true;
                    LOG("google表情解析==显示谷歌表情");
                    int matcherStart = start+cusEmojiMatcher.start();
                    int matcherEnd = start+cusEmojiMatcher.end();
                    EmojiReplaceSpan emojiReplaceSpan = new EmojiReplaceSpan(emojiStr);
                    spannableString
                            .setSpan(emojiReplaceSpan, matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return whetherMatches;
    }

    public static boolean picEmojiDisplay(CharSequence emojiPlaceholder, Spannable spannableString, int start){
        if(TextUtils.isEmpty(emojiPlaceholder) || TextUtils.isEmpty(spannableString)) {
            return false;
        }
        boolean whetherMatches = false;
        //将转换后的emoji占位符 正常显示
        Matcher picEmojiMatcher = CUSPIC_EMOJI_PATTEN.matcher(emojiPlaceholder);
        if(picEmojiMatcher != null) {
            while(picEmojiMatcher.find()) {
                EmoticonSpan picEmojiSpan = getPicEmojiSpan(0, picEmojiMatcher.group());
                if(picEmojiSpan != null) {
                    LOG("图片表情解析==成功");
                    whetherMatches = true;
                    int startMatcher = start+picEmojiMatcher.start();
                    int endMatcher = start+picEmojiMatcher.end();
                    spannableString.setSpan(picEmojiSpan, startMatcher, endMatcher, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return whetherMatches;
    }
}
