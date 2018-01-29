package com.webber.androidemoji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webber.androidemoji.R;
import com.webber.androidemoji.model.ImageModel;
import com.webber.androidemoji.utils.DisplayUtil;
import com.webber.androidemoji.utils.EmotionUtil;
import com.webber.androidemoji.utils.SpanStringUtil;

import java.util.List;


public class ContentRecyclerviewAdapter extends RecyclerView.Adapter<ContentRecyclerviewAdapter.ViewHolder> {

    private List<String> datas;
    private LayoutInflater mInflater;
    private Context context;

    public ContentRecyclerviewAdapter(Context context, List<String> datas){
        this.datas=datas;
        this.context=context;
        mInflater= LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.item_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String model = datas.get(position);
        holder.textView.setText(SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE,
                        context, ((TextView) holder.textView), model));

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView.findViewById(R.id.tv);
        }
    }

}
