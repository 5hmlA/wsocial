package jiang.wsocial.emoji.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class EmoticonSpan extends ImageSpan {
    private float scale = 1f;

    public EmoticonSpan(Context context, int resourceId){
        super(context, resourceId);
    }

    public EmoticonSpan(Drawable drawable){
        super(drawable);
    }

    //这个行数设置行的大小，可以设置fontMetricsInt的top/bottom/accent/decent等
    //设置完成之后一行的大小就会改变
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt){
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if(fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            float fontHeight = fmPaint.descent-fmPaint.ascent;//字体高
            int drHeight = rect.bottom-rect.top;//图片高
            if(drHeight>0) {
                scale = fontHeight/drHeight;
            }
        }
        scale = scale == 0 ? 1 : scale;
        //返回表情的宽度
        return (int)( rect.right*scale )+3;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint){
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        transY = (int)(( ( bottom-top )-drawable.getBounds().bottom*scale )/2+top);
        canvas.translate(x, transY);
        canvas.scale(scale,scale);
        drawable.draw(canvas);
        canvas.restore();
    }
}

