package social.jiang.emotionkeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;

import jiang.wsocial.emoji.GoogleEmoji;
import jiang.wsocial.emoji.SmileyInputRoot;
import social.jiang.wsocial.R;

public class PublishAt extends AppCompatActivity implements View.OnClickListener {

    private SmileyInputRoot rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_at);

        initEmotionInput();
    }

    private void initEmotionInput(){
//        EditText input = (EditText)findViewById(R.id.ed_comment1);
        EditText botn = (EditText)findViewById(R.id.ed_comment1);
        botn.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
                Matcher m = GoogleEmoji.getEmojiMatcher(source.toString());
//                if(m != null) {
//                    while(m.find()) {
//                        return "";
//                    }
//                }

                return null;
            }

        }});
        String emoji = "{:16_0008:}{:16_0009:}{:15_950:}Ծ‸Ծ(￣3￣)(๑•॒̀ ູ॒•́๑)\uD83D\uDE33\uD83D\uDE01\uD83E\uDD14";

        View smileyBtn = findViewById(R.id.btn_emotion);
        View btnMore = findViewById(R.id.btn_more);
        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        rootView = (SmileyInputRoot)findViewById(R.id.root);

        rootView.initSmiley(botn, smileyBtn, btnSend);

        /**
         * 设置more view 默认不显示
         */
        rootView.setMoreView(LayoutInflater.from(this).inflate(R.layout.my_smiley_menu, null), btnMore);
        //findViewById(R.id.btn_star).setOnClickListener(this);
        //findViewById(R.id.btn_link).setOnClickListener(this);
        //findViewById(R.id.btn_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        String s = "";
        switch(v.getId()) {
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
        Toast.makeText(this, s+"被电击", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed(){
        if(!rootView.onActivityBackClick()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture){

    }
}
