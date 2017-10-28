package jiang.wsocial.contact;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import jiang.wsocial.R;

public class RecyclerViewFastScroller extends LinearLayout {
    private static final int BUBBLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;
    private float mLetterSpacingExtra;
    private TextView bubble;
    private View handle;
    private RecyclerView mRecyclerView;
    private float height;
    private boolean isInitialized = false;
    private ObjectAnimator currentAnimator = null;
    private List<String> mNavigators = Arrays.asList(DETAULT_DATA);
    private Paint mNavigatorsPaint;
    private RectF mNavigatorsRectf;
    //当前选中的 字母index
    private int mFocusIndex;
    private int mFocusIndexColor;

    private final Paint mNavigatorsFocusPaint;
    private LinkedHashMap<Integer,String> mRealNavigators;
    private boolean mBubbleCenter;
    private int mW;
    public static final String[] DETAULT_DATA = {"\u2606", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy){
            if(!mScrolledByNaviListTouchMode) {
                updateNavigatorFocusIndex();
            }
        }
    };
    private float mSingleLetterHeight;
    private HashMap<String,String> mSpecialNameMap = new HashMap<>();
    private LinearLayoutManager mRecvLayoutManager;
    private String mLastPositionLetter;
    /**
     * 滑动 联系人字母导航栏 触发的 recycleview滚动
     */
    private boolean mScrolledByNaviListTouchMode;

    public interface BubbleTextGetter {
        String getTextToShowInBubble(int pos);
    }

    {
        mNavigatorsPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(getResources().getColor(R.color.wsocial_navigators_color));
                setTextSize(getResources().getDimension(R.dimen.wsocial_navigators_textsize));
                setTextAlign(Align.CENTER);
            }
        };
        mNavigatorsFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(getResources().getColor(R.color.wsocial_navigators_focuscolor));
                setTextSize(getResources().getDimension(R.dimen.wsocial_navigators_textsize));
                setTextAlign(Align.CENTER);
            }
        };
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RecyclerViewFastScroller(final Context context){
        super(context);
        init(context);
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    protected void init(Context context){
        if(isInitialized) {
            return;
        }
        isInitialized = true;
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        setWillNotDraw(false);
        setClickable(true);
    }

    public void setViewsToUse(@LayoutRes int layoutResId, @IdRes int bubbleResId, @IdRes int handleResId){
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(layoutResId, this, true);
        bubble = (TextView)findViewById(bubbleResId);
        if(bubble != null) {
            bubble.setVisibility(INVISIBLE);
        }
        handle = findViewById(handleResId);
    }

    public void setViewsToUse(
            @LayoutRes int layoutResId, @IdRes int bubbleResId, @IdRes int handleResId, boolean bubbleCenter){
        mBubbleCenter = bubbleCenter;
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(layoutResId, this, true);
        bubble = (TextView)findViewById(bubbleResId);
        if(bubble != null) {
            bubble.setVisibility(INVISIBLE);
        }
        handle = findViewById(handleResId);
    }

    public void setViewsToUse(@LayoutRes int layoutResId, @IdRes int bubbleResId, boolean bubbleCenter){
        mBubbleCenter = bubbleCenter;
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(layoutResId, this, true);
        bubble = (TextView)findViewById(bubbleResId);
        if(bubble != null) {
            bubble.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureHeight(int heightMeasureSpec){
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY || specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        }else {
            Paint.FontMetrics fm = mNavigatorsPaint.getFontMetrics();
            float singleHeight = fm.bottom-fm.top+mLetterSpacingExtra;
            //            mBaseLineHeight = fm.bottom * mLetterSpacingExtra;
            result = (int)( mNavigators.size()*singleHeight );
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        mW = w;
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        requestChangge();
    }

    private void requestChangge(){
        float navigatorWidth = mNavigatorsPaint.measureText("A");
        //todo 不以控件宽高
        mNavigatorsRectf = new RectF(mW-2*navigatorWidth, 0, mW, height);
        if(bubble != null && handle == null && ( (LayoutParams)bubble.getLayoutParams() ).rightMargin == 0) {
            ( (LayoutParams)bubble.getLayoutParams() ).rightMargin = (int)( mNavigatorsRectf.width()*3/2f );
        }
        mSingleLetterHeight = height/mNavigators.size();
        if(handle != null) {
            handle.getLayoutParams().height = (int)mSingleLetterHeight;
        }
        updateBubbleAndHandlePosition();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(mRecyclerView != null) {
            for(int i = 0; i<mNavigators.size(); i++) {
                float xPos = mNavigatorsRectf.centerX();
                float yPos = getyPosByNaviIndex(i);
                if(i == mFocusIndex) {
                    canvas.drawText(mNavigators.get(i), xPos, yPos, mNavigatorsFocusPaint);
                }else {
                    canvas.drawText(mNavigators.get(i), xPos, yPos, mNavigatorsPaint);
                }
            }
        }
    }

    private float getyPosByNaviIndex(int i){
        return mSingleLetterHeight*( i+1/2f )+getFontHeight()/2f;
    }

    private float getFontHeight(){
        Rect bounds = new Rect();
        mNavigatorsPaint.getTextBounds("A", 0, 1, bounds);
        //        Paint.FontMetrics fontMetrics = mNavigatorsPaint.getFontMetrics();
        //        return fontMetrics.bottom-fontMetrics.top;
        return bounds.height();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){
        if(mRecyclerView != null) {
            final int action = event.getAction();
            //第几个字母
            int navposition = calculateOnClickItemNum(event.getY());
            switch(action) {
                case MotionEvent.ACTION_DOWN:
                    if(!mNavigatorsRectf.contains(event.getX(), event.getY())) {
                        //不在 字母区域内
                        return false;
                    }
                    mScrolledByNaviListTouchMode = true;
                    if(currentAnimator != null) {
                        currentAnimator.cancel();
                    }
                    if(bubble != null && bubble.getVisibility() == INVISIBLE) {
                        showBubble(navposition);
                    }
                    if(handle != null) {
                        handle.setSelected(true);
                    }
                case MotionEvent.ACTION_MOVE:
                    final float y = event.getY();
                    setBubbleAndHandlePosition(y, navposition);
                    setRecyclerViewPosition(y, navposition);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(handle != null) {
                        handle.setSelected(false);
                    }
                    hideBubble();
                    mScrolledByNaviListTouchMode = false;
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param yPos
     * @return the corresponding position in list
     */
    private int calculateOnClickItemNum(float yPos){
        int result;
        result = (int)( yPos/height*mNavigators.size() );
        if(result>=mNavigators.size()) {
            result = mNavigators.size()-1;
        }else if(result<0) {
            result = 0;
        }
        return result;
    }

    public RecyclerViewFastScroller setRecyclerView(final RecyclerView recyclerView, LinkedHashMap<Integer,String> realNavigators){
        mRealNavigators = realNavigators;

        if(this.mRecyclerView != recyclerView) {
            if(this.mRecyclerView != null) {
                mRecyclerView.removeOnScrollListener(onScrollListener);
            }
            this.mRecyclerView = recyclerView;
            mRecvLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        }
        if(height>0) {
            requestChangge();
            postInvalidate();
        }
        return this;
    }

    public RecyclerViewFastScroller setRecyclerView2(final RecyclerView recyclerView, LinkedHashMap<Integer,String> realNavigators){
        mRealNavigators = realNavigators;
        if(this.mRecyclerView != recyclerView) {
            if(this.mRecyclerView != null) {
                mRecyclerView.removeOnScrollListener(onScrollListener);
            }
            this.mRecyclerView = recyclerView;
            mRecvLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(onScrollListener);
        }
        if(height>0) {
            requestChangge();
            postInvalidate();
        }
        return this;
    }

    /**
     * @param recyclerView
     * @param realNavigators
     * @param onlyShowRealNavi
     *         是否只显示存在的联系人 字母
     */
    public RecyclerViewFastScroller setRecyclerView(final RecyclerView recyclerView, LinkedHashMap<Integer,String> realNavigators, boolean onlyShowRealNavi){
        mRealNavigators = realNavigators;
        if(onlyShowRealNavi) {
            mNavigators.clear();
            mNavigators.addAll(mRealNavigators.values());
        }
        if(this.mRecyclerView != recyclerView) {
            this.mRecyclerView = recyclerView;
            mRecvLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(onScrollListener);
        }
        if(height>0) {
            requestChangge();
            postInvalidate();
        }
        return this;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(mRecyclerView != null) {
            mRecyclerView = null;
        }
    }

    private void setRecyclerViewPosition(float y, int p){
        if(mRecyclerView != null) {
            String cLetter = checkSpecialName(mNavigators.get(p));
            if(!cLetter.equals(mLastPositionLetter)) {
                mLastPositionLetter = cLetter;
                if(mRealNavigators.containsValue(mLastPositionLetter)) {
                    mFocusIndex = p;
                    for(Integer position : mRealNavigators.keySet()) {
                        if(mRealNavigators.get(position).equals(mLastPositionLetter)) {
                            //                        mRecyclerView.smoothScrollToPosition(position);
                            mRecvLayoutManager.scrollToPositionWithOffset(position, 0);
                            return;
                        }
                    }
                }
            }
        }
    }

    public String checkSpecialName(String orign){
        String special = mSpecialNameMap.get(orign);
        return TextUtils.isEmpty(special) ? orign : special;
    }

    public RecyclerViewFastScroller addSpecialNameMap(String orign, String special){
        mSpecialNameMap.put(orign, special);
        return this;
    }

    private float getValueInRange(int min, float max, float value){
        float minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }


    /**
     * 在recycleview滚动的时候更新 字母列表 选中的颜色
     */
    private void updateNavigatorFocusIndex(){
        if(mRecyclerView != null && mRealNavigators != null && mRealNavigators.size()>0) {
            int firstVisibleItemPosition = mRecvLayoutManager.findFirstVisibleItemPosition();
            //根据第一个item的位置 找到对应的字幕
            Integer lastLess = 0;
            for(Integer integer : mRealNavigators.keySet()) {
                if(firstVisibleItemPosition<integer) {
                    break;
                }
                lastLess = integer;
            }
            System.out.println("=======");
            System.out.println("找到的位置："+lastLess);
            //获取当前第一个位置对应的字母
            String rEcvNavi = mRealNavigators.get(lastLess);
            System.out.println("获取当前第一个位置对应的字母==="+rEcvNavi);
            //获取 字母列表中 对应的字母的下标
            mFocusIndex = mNavigators.indexOf(rEcvNavi);
            System.out.println("获取 字母列表中 对应的字母的下标==="+mFocusIndex);
            //mFocusIndex = -1 得情况 可能是 ☆
            mFocusIndex = Math.max(mFocusIndex, 0);
            //找到当前字母对应中心的Y
            float y = getyPosByNaviIndex(mFocusIndex);
            //吧handle和bubble移动到指定位置
            setBubbleAndHandlePosition(y, mFocusIndex);
            invalidate();
        }
    }

    private void updateBubbleAndHandlePosition(){
        if(mRecyclerView == null || bubble == null || ( handle != null && handle
                .isSelected() ) || mRecvLayoutManager == null) {
            return;
        }
        final int verticalScrollOffset = mRecyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = mRecyclerView.computeVerticalScrollRange();
        float proportion = (float)verticalScrollOffset/( (float)verticalScrollRange-height );
        setBubbleAndHandlePosition(height*proportion, 0);
    }

    private void setBubbleAndHandlePosition(float y, int navposition){
        int handleHeight = 0;
        if(handle != null) {
            handleHeight = handle.getHeight();
            handle.setY(getValueInRange(0, height-handleHeight, (int)( y-handleHeight/2 )));
        }
        if(bubble != null) {
            int bubbleHeight = bubble.getHeight();
            if(mBubbleCenter) {
                bubble.setY(getValueInRange(0, height-bubbleHeight-handleHeight/2, (int)( y-bubbleHeight/2f )));
            }else {
                bubble.setY(getValueInRange(0, height-bubbleHeight-handleHeight/2, (int)( y-bubbleHeight )));
            }
            bubble.setText(mNavigators.get(navposition));
        }
    }

    private void showBubble(int navposition){
        if(bubble == null) {
            return;
        }
        bubble.setVisibility(VISIBLE);
        bubble.setText(mNavigators.get(navposition));
        if(currentAnimator != null) {
            currentAnimator.cancel();
        }
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.start();
    }

    private void hideBubble(){
        if(bubble == null) {
            return;
        }
        if(currentAnimator != null) {
            currentAnimator.cancel();
        }
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation){
                super.onAnimationEnd(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation){
                super.onAnimationCancel(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }
}
