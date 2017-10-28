package jiang.wsocial.emoji;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * For save the keyboard height.
 */
public class KeyBoardHeightPreference {

    private final static String FILE_NAME = "keyboard.common";
    private final static String KEY_KEYBOARD_HEIGHT = "sp.key.keyboard.height";
    private volatile static SharedPreferences SP;

    public static boolean save(final int keyboardHeight) {
        return with().edit()
                .putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight)
                .commit();
    }

    private static SharedPreferences with() {
        if (SP == null) {
            synchronized (KeyBoardHeightPreference.class) {
                if (SP == null) {
                    SP = EmojiManager.mApplicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }

        return SP;
    }

    public static int get() {
        int defaultHeight = Math.round(EmojiManager.mApplicationContext.getResources().getDisplayMetrics().heightPixels*0.418f);
        return with().getInt(KEY_KEYBOARD_HEIGHT, defaultHeight);
    }

}
