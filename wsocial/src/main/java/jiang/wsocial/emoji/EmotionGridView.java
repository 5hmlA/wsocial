package jiang.wsocial.emoji;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static jiang.wsocial.emoji.SmileyDataSet.TAG_TEXTEMOJI_TYPE;
import static jiang.wsocial.emoji.SmileyDataSet.TYPE_EMOJI;

/**
 * 一页 表情面板
 */
public class EmotionGridView extends ViewGroup implements View.OnClickListener {
    private int colNum, rowNum;
    private int itemWidth, itemHeight;
    private Context context;
    private boolean isInitView;
    private SmileyDataSet set;
    private int startIndex;
    private EmotionWrapper handler;

    public EmotionGridView(Context context, SmileyDataSet set, int colNum, int rowNum, int startIndex, EmotionWrapper handler){
        super(context);
        this.colNum = colNum == 0 ? 6 : colNum;
        this.rowNum = rowNum == 0 ? 4 : rowNum;
        this.set = set;
        this.context = context;
        this.startIndex = startIndex;
        this.handler = handler;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(sizeWidth, sizeHeight);

        itemWidth = sizeWidth/colNum;
        itemHeight = sizeHeight/rowNum;

        if(!isInitView && itemHeight>0) {
            isInitView = true;
            initViews();
        }
    }

    public void initViews(){
        removeAllViews();
        int size = (int)( Math.min(itemHeight, itemWidth)*0.8f );
        int marginLR = ( itemWidth-size )/2;
        int marginTB = ( itemHeight-size )/2;

        LayoutParams lp = new LayoutParams(itemWidth, itemHeight);
        for(int i = startIndex; i<set.getCount() && ( ( i-startIndex )<colNum*rowNum ); i++) {
            View view = set.getSmileyItem(context, i, size);
            view.setPadding(marginLR, marginTB, marginLR, marginTB);
            view.setOnClickListener(this);
            addView(view, lp);
        }

        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        int childCount = getChildCount();
        int startX, startY;
        Log.d("EmotionGridView.layout", l+"||"+t);
        for(int i = 0; i<childCount; i++) {
            startX = ( ( i%colNum )*itemWidth );
            startY = ( ( i/colNum )*itemHeight );
            int endX = Math.min(startX+itemWidth, r);
            int endY = Math.min(startY+itemHeight, b);
            getChildAt(i).layout(startX, startY, endX, endY);
        }
    }

    @Override
    public void onClick(View view){
        if(view instanceof ImageView) {
            ImageView v = (ImageView)view;
            //            handler.insertSmiley((String)v.getTag(SmileyDataSet.TAG_EMOJI_PLACEHOLDER), v.getDrawable());
            handler.insertEmoji((String)v.getTag(SmileyDataSet.TAG_EMOJI_PLACEHOLDER));
        }else if(view instanceof TextView) {
            TextView v = (TextView)view;
            if(( (int)v.getTag(TAG_TEXTEMOJI_TYPE) ) == TYPE_EMOJI) {
                handler.insertEmoji((String)v.getTag(SmileyDataSet.TAG_EMOJI_PLACEHOLDER));
            }else {
                handler.insertString(v.getText().toString());
            }
        }
    }
}
