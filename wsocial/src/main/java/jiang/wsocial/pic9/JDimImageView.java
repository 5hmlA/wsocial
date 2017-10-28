package jiang.wsocial.pic9;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class JDimImageView extends android.support.v7.widget.AppCompatImageView {
//    PorterDuff.Mode枚举值：
//            1.PorterDuff.Mode.CLEAR
//    所绘制不会提交到画布上。
//            2.PorterDuff.Mode.SRC
//            显示上层绘制图片
//3.PorterDuff.Mode.DST
//            显示下层绘制图片
//4.PorterDuff.Mode.SRC_OVER
//    正常绘制显示，上下层绘制叠盖。
//            5.PorterDuff.Mode.DST_OVER
//    上下层都显示。下层居上显示。
//            6.PorterDuff.Mode.SRC_IN
//    取两层绘制交集。显示上层。
//            7.PorterDuff.Mode.DST_IN
//    取两层绘制交集。显示下层。
//            8.PorterDuff.Mode.SRC_OUT
//    取上层绘制非交集部分。
//            9.PorterDuff.Mode.DST_OUT
//    取下层绘制非交集部分。
//            10.PorterDuff.Mode.SRC_ATOP
//            取下层非交集部分与上层交集部分
//11.PorterDuff.Mode.DST_ATOP
//            取上层非交集部分与下层交集部分
//12.PorterDuff.Mode.XOR
//    //变暗
//13.PorterDuff.Mode.DARKEN
//    //调亮
//14.PorterDuff.Mode.LIGHTEN
//    //用于颜色滤镜
//15.PorterDuff.Mode.MULTIPLY
//16.PorterDuff.Mode.SCREEN
    public static final int MASK_HINT_COLOR = 0x99000000;
    /**
     * 变暗
     */
    public static final float[] SELECTED_DARK = new float[]
            {1, 0, 0, 0, -80,
                    0, 1, 0, 0, -80,
                    0, 0, 1, 0, -80,
                    0, 0, 0, 1, 0};
    /**
     * 变亮
     */

    public static final float[] SELECTED_BRIGHT = new float[]
            {1, 0, 0, 0, 80,
                    0, 1, 0, 0, 80,
                    0, 0, 1, 0, 80,
                    0, 0, 0, 1, 0};

    /**
     * 高对比度
     */

    public static final float[] SELECTED_HDR = new float[]
            {5, 0, 0, 0, -250,
                    0, 5, 0, 0, -250,
                    0, 0, 5, 0, -250,
                    0, 0, 0, 1, 0};

    /**
     * 高饱和度
     */
    public static final float[] SELECTED_HSAT = new float[]
            {(float) 3, (float) -2, (float) -0.2, 0, 50,
                    -1, 2, -0, 0, 50,
                    -1, -2, 4, 0, 50,
                    0, 0, 0, 1, 0};

    /**
     * 改变色调
     */
    public static final float[] SELECTED_DISCOLOR = new float[]
            {(float) -0.5, (float) -0.6, (float) -0.8, 0, 0,
                    (float) -0.4, (float) -0.6, (float) -0.1, 0, 0,
                    (float) -0.3, 2, (float) -0.4, 0, 0,
                    0, 0, 0, 1, 0};

    public JDimImageView(Context context) {
        this(context, null);
    }

    public JDimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//        setMeasuredDimension(widthSize, widthSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setColorFilter(new ColorMatrixColorFilter(SELECTED_DARK));
                break;
            case MotionEvent.ACTION_CANCEL:
                clearColorFilter();
                break;
            case MotionEvent.ACTION_UP:
                clearColorFilter();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setDimMask(){
        setColorFilter(MASK_HINT_COLOR, PorterDuff.Mode.DARKEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            Log.e("JDimImageView","JDimImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }
}
