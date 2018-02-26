package com.webber.androidemoji.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.webber.androidemoji.R;
import com.webber.androidemoji.adapter.ContentRecyclerviewAdapter;
import com.webber.androidemoji.adapter.HorizontalRecyclerviewAdapter;
import com.webber.androidemoji.adapter.NoHorizontalScrollerVPAdapter;
import com.webber.androidemoji.model.ImageModel;
import com.webber.androidemoji.model.ItemBean;
import com.webber.androidemoji.model.ItemModel;
import com.webber.androidemoji.utils.AudioPlayManager;
import com.webber.androidemoji.utils.AudioRecoderManager;
import com.webber.androidemoji.utils.EmotionUtil;
import com.webber.androidemoji.utils.GlobalOnItemClickManagerUtil;
import com.webber.androidemoji.utils.SpanStringUtil;
import com.webber.androidemoji.utils.VoiceBubbleListener;
import com.webber.androidemoji.widget.EmotionKeyboard;
import com.webber.androidemoji.widget.NoHorizontalScrollerViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class EmojiFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener, VoiceBubbleListener {
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
    private TextView tv_voice;
    private double voiceValue;
    private boolean isSend; //是否发送
    private Thread recordThread;
    private boolean isRecording = false;
    private static float recodeTime = 0.0f;
    private float y;

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
                AudioPlayManager.getInstance(getContext(), this).stopPlay();
                isRecording = true;
                isSend = true;
                btn_voice.setText("松开 发送");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_pressed);
                showVoiceDialog();
                try {
                    AudioRecoderManager.getInstance(getContext()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecordThread();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                if(y-currentY>30){
                    //取消发送
                    isSend = false;
                    tv_voice.setText("松开手指，取消发送");
                    tv_voice.setTextColor(0XFFFF3030);
                }else {
                    //取消发送
                    isSend = true;
                    tv_voice.setText("手指上划，取消发送");
                    tv_voice.setTextColor(0XFFFFFFFF);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (voiceDialog.isShowing()) {
                    voiceDialog.dismiss();
                }
                btn_voice.setText("按住 说话");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_nomal);
                if(isSend){
                    //发送语音
                    Toast.makeText(getActivity(),"发送语音",Toast.LENGTH_LONG).show();
                }
                break;
            case MotionEvent.ACTION_UP:
                try {
                    isRecording = false;
                    if (voiceDialog.isShowing()) {
                        voiceDialog.dismiss();
                    }
                    String voicePath = AudioRecoderManager.getInstance(getContext()).stop();
                    File file = new File(voicePath);
                    //将音频保存到字节数组中然后再转成String字符串用于发送
                    byte[] voicebyte = null;
                    try {
                        FileInputStream in = new FileInputStream(file);
                        voicebyte = new byte[(int) file.length()];
                        in.read(voicebyte, 0, voicebyte.length);
                        in.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    String fileType = voicePath.substring(voicePath.lastIndexOf(".")+1);
                    String voiceStr = new String(Base64.encode(voicebyte, Base64.DEFAULT));
                    voiceValue = 0.0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AudioRecoderManager.destroy();
                btn_voice.setText("按住 说话");
                btn_voice.setBackgroundResource(R.drawable.voice_bg_button_nomal);
                if(isSend){
                    //发送语音
                    Toast.makeText(getActivity(),"发送语音",Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private void showVoiceDialog() {
        voiceDialog = new Dialog(getActivity(), R.style.VoiceDialogStyle);
        voiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        voiceDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        voiceDialog.setContentView(R.layout.voice_dialog);
        voiceImage = (ImageView) voiceDialog.findViewById(R.id.dialog_img);
        tv_voice = (TextView) voiceDialog.findViewById(R.id.tv_voice);
        voiceDialog.show();
    }

    private void startRecordThread() {
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }

    private Runnable ImgThread = new Runnable() {
        @Override
        public void run() {
            recodeTime = 0.0f;
            while (isRecording) {
                try {
                    Thread.sleep(200);
                    recodeTime += 0.2;
                    voiceValue = AudioRecoderManager.getInstance(getContext())
                            .getAmplitude();
                    imgHandle.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Handler imgHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        setVoiceImage();
                        break;
                    default:
                        break;
                }

            }
        };
    };

    private void setVoiceImage() {
        if (voiceValue < 200.0) {
            voiceImage.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 200.0 && voiceValue < 400) {
            voiceImage.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 400.0 && voiceValue < 800) {
            voiceImage.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 800.0 && voiceValue < 1600) {
            voiceImage.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1600.0 && voiceValue < 3200) {
            voiceImage.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 3200.0 && voiceValue < 5000) {
            voiceImage.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 5000.0 && voiceValue < 7000) {
            voiceImage.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 28000.0) {
            voiceImage.setImageResource(R.drawable.record_animate_14);
        }
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

    @Override
    public void playFail(View messageBubble) {

    }

    @Override
    public void playStoped(View messageBubble) {

    }

    @Override
    public void playStart(View messageBubble) {

    }

    @Override
    public void playDownload(View messageBubble) {

    }

    @Override
    public void playCompletion(View messageBubble) {

    }
}
