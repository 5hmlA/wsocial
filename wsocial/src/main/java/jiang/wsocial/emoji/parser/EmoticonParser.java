package jiang.wsocial.emoji.parser;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;

import jiang.wsocial.emoji.EmojiManager;
import jiang.wsocial.emoji.span.EmoticonSpan;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public abstract class EmoticonParser {
    public abstract boolean parserEmoji(TextView editText, CharSequence text, int start, int lengthBefore, int after);

    public static EmoticonSpan getPicEmojiSpan(float textHeight, String picEmojiPlaceholder){
        Drawable drawable = getDrawableAsset(EmojiManager.sEmojiMapping.get(picEmojiPlaceholder));
        if(drawable != null) {
            float ratio = drawable.getIntrinsicWidth()*1.0f/drawable.getIntrinsicHeight();
            //重点 不设置bounds图片不会显示 坑啊
            if(textHeight != 0) {
                drawable.setBounds(0, 0, (int)( textHeight*ratio ), (int)( textHeight*ratio ));
            }else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            return new jiang.wsocial.emoji.span.EmoticonSpan(drawable);
        }
        return null;
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

    public static Drawable getDrawable(Context context, String emojiName){
        if(TextUtils.isEmpty(emojiName)) {
            return null;
        }

        if(emojiName.indexOf(".")>=0) {
            emojiName = emojiName.substring(0, emojiName.indexOf("."));
        }
        int resID = context.getResources().getIdentifier(emojiName, "mipmap", context.getPackageName());
        if(resID<=0) {
            resID = context.getResources().getIdentifier(emojiName, "drawable", context.getPackageName());
        }

        try {
            return Build.VERSION.SDK_INT>=21 ? context.getResources()
                    .getDrawable(resID, (Resources.Theme)null) : context.getResources().getDrawable(resID);
        }catch(Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Drawable getDrawable(Context context, int emoticon){
        if(emoticon<=0) {
            return null;
        }
        try {
            return Build.VERSION.SDK_INT>=21 ? context.getResources()
                    .getDrawable(emoticon, (Resources.Theme)null) : context.getResources().getDrawable(emoticon);
        }catch(Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public void clearSpan(Spannable spannable, int start, int end){
        if(start == end || spannable == null) {
            return;
        }
        ImageSpan[] oldSpans = spannable.getSpans(start, end, ImageSpan.class);
        if(oldSpans != null) {
            for(int i = 0; i<oldSpans.length; i++) {
                spannable.removeSpan(oldSpans[i]);
            }
        }
    }
}
