/**
 * Copyright 2017 ChenHao Dendi
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jiang.wsocial.contact;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.LinkedHashMap;

import jiang.wsocial.R;

public class FloatItemDecoration extends RecyclerView.ItemDecoration {

    private final String TAG = FloatItemDecoration.class.getSimpleName();
    private Context mContext;
    private int mTitleHeight;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mTextHeight;
    private int mTextBaselineOffset;
    private int mTextStartMargin;
    /**
     * Integer means the related position of the Recyclerview#getViewAdapterPosition()
     * (the position of the view in original adapter's list)
     * String means the title to be drawn
     * 第几个 什么字母
     */
    private LinkedHashMap<Integer,String> mList;
    private int mParentPadingLeft;
    private int mParentPadingTop;
    private int mParentRight;
    private int mParentBottom;

    public FloatItemDecoration(Context context){
        this(context, null);
    }
    public FloatItemDecoration(Context context, LinkedHashMap<Integer,String> list){
        this.mContext = context;
        Resources resources = mContext.getResources();
        this.mList = list;
        this.mTitleHeight = resources.getDimensionPixelSize(R.dimen.wsocial_decoration_title_height);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(mContext, R.color.wsocial_decoration_title_background));

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.wsocial_decoration_title_fontcolor));
        mTextPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.wsocial_decoration_title_fontsize));

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int)( fm.bottom-fm.top );
        mTextBaselineOffset = (int)fm.bottom;
        mTextStartMargin = resources.getDimensionPixelOffset(R.dimen.wsocial_decoration_title_start_margin);
    }

    public int getTextStartMargin(){
        return mTextStartMargin;
    }

    public void setTextStartMargin(int textStartMargin){
        mTextStartMargin = textStartMargin;
    }

    public void setDecorationData(LinkedHashMap<Integer,String> list){
        this.mList = list;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        int position = ( (RecyclerView.LayoutParams)view.getLayoutParams() ).getViewAdapterPosition();
        outRect.set(0, mList.containsKey(position) ? mTitleHeight : 0, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){
        super.onDraw(c, parent, state);
        mParentPadingLeft = parent.getPaddingLeft();
        mParentPadingTop = parent.getPaddingTop();
        mParentRight = parent.getWidth()-parent.getPaddingRight();
        mParentBottom = parent.getPaddingTop()+parent.computeVerticalScrollExtent();
        if(mParentPadingTop != 0) {
            c.save();
            c.clipRect(mParentPadingLeft, mParentPadingTop, mParentRight, mParentBottom);
        }
        final int childCount = parent.getChildCount();//当前屏幕可见的个数
        for(int i = 0; i<childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            int position = params.getViewAdapterPosition();//当前控件实际的位置
            if(!mList.containsKey(position)) {
                //没有
                continue;
            }
            //当前位置 有对应的字母就显示 画Decoration
            drawTitleArea(c, child, params, position);
        }
        if(mParentPadingTop != 0) {
            c.restore();
        }
    }

    private void drawTitleArea(Canvas c, View child, RecyclerView.LayoutParams params, int position){
        final int rectBottom = child.getTop()-params.topMargin;
        c.drawRect(mParentPadingLeft, rectBottom-mTitleHeight, mParentRight, rectBottom, mBackgroundPaint);
        c.drawText(mList.get(position), mParentPadingLeft+child.getPaddingLeft()+mTextStartMargin,
                rectBottom-( mTitleHeight-mTextHeight )/2-mTextBaselineOffset, mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        super.onDrawOver(c, parent, state);
        final int position = ( (LinearLayoutManager)parent.getLayoutManager() ).findFirstVisibleItemPosition();
        if(position == RecyclerView.NO_POSITION) {
            return;
        }
        View child = parent.findViewHolderForAdapterPosition(position).itemView;
        String initial = getTag(position);
        if(initial == null) {
            return;
        }

        boolean flag = false;
        if(getTag(position+1) != null && !initial.equals(getTag(position+1))) {
            if(child.getHeight()+child.getTop()-parent.getPaddingTop()<mTitleHeight) {
                c.save();
                if(mParentPadingTop != 0) {
                    c.clipRect(mParentPadingLeft, mParentPadingTop, mParentRight, mParentBottom);
                }
                flag = true;
                c.translate(0, child.getHeight()+child.getTop()-parent.getPaddingTop()-mTitleHeight);
            }
        }

        c.drawRect(mParentPadingLeft, mParentPadingTop, mParentRight, mParentPadingTop+mTitleHeight, mBackgroundPaint);
        c.drawText(initial, mParentPadingLeft+child.getPaddingLeft()+mTextStartMargin,
                mParentPadingTop+mTitleHeight-( mTitleHeight-mTextHeight )/2-mTextBaselineOffset, mTextPaint);

        if(flag) {
            c.restore();
        }
    }

    private String getTag(int position){
        while(position>=0) {
            if(mList.containsKey(position)) {
                return mList.get(position);
            }
            position--;
        }
        return null;
    }
}

