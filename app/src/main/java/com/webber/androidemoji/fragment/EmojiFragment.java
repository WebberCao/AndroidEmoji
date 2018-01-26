package com.webber.androidemoji.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.webber.androidemoji.R;
import com.webber.androidemoji.adapter.HorizontalRecyclerviewAdapter;
import com.webber.androidemoji.adapter.NoHorizontalScrollerVPAdapter;
import com.webber.androidemoji.model.ImageModel;
import com.webber.androidemoji.utils.EmotionUtil;
import com.webber.androidemoji.utils.GlobalOnItemClickManagerUtil;
import com.webber.androidemoji.utils.SpanStringUtil;
import com.webber.androidemoji.widget.EmotionKeyboard;
import com.webber.androidemoji.widget.NoHorizontalScrollerViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class EmojiFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = EmojiFragment.class.getSimpleName();
    private CheckBox mCBEmotionBtn;
    private EditText mEdtContent;
    private Button mBtnSend;
    private NoHorizontalScrollerViewPager mNoHorizontalVP;
    //底部水平tab
    private RecyclerView mBottomRecyclerView;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    //当前被选中底部tab
    private static final String CURRENT_POSITION_FLAG = "CURRENT_POSITION_FLAG";
    private int CurrentPosition = 0;
    private List<Fragment> fragments = new ArrayList<>();

    private View contentView;
    private EmotionKeyboard mEmotionKeyboard;
    private GlobalOnItemClickManagerUtil globalOnItemClickManager;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragmentemoji_main, container, false);
        sp = getActivity().getSharedPreferences("EmotionPositionFlag", MODE_PRIVATE);
        initView(layout);
        //初始化EmotionKeyboard
        mEmotionKeyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(layout.findViewById(R.id.ll_emoji))//绑定表情面板
                .bindToContent(contentView)//绑定内容view
                .bindToEditText(((EditText) layout.findViewById(R.id.et_content)))//判断绑定那种EditView
                .bindToEmotionButton(layout.findViewById(R.id.chb_emoji))//绑定表情按钮
                .build();
        initData();
//        点击表情的全局监听管理类
        globalOnItemClickManager = GlobalOnItemClickManagerUtil.getInstance();
        //绑定EditText
        globalOnItemClickManager.attachToEditText(mEdtContent);
        return layout;
    }

    /**
     * 绑定内容view
     *
     * @param contentView
     * @return
     */
    public void bindToContentView(View contentView) {
        this.contentView = contentView;
    }

    private void initView(View layout) {

        mCBEmotionBtn = ((CheckBox) layout.findViewById(R.id.chb_emoji));
        mEdtContent = ((EditText) layout.findViewById(R.id.et_content));
        mBtnSend = ((Button) layout.findViewById(R.id.btn_send));
        mNoHorizontalVP = ((NoHorizontalScrollerViewPager) layout.findViewById(R.id.vp_emoji));
        mBottomRecyclerView = ((RecyclerView) layout.findViewById(R.id.rv_more));
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Log.d(TAG, "onClick: " + mEdtContent.getText().toString());

                ((TextView) contentView).setText(SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE,
                        getActivity(), ((TextView) contentView), mEdtContent.getText().toString()));
                mEdtContent.setText("");
                break;
        }
    }

    private void initData() {

        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                ImageModel model1 = new ImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {//创建其他的表情fragment，不需要可以注释
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }


        //记录底部默认选中第一个
        CurrentPosition = 0;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(CURRENT_POSITION_FLAG, CurrentPosition);
        editor.commit();

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(getActivity(), list);
        mBottomRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        mBottomRecyclerView.setAdapter(horizontalRecyclerviewAdapter);
        mBottomRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //获取先前被点击tab
                int oldPosition = sp.getInt(CURRENT_POSITION_FLAG,0);
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(CURRENT_POSITION_FLAG, CurrentPosition);
                editor.commit();
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                mNoHorizontalVP.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });


    }

    /**
     * 创建其他的表情fragment，不需要可以注释
     */
    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotionComplateFragment f1 = (EmotionComplateFragment) factory.getFragment(EmotionUtil.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle b = null;
        for (int i = 0; i < 7; i++) {
            b = new Bundle();
            b.putString("Interge", "Fragment-" + i);
            FragmentOther fg = FragmentOther.newInstance(FragmentOther.class, b);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(), fragments);
        mNoHorizontalVP.setAdapter(adapter);
    }

    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     *
     * @return true则隐藏表情布局，拦截返回键操作
     * false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress() {
        return mEmotionKeyboard.interceptBackPress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        globalOnItemClickManager.unAttachToEditText();
    }

}
