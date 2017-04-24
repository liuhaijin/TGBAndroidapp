package com.tgb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tgb.R;
import com.tgb.model.Notice;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    public static final int FOOTER_LOAD_MORE = 2;
    public static final int FOOTER_NO_ITEM = 3;
    private Context context;
    private List<Notice> data;

    private int footerType = FOOTER_LOAD_MORE;

    public RecyclerViewAdapter(Context context, List data) {
        this.context = context;
        this.data = data;
    }

    public int getFooterType(){
        return footerType;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_base, parent,
                    false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).tv_username.setText(data.get(position).getPublisher());
            ((ItemViewHolder) holder).tv_date.setText(sdf.format(data.get(position).getReleaseTime()) + "    来自微信助手");
            ((ItemViewHolder) holder).tv_content.setText(data.get(position).getContent());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        } else {
            Log.i("RecyclerViewAdapter","FootViewHolder");
            if (footerType == FOOTER_NO_ITEM){
                ((FootViewHolder) holder).progressBar.setVisibility(View.GONE);
                ((FootViewHolder) holder).txt_load_tip.setText(R.string.nomore);
            }else {
                ((FootViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                ((FootViewHolder) holder).txt_load_tip.setText(R.string.loading);
            }
        }
    }

    public void setFooterType(int footerType){
        this.footerType = footerType;
    }

    static class ItemViewHolder extends ViewHolder {

        TextView tv_username;
        TextView tv_date;
        TextView tv_content;

        public ItemViewHolder(View view) {
            super(view);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
        }
    }

    static class FootViewHolder extends ViewHolder {

        public ProgressBar progressBar;
        public TextView txt_load_tip;

        public FootViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            txt_load_tip = (TextView) itemView.findViewById(R.id.txt_load_tip);
        }
    }
}