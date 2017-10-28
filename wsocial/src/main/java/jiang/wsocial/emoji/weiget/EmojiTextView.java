package jiang.wsocial.emoji.weiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jiang.wsocial.R;
import jiang.wsocial.Utils;
import jiang.wsocial.emoji.parser.EmoticonParser;
import jiang.wsocial.emoji.parser.FaceWorldEmojiParser;
import jiang.wsocial.emoji.parser.GoogleEmojiParser;
import jiang.wsocial.emoji.parser.PicEmojiParser;

import static jiang.wsocial.emoji.help.EmojiDisplay.buildEmojiString;
import static jiang.wsocial.emoji.span.SpannableClickable.formatUrlPhoneString;

/**
 * @another 江祖赟
 * @date 2017/9/28 0028.
 */
@SuppressLint("AppCompatCustomView")
public class EmojiTextView extends TextView {

    private List<EmoticonParser> mParserList = new ArrayList<>();
    private boolean charSequenceFromSetTextMethod;

    {
        mParserList.add(new GoogleEmojiParser());
        mParserList.add(new PicEmojiParser());
        mParserList.add(new FaceWorldEmojiParser());
    }

    public EmojiTextView(Context context){
        super(context);
    }

    public EmojiTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type){
        if(!TextUtils.isEmpty(text)) {
            try {
                CharSequence emojiString = buildEmojiString(Utils.getTextHeight(this), text);
                super.setText(formatUrlPhoneString(emojiString,
                        ContextCompat.getColor(getContext(), R.color.wsocial_text_formart_phoneurl_color)), type);
            }catch(Exception e) {
                super.setText(text, type);
            }
        }
    }
}
