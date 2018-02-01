package com.webber.androidemoji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.webber.androidemoji.R;
import com.webber.androidemoji.model.ImageModel;
import com.webber.androidemoji.model.ItemBean;
import com.webber.androidemoji.model.ItemModel;
import com.webber.androidemoji.model.ItemViewType;
import com.webber.androidemoji.utils.DisplayUtil;
import com.webber.androidemoji.utils.EmotionUtil;
import com.webber.androidemoji.utils.SpanStringUtil;
import com.webber.androidemoji.widget.ItemTextView;

import java.util.List;


public class ContentRecyclerviewAdapter extends BaseSectionQuickAdapter<ItemModel,BaseViewHolder> {

    private Context context;

    public ContentRecyclerviewAdapter(Context context, int layoutResId, int layoutHeadResId, List<ItemModel> datas){
        super(layoutResId,layoutHeadResId,datas);
        this.context=context;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ItemModel item) {
    }

    @Override
    protected void convert(BaseViewHolder helper, ItemModel item) {
        ItemBean itemBean = (ItemBean)item.t;
        helper.setVisible(R.id.item_text_view,false);
        switch (itemBean.getType()){
            case ItemViewType.TYPE_ITEM_TEXT:
                helper.setVisible(R.id.item_text_view, true);
                ItemTextView itemTextView = (ItemTextView)helper.getView(R.id.item_text_view);
                itemTextView.getTextView().setText(SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE,
                        context, ((TextView) itemTextView.getTextView()), itemBean.getContent()));
                break;
            default:
                break;
        }

    }

}
