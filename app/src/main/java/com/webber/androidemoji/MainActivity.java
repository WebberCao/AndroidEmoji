package com.webber.androidemoji;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.webber.androidemoji.fragment.EmojiFragment;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_text;
    private FrameLayout fl_emoji;
    private Fragment emojiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        //rv_text = (RecyclerView) findViewById(R.id.rv_text);
        fl_emoji = (FrameLayout) findViewById(R.id.fl_emoji);
    }

    private void initData() {
        emojiFragment = new EmojiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_emoji,emojiFragment);
        fragmentTransaction.commit();
    }
}
