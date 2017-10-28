package jiang.wsocial.emoji.span;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

/**
 * @another 江祖赟
 * @date 2017/9/29 0029.
 */
public class EmojiReplaceSpan extends ReplacementSpan {

    String emojiStr;

    public EmojiReplaceSpan(String emojiStr){
        this.emojiStr = emojiStr;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm){
        return (int)( paint.measureText(emojiStr)+2 );
    }

    @Override
    public void draw(
            @NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
            @NonNull Paint paint){
        canvas.drawText(emojiStr, x, y, paint);
    }
}
