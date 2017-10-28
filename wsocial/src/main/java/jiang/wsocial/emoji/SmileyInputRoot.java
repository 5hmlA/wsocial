package jiang.wsocial.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import jiang.wsocial.DimmenUtils;
import jiang.wsocial.R;
import jiang.wsocial.Utils;

/**
 * 容器：按钮 更多布局 表情面板
 */
public class SmileyInputRoot extends LinearLayout {

    private int mOldHeight = -1;
    private SmileyContainer mSmileyContainer;
    private int maxHeight = 100;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout(){
            possiblyResizeChildOfContent();
        }
    };
    private WeakReference<Activity> mActivityWeakReference;
    private Rect r = new Rect();
    private int orignButtom;
    private int dirtydata;

    private void possiblyResizeChildOfContent(){
        //        adjustNothing 会导致 无法检测键盘弹出与收起
        if(mActivityWeakReference.get() != null) {
            int usableButtomNow = computeUsableButtom();
            int height = orignButtom-usableButtomNow;
            if(dirtydata != height) {
                dirtydata = height;
                // offset > 0 键盘弹起了
                if(mSmileyContainer != null) {
                    mSmileyContainer.onMainViewSizeChange(height);
                }
            }
        }
    }

    private int computeUsableButtom(){
        if(mActivityWeakReference.get() != null) {
            //可见部分
            mActivityWeakReference.get().findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
        }
        return r.bottom;
    }

    public SmileyInputRoot(Context context){
        super(context);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
        if(getContext() instanceof Activity) {
            final Activity activity = Utils.getActivityFromView(this);
            if(activity != null) {
                //        adjustNothing 会导致 无法检测键盘弹出与收起
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                if(( ( attributes.softInputMode&WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING ) == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING ) || ( ( attributes.softInputMode&WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN ) == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN )) {
                    //如果windowSoftInputMode为adjustNothing  重新设置windowSoftInputMode
                    attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
                    Log.e("SmileyInputRoot", "adjustNothing|adjustPan从新设置windowSoftInputMode为 adjustResize");
                }
                //布局全屏 adjustPan|adjustResize下弹出输入法不会导致 布局size变化
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP && ( window
                        .getAttributes().flags&WindowManager.LayoutParams.FLAG_FULLSCREEN ) != WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    //低版本手机 无法设置状态栏透明 非全屏状态下会被黑色状态栏挡住
                    activity.findViewById(android.R.id.content)
                            .setPadding(0, DimmenUtils.getStatusBarHeight(activity), 0, 0);
                }
                mActivityWeakReference = new WeakReference<Activity>((Activity)getContext());
            }
        }
        getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        //        setLayoutTransition(new LayoutTransition());
    }

    public void bindActivity(Activity activity){
        mActivityWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mSmileyContainer = new SmileyContainer(getContext());
        mSmileyContainer.setBackgroundResource(R.color.wsocial_emoji_pannel_bg);
        mSmileyContainer.setVisibility(GONE);
        //添加表情面板
        addView(mSmileyContainer);
    }

    public SmileyInputRoot toggleEmojiPannel(){
        mSmileyContainer.toggleEmojiPannel(true);
        return this;
    }

    /**
     * 表情面板是否显示
     *
     * @return
     */
    public boolean isEmojiPannelShowing(){
        return mSmileyContainer.isEmojiShowing();
    }

    /**
     * 表情控制 面板是否显示
     *
     * @return
     */
    public boolean isContanierPannelShowing(){
        return mSmileyContainer.isContanierShowing();
    }

    public SmileyInputRoot hideControlPannel(){
        mSmileyContainer.hideControlPannel();
        return this;
    }

    public void initSmiley(View smileyBtn){
        mSmileyContainer.init(null, smileyBtn, null);
    }

    public void initSmiley(EditText editText, View smileyBtn, final View sendBtn){
        mSmileyContainer.init(editText, smileyBtn, sendBtn);
    }

    public void setMoreView(View moreViewIn, View moreBtn){
        mSmileyContainer.setMoreView(moreViewIn, moreBtn);
    }

    //return is handled
    public boolean onActivityBackClick(){
        if(mSmileyContainer.getVisibility() == VISIBLE) {
            mSmileyContainer.hideContainer(false);
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = computeUsableButtom();
        if(orignButtom<height) {
            orignButtom = height;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }
}
