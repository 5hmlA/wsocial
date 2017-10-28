package jiang.wsocial.emoji;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import jiang.wsocial.R;

/**
 * @another 江祖赟
 * @date 2017/9/13 0013.
 */
public class EmojiManager {
    public final static Pattern CUSPIC_EMOJI_PATTEN = Pattern.compile("\\{:.*?:\\}");
    public final static Pattern GOOGLE_EMOJI_PATTEN = Pattern.compile("\\[:.*?:\\]");

    /**
     * 表情定位符：开始
     */
    public static final String CUSPIC_EMOJI_START = "{:";
    public static final String GOOGLE_EMOJI_START = "[:";

    /**
     * 表情定位符：结束
     */
    public static final String CUSPIC_EMOJI_END = ":}";
    public static final String GOOGLE_EMOJI_END = ":]";

    /**
     * Emoji 表情映射表
     * 几个表示几页
     */
    public static List<SmileyDataSet> smileys = new ArrayList<>();
    public static HashMap<String,String> sEmojiMapping = new HashMap<String,String>();
    public static List<String> sFaceWorlds = new ArrayList<>();

    //    public static final String SMILEY_BASE = "file:///android_asset/smiley/";
    public static final String SMILEY_BASE = "smiley/";
    public static Context mApplicationContext;
    public static boolean Debug = true;

    private static class Inner {
        public static EmojiManager sEmojiManager = new EmojiManager();
    }


    private EmojiManager(){

    }


    public static EmojiManager get(){
        return EmojiManager.Inner.sEmojiManager;
    }


    public EmojiManager initEmojis(Context context){
        mApplicationContext = context.getApplicationContext();
        smileys.clear();
        sEmojiMapping.clear();
        SmileyDataSet setTieba = SmileyDataSet
                .getDataSet(context, "\uD83D\uDE14", SmileyDataSet.TYPE_IMAGE, R.array.smiley_jindian);
        buildEmojiMapping(setTieba);

        SmileyDataSet setAcn = SmileyDataSet
                .getDataSet(context, "\uD83E\uDD17", SmileyDataSet.TYPE_IMAGE, R.array.smiley_acn);
        buildEmojiMapping(setAcn);

        SmileyDataSet setYwz = SmileyDataSet.getDataSet(context, "颜文字", SmileyDataSet.TYPE_TEXT, R.array.smiley_ywz);
        buildFaceWorlds(setYwz);
        buildEmojiMapping(setYwz);

        SmileyDataSet emojis = GoogleEmoji.getEmojis();
        buildEmojiMapping(emojis);

        EmojiManager.smileys.add(setTieba);
        EmojiManager.smileys.add(setAcn);
        EmojiManager.smileys.add(emojis);
        EmojiManager.smileys.add(setYwz);
        return this;
    }

    private void buildFaceWorlds(SmileyDataSet setYwz){
        sFaceWorlds.clear();
        for(Pair<String,String> stringStringPair : setYwz.getSmileys()) {
            sFaceWorlds.add(stringStringPair.first);
        }
    }


    private void buildEmojiMapping(SmileyDataSet setTieba){
        List<Pair<String,String>> smileys = setTieba.getSmileys();
        for(Pair<String,String> smiley : smileys) {
            sEmojiMapping.put(smiley.second, smiley.first);
        }
    }

    public static void LOG(CharSequence str){
        if(Debug && !TextUtils.isEmpty(str)) {
            Log.d("表情管理", str.toString());
        }
    }
}
