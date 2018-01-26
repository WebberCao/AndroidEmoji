package com.webber.androidemoji;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.webber.androidemoji.fragment.EmojiFragment;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_text;
    private TextView tv_content;
    private FrameLayout fl_emoji;
    private EmojiFragment emojiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initData();
    }

    private void initView() {
        //rv_text = (RecyclerView) findViewById(R.id.rv_text);
        tv_content = (TextView) findViewById(R.id.tv_content);
        fl_emoji = (FrameLayout) findViewById(R.id.fl_emoji);
    }

    private void initData() {
        emojiFragment = EmojiFragment.newInstance(EmojiFragment.class,null);
        emojiFragment.bindToContentView(tv_content);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_emoji,emojiFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        /**
         * 按下返回键，如果表情显示，则隐藏，没有显示则回退页面
         */
        if (!emojiFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }
}
