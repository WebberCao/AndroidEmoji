package com.webber.androidemoji.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webber.androidemoji.R;

public class ItemTextView extends LinearLayout {

    private Context mContext;
    private TextView textview_content_r;
    private ProgressBar view_progress_r;

    public ItemTextView(Context context) {
        this(context,null);
    }

    public ItemTextView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_right_text,this);
        mContext = context;
        initView();
    }

    private void initView() {
        textview_content_r = (TextView) findViewById(R.id.textview_content_r);
        view_progress_r = (ProgressBar) findViewById(R.id.view_progress_r);
    }

    public TextView getTextView(){
        return textview_content_r;
    }

    public ProgressBar getProgressBar(){
        return view_progress_r;
    }


}
