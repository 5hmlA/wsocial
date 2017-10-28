package social.jiang.emotionkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jiang.wsocial.emoji.EmojiManager;
import jiang.wsocial.emoji.SmileyInputRoot;
import social.jiang.wsocial.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private SmileyInputRoot rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.get().initEmojis(getApplicationContext());
        setContentView(R.layout.activity_main);
        //抽出颜色到color.xml主库可以复写，替换tab
        initEmotionInput();
    }

    private void initEmotionInput() {
        EditText input = (EditText) findViewById(R.id.ed_comment);
        TextView showEmoji = (TextView) findViewById(R.id.etextv);
        View smileyBtn = findViewById(R.id.btn_emotion);
        View btnMore = findViewById(R.id.btn_more);
        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        rootView = (SmileyInputRoot) findViewById(R.id.root);
        showEmoji.setText("[:1f602:][:1f603:][:1f603:][:1f603:][:1f603:][:1f603:]{:16_0001:}-");
        rootView.initSmiley(input, smileyBtn, btnSend);
//
        /**
         * 设置more view 默认不显示
         */
        rootView.setMoreView(LayoutInflater.from(this).inflate(R.layout.my_smiley_menu, null), btnMore);
        //findViewById(R.id.btn_star).setOnClickListener(this);
        //findViewById(R.id.btn_link).setOnClickListener(this);
        //findViewById(R.id.btn_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String s = "";
        switch (v.getId()) {
            case R.id.btn_send:
                s = "发送";
                break;
            case R.id.btn_link:
                s = "复制连接";
                break;
            case R.id.btn_share:
                s = "分享";
                break;
            case R.id.btn_star:
                s = "收藏";
                break;
        }
        Toast.makeText(this, s + "被电击", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        if (!rootView.onActivityBackClick()) {
            super.onBackPressed();
        }
    }

    public void toPublish(View view){
        Intent intent = new Intent(this, PublishAt.class);
        startActivity(intent);
    }
    int vari;
    public void tooglefull(View view){
        if(vari == 0)
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            vari = 1;
        }else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            vari = 0;
        }
        System.out.println(vari);
    }
}
