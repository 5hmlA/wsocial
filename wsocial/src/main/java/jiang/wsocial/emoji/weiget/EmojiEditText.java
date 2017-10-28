package jiang.wsocial.emoji.weiget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import jiang.wsocial.emoji.parser.EmoticonParser;
import jiang.wsocial.emoji.parser.FaceWorldEmojiParser;
import jiang.wsocial.emoji.parser.GoogleEmojiParser;
import jiang.wsocial.emoji.parser.PicEmojiParser;

import static jiang.wsocial.emoji.EmojiManager.LOG;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
public class EmojiEditText extends android.support.v7.widget.AppCompatEditText {
    private List<EmoticonParser> mParserList = new ArrayList<>();
    private boolean charSequenceFromSetTextMethod;

    {
        mParserList.add(new GoogleEmojiParser());
        mParserList.add(new PicEmojiParser());
        mParserList.add(new FaceWorldEmojiParser());
    }

    public EmojiEditText(Context context){
        super(context);
    }

    public EmojiEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type){
        if(!TextUtils.isEmpty(text)) {
            charSequenceFromSetTextMethod = true;
        }
        super.setText(text, type);
    }

    @Override
    protected final void onTextChanged(CharSequence text, int start, int lengthBefore, int after){
        super.onTextChanged(text, start, lengthBefore, after);

        if(mParserList == null) {
            return;
        }
        for(EmoticonParser emoticonFilter : mParserList) {
            //charSequenceFromSetTextMethod必须放后面
            if(emoticonFilter.parserEmoji(this, text, start, lengthBefore, after) && !charSequenceFromSetTextMethod) {
                //匹配到 结束循环  通过setText输入不行这样
                break;
            }
        }
        charSequenceFromSetTextMethod = false;
        LOG(getText());
    }

    public void addEmoticonParser(EmoticonParser emoticonParser){
        if(mParserList == null) {
            mParserList = new ArrayList<>();
        }
        mParserList.add(emoticonParser);
    }

    public void removedEmoticonParser(EmoticonParser emoticonParser){
        if(mParserList != null && mParserList.contains(emoticonParser)) {
            mParserList.remove(emoticonParser);
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event){
        if(onBackKeyClickListener != null) {
            onBackKeyClickListener.onBackKeyClick();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public interface OnBackKeyClickListener {
        void onBackKeyClick();
    }

    OnBackKeyClickListener onBackKeyClickListener;

    public void setOnBackKeyClickListener(OnBackKeyClickListener i){
        onBackKeyClickListener = i;
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    OnSizeChangedListener onSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener i){
        onSizeChangedListener = i;
    }
}
