package jiang.wsocial.emoji;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static jiang.wsocial.emoji.EmojiManager.SMILEY_BASE;

/**
 * 某类表情集合
 */
public class SmileyDataSet {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_EMOJI = 2;

    /**
     * 表情的占位符 tag
     */
    public static final int TAG_EMOJI_PLACEHOLDER = 0x7f056666;
    /**
     * 文字类型表情 类型
     */
    public static final int TAG_TEXTEMOJI_TYPE = 0x7d199101;

    public String name;
    public int type;
    /**
     * Pair.first 表情地址，Pair.second占位名字
     */
    private List<Pair<String, String>> smileys;

    public SmileyDataSet(String name, int type) {
        this.type = type;
        this.name = name;
        smileys = new ArrayList<>();
    }

    public void setSmileys(List<Pair<String, String>> smileys) {
        this.smileys = smileys;
    }

    public int getCount() {
        if (smileys == null) {
            return 0;
        } else {
            return smileys.size();
        }
    }


    public static SmileyDataSet getDataSet(Context context, String name, int type, int stringId) {
        SmileyDataSet set = new SmileyDataSet(name, type);
        String[] smileyArray = context.getResources().getStringArray(stringId);
        List<Pair<String, String>> smileys = new ArrayList<>();

        if (type == TYPE_IMAGE) {
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(SMILEY_BASE + aSmileyArray.split(",")[0], aSmileyArray.split(",")[1]));
            }
        } else {
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(aSmileyArray, aSmileyArray));
            }
        }
        set.setSmileys(smileys);
        return set;
    }
    public Drawable getDrawableAsset(Context context, String path) {
        //这里才是重点
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        try {
            in = assetManager.open(path);
            return Drawable.createFromStream(in, null);
        } catch (Exception e) {
            Log.e("xxx", Log.getStackTraceString(e));
        } finally {
            //if (in!=null)
        }
        return null;
    }

    public View getSmileyItem(Context context, int index, int size) {
        if (index >= smileys.size()) return null;
        Pair<String, String> d = smileys.get(index);

        View v;
        if (type == SmileyDataSet.TYPE_IMAGE) {
            v = new ImageView(context);
            ((ImageView) v).setImageDrawable(getDrawableAsset(context,d.first));
            ////这里才是重点
            //AssetManager assetManager=context.getAssets();
            //try {
            //    InputStream in=assetManager.open(d.first);
            //    Bitmap bmp= BitmapFactory.decodeStream(in);
            //    ( (ImageView)v ).setImageBitmap(bmp);
            //} catch (Exception e) {
            //    Log.e("xxx", Log.getStackTraceString(e));
            //}
        } else {
            v = new TextView(context);
            if (type == SmileyDataSet.TYPE_EMOJI) {
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 2);
            } else {
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 4);
            }
            //v.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            ((TextView) v).setGravity(Gravity.CENTER);
            ((TextView) v).setText(d.first);
        }
        v.setTag(TAG_EMOJI_PLACEHOLDER, d.second);
        v.setTag(TAG_TEXTEMOJI_TYPE, type);
        v.setClickable(true);

        return v;
    }

    public List<Pair<String,String>> getSmileys(){
        return smileys;
    }
}
