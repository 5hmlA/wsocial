package jiang.wsocial.emoji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import jiang.wsocial.KeyboardUtil;
import jiang.wsocial.R;

/**
 * 表情布局 控制  更多，弹出表情面版
 */
public class SmileyContainer extends FrameLayout {

    boolean isKeyboardShowing;
    private SmileyView smileyView;
    private View moreView;
    private EditText editText;
    private int savedHeight = 0;

    private View moreViewBtn, sendBtn;
    private Paint paint = new Paint();
    /**
     * 表情面板是否 显示
     */
    private boolean mIsEmojiShowing;
    /**
     * 控制面板是否显示
     */
    private boolean mIsContanierShowing;
    private boolean mIsMorePannelShowing;


    public SmileyContainer(Context context) {
        super(context);
        init();
    }


    private void init() {
        savedHeight = KeyBoardHeightPreference.get();
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, savedHeight));
        paint.setColor(getResources().getColor(R.color.wsocial_emoji_pannel_divid_top));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(.5f);

        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }


    /**
     * @param editText 输入框
     * @param smileyBtn 表情面板开关
     * @param sendBtn 发送按钮
     */
    public void init(EditText editText, View smileyBtn, final View sendBtn) {
        if (editText != null) {
            if (sendBtn != null) {
                sendBtn.setEnabled(false);
                this.sendBtn = sendBtn;
            }
            EmotionWrapper handler = new EmotionWrapper(editText,
                    new EmotionWrapper.TextChangeListener() {
                        @Override public void onTextChange(boolean enable, String s) {
                            if (sendBtn != null) {
                                sendBtn.setEnabled(enable);
                                if (moreView != null && moreViewBtn != null) {
                                    if (!enable) {
                                        sendBtn.setVisibility(GONE);
                                        moreViewBtn.setVisibility(VISIBLE);
                                    }
                                    else {
                                        moreViewBtn.setVisibility(GONE);
                                        sendBtn.setVisibility(VISIBLE);
                                    }
                                }
                            }
                        }
                    });

            smileyView.setInputView(handler);

            this.editText = editText;
            //            this.editText.setOnTouchListener(new OnTouchListener() {
            //                @Override public boolean onTouch(View v, MotionEvent event) {
            //                    hideContainer(true);
            //                    return false;
            //                }
            //            });
        }
        setSmileyView(smileyBtn);
    }


    private void setSmileyView(View smileyBtn) {
        smileyBtn.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                toggleEmojiPannel(false);//切换表情面板
                if (moreView != null) {
                    moreView.setVisibility(GONE);
                }
            }
        });
    }


    public void setMoreView(View moreViewIn, View moreBtn) {
        this.moreView = moreViewIn;
        if (sendBtn != null) {
            this.sendBtn.setVisibility(GONE);
            addView(this.moreView);
            this.moreViewBtn = moreBtn;
            this.moreViewBtn.setVisibility(VISIBLE);
            this.sendBtn.setVisibility(GONE);

            moreViewBtn.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    toggleMorePannel(false);
                }
            });
        }
    }


    public SmileyContainer toggleMorePannel(boolean force) {
        if (mIsMorePannelShowing) {
            hideMoreViewPannel(force);
        }
        else {
            showMoreViewPannel();
        }
        return this;
    }


    public void showMoreViewPannel() {
        if (moreView != null && !mIsMorePannelShowing) {
            mIsMorePannelShowing = true;
            setVisibility(VISIBLE);
            mIsContanierShowing = true;
            mIsEmojiShowing = false;
            moreView.setVisibility(VISIBLE);
            smileyView.setVisibility(GONE);
            if (isKeyboardShowing) {
                KeyboardUtil.hideKeyboard(editText);
            }
        }
    }


    public void hideMoreViewPannel(boolean force) {
        if (moreView != null && mIsMorePannelShowing) {
            mIsMorePannelShowing = false;
            mIsEmojiShowing = false;
            moreView.setVisibility(GONE);
            if (force) {
                setVisibility(GONE);
                //控制面板 隐藏
                mIsContanierShowing = false;
            }
            else {
                mIsContanierShowing = true;
                if (!isKeyboardShowing) {
                    KeyboardUtil.showKeyboard(editText);//弹出键盘
                }
            }
        }
    }


    /**
     * 显示表情面板 同时显示 表情控制面板
     */
    public void showEmojiPannel() {
        if (!mIsEmojiShowing) {
            mIsEmojiShowing = true;
            mIsMorePannelShowing = false;
            setVisibility(VISIBLE);
            smileyView.setVisibility(VISIBLE);
            if (moreView != null) {
                //隐藏更多面包
                moreView.setVisibility(GONE);
            }
            if (isKeyboardShowing) {
                KeyboardUtil.hideKeyboard(editText);
            }
        }
    }


    /**
     * @param force true 关闭表情控制面板 false切换到输入法
     */
    public void hideEmojiControlPannel(boolean force) {
        if (mIsEmojiShowing || force) {
            mIsEmojiShowing = false;
            //隐藏表情面板
            smileyView.setVisibility(GONE);
            if (force) {
                setVisibility(GONE);
                //控制面板 隐藏
                mIsContanierShowing = false;
            }
            else {
                mIsContanierShowing = true;
                KeyboardUtil.showKeyboard(editText);//弹出键盘
            }
        }
    }


    public void emojiPannel2Keyboard() {
        hideEmojiControlPannel(false);
    }


    //参数代表是否由键盘弹起
    public void hideContainer(boolean isCauseByKeyboard) {
        hideEmojiControlPannel(true);
    }


    /**
     * @param force false切换到键盘 true关闭面板
     */
    public SmileyContainer toggleEmojiPannel(boolean force) {
        if (mIsEmojiShowing) {
            hideEmojiControlPannel(force);
        }
        else {
            showEmojiPannel();
        }
        return this;
    }


    /**
     * 显示 表情控制面板 但不显示表情面板
     */
    public void onlyShowControlPanner() {
        mIsEmojiShowing = false;
        mIsMorePannelShowing = false;
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
        mIsContanierShowing = true;
        smileyView.setVisibility(GONE);
        if (moreView != null) {
            //隐藏更多面包
            moreView.setVisibility(GONE);
        }
    }


    /**
     * 隐藏 表情控制面板
     */
    public void hideControlPannel() {
        if (mIsContanierShowing && !mIsEmojiShowing && !mIsMorePannelShowing) {
            hideEmojiControlPannel(true);
        }
    }


    //offset > 0 可能时键盘弹起
    void onMainViewSizeChange(int offset) {
        if (offset > 0) {//键盘弹起
            this.isKeyboardShowing = true;
            if (offset != savedHeight) {
                KeyBoardHeightPreference.save(offset);
                savedHeight = offset;
                setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, offset));
            }
            onlyShowControlPanner();
        }
        else if (offset <= 0) {
            //            Log.e("______", "keyboard hide :"+offset);
            this.isKeyboardShowing = false;
            hideControlPannel();
        }
    }

    @Override public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        canvas.drawLine(0, 0.5f, getMeasuredWidth(), 0.5f, paint);
    }

    public boolean isContanierShowing() {
        return mIsContanierShowing;
    }

    public boolean isEmojiShowing() {
        return mIsEmojiShowing;
    }
}
