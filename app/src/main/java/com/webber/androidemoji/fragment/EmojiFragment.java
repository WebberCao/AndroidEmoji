package com.webber.androidemoji.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.webber.androidemoji.R;
import com.webber.androidemoji.adapter.ContentRecyclerviewAdapter;
import com.webber.androidemoji.adapter.HorizontalRecyclerviewAdapter;
import com.webber.androidemoji.adapter.NoHorizontalScrollerVPAdapter;
import com.webber.androidemoji.model.ImageModel;
import com.webber.androidemoji.model.ItemBean;
import com.webber.androidemoji.model.ItemModel;
import com.webber.androidemoji.utils.EmotionUtil;
import com.webber.androidemoji.utils.GlobalOnItemClickManagerUtil;
import com.webber.androidemoji.utils.SpanStringUtil;
import com.webber.androidemoji.widget.EmotionKeyboard;
import com.webber.androidemoji.widget.NoHorizontalScrollerViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class EmojiFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = EmojiFragment.class.getSimpleName();
    private CheckBox mCBEmotionBtn;
    private EditText mEdtContent;
    private Button mBtnSend;
    private Button btn_voice;
    private CheckBox chk_voice;
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
    private List<ItemModel> data = new ArrayList<>();
    private ContentRecyclerviewAdapter contentRecyclerviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Dialog voiceDialog;
    private ImageView voiceImage;

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
                .bindToEmotionButton((CheckBox) layout.findViewById(R.id.chb_emoji))//绑定表情按钮
                .bindToVoiceChekBox((CheckBox) layout.findViewById(R.id.chk_voice)) //绑定语音checkBox
                .bindToVoiceButton((Button) layout.findViewById(R.id.btn_voice))//绑定语音 按钮
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
        btn_voice = (Button) layout.findViewById(R.id.btn_voice);
        btn_voice.setOnTouchListener(this);
        chk_voice = (CheckBox) layout.findViewById(R.id.chk_voice);
        chk_voice.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if(contentRecyclerviewAdapter == null){
                    contentRecyclerviewAdapter = new ContentRecyclerviewAdapter(getContext(),R.layout.item_content,0, data);
                    mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                    ((RecyclerView)contentView).setLayoutManager(mLayoutManager);
                    ((RecyclerView)contentView).setAdapter(contentRecyclerviewAdapter);
                }
                ItemBean itemBean = new ItemBean(1,mEdtContent.getText().toString());
                ItemModel itemModel = new ItemModel(itemBean);
                data.add(itemModel);
                contentRecyclerviewAdapter.notifyDataSetChanged();
                contentRecyclerviewAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
                ((RecyclerView)contentView).smoothScrollToPosition(data.size()-1);
                mEdtContent.setText("");
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btn_voice:
                voiceTouch(event);
                break;
            default:
                break;
        }
        return false;
    }

    private void voiceTouch(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                btn_voice.setText("松开 发送");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_pressed);
                showVoiceDialog();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                if (voiceDialog.isShowing()) {
                    voiceDialog.dismiss();
                }
                btn_voice.setText("按住 说话");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_nomal);
                break;
            case MotionEvent.ACTION_UP:
                if (voiceDialog.isShowing()) {
                    voiceDialog.dismiss();
                }
                btn_voice.setText("按住 说话");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_nomal);
                break;

        }
    }

    void showVoiceDialog() {
        voiceDialog = new Dialog(getActivity(), R.style.VoiceDialogStyle);
        voiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        voiceDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        voiceDialog.setContentView(R.layout.voice_dialog);
        voiceImage = (ImageView) voiceDialog.findViewById(R.id.dialog_img);
        voiceDialog.show();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard(){
        InputMethodManager manager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
