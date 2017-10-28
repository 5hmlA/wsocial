package jiang.wsocial.emoji;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.widget.EditText;

import java.util.ArrayList;

import jiang.wsocial.emoji.span.EmoticonSpan;

/**
 * Created by free2 on 16-3-20.
 * 表情处理 解析
 * 格式处理 加粗 斜体等。。。
 */
public class EmotionWrapper implements TextWatcher {

    private final EditText mEditor;
    private final TextChangeListener listener;
    private final ArrayList<ReplacementSpan> mEmoticonsToRemove = new ArrayList<>();
    private final ArrayList<ColorTextSpan> mEmoticonsToRemove2 = new ArrayList<>();

    public EmotionWrapper(EditText editor, TextChangeListener listener){
        mEditor = editor;
        mEditor.addTextChangedListener(this);
        this.listener = listener;
    }


    /**
     * 颜文字 添加颜色
     *
     * @param s
     */
    public void insertString(String s){
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        Editable editableText = mEditor.getEditableText();
        editableText.insert(end, s);
//        editableText.setSpan(new ColorTextSpan(), start, start+s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * {}转为图片
     *
     * @param s
     *         表情占位符
     * @param drawable
     */
    public void insertSmiley(String s, Drawable drawable){
        if(drawable != null) {
            EmoticonSpan emoticonSpan = new EmoticonSpan(drawable);
            int start = mEditor.getSelectionStart();
            int end = mEditor.getSelectionEnd();
            Editable editableText = mEditor.getEditableText();
            // Insert the emoticon.
            editableText.insert(end, s);//会处罚editetxtview的onTextChanged方法后往下走
            //在onTextChanged中 会触发PicEmojiParse解析
            editableText.setSpan(emoticonSpan, start, start+s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * {}转为图片/[]转为Google表情
     *
     * @param emojiPlachHolder
     */
    public void insertEmoji(String emojiPlachHolder){
        int end = mEditor.getSelectionEnd();
        Editable editableText = mEditor.getEditableText();
        // Insert the emoticon.
        editableText.insert(end, emojiPlachHolder);//会触发editetxtview的onTextChanged方法后往下走
        //在onTextChanged中 会触发PicEmojiParse解析
        //            editableText.setSpan(emoticonSpan, start, start+s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void backSpace(){
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        if(start == 0) {
            return;
        }
        if(( start == end ) && start>0) {
            start = start-1;
        }
        mEditor.getText().delete(start, end);
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after){
        if(count>0) {
            int end = start+count;
            Editable message = mEditor.getEditableText();
            ReplacementSpan[] list = message.getSpans(start, end, ReplacementSpan.class);
            for(ReplacementSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                if(( spanStart<end ) && ( spanEnd>start )) {
                    // Add to remove list
                    mEmoticonsToRemove.add(span);
                }
            }

            //            ColorTextSpan[] list2 = message.getSpans(start, end, ColorTextSpan.class);
            //            for (ColorTextSpan span : list2) {
            //                int spanStart = message.getSpanStart(span);
            //                int spanEnd = message.getSpanEnd(span);
            //                if ((spanStart < end) && (spanEnd > start)) {
            //                    // Add to remove list
            //                    mEmoticonsToRemove2.add(span);
            //                }
            //            }

        }
    }

    @Override
    public void afterTextChanged(Editable text){
        Editable message = mEditor.getEditableText();
        for(ReplacementSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if(start != end) {
                message.delete(start, end);
            }
        }

        for(ColorTextSpan span : mEmoticonsToRemove2) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if(start != end) {
                message.delete(start, end);
            }
        }

        mEmoticonsToRemove.clear();
        mEmoticonsToRemove2.clear();

        if(!TextUtils.isEmpty(mEditor.getText().toString())) {
            listener.onTextChange(true, mEditor.getText().toString());
        }else {
            listener.onTextChange(false, mEditor.getText().toString());
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count){

    }

    public interface TextChangeListener {
        void onTextChange(boolean enable, String s);
    }

}