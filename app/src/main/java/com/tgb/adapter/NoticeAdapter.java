package com.tgb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tgb.R;
import com.tgb.app.AppProfile;
import com.tgb.app.AppState;
import com.tgb.fragment.MeFragment;
import com.tgb.model.Notice;
import com.tgb.model.NoticeCustom;
import com.tgb.utils.DateFormatUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    public static final int FOOTER_LOAD_MORE = 2;
    public static final int FOOTER_NO_ITEM = 3;
    private Context context;
    private List<NoticeCustom> data;

    private int footerType = FOOTER_LOAD_MORE;

    public NoticeAdapter(Context context, List data) {
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            if(data.get(position).getUserWechat() != null){
                if(data.get(position).getUserWechat().getUname() != null && !data.get(position).getUserWechat().getUname().equals("")){
                    ((ItemViewHolder) holder).tv_username.setText(data.get(position).getUserWechat().getUname());
                }else{
                    ((ItemViewHolder) holder).tv_username.setText("匿名用户");
                }

                if(data.get(position).getUserWechat().getUid() != null && !data.get(position).getUserWechat().getUid().equals("")){
                    Log.i("icon url", AppProfile.getBaseAddress + "/user/getPic/icon_" + data.get(position).getUserWechat().getUid() + ".jpg");
                    Glide.with(context)
                            .load(AppProfile.getBaseAddress + "/user/getPic/icon_" + data.get(position).getUserWechat().getUid() + ".jpg")
                            .error(R.mipmap.avatar)
                            .into(((ItemViewHolder) holder).iv_avatar);
                }else{
                    ((ItemViewHolder) holder).iv_avatar.setImageResource(R.mipmap.avatar);
                }
            }else{
                ((ItemViewHolder) holder).iv_avatar.setImageResource(R.mipmap.avatar);
                ((ItemViewHolder) holder).tv_username.setText("匿名用户");
            }
            ((ItemViewHolder) holder).tv_date.setText(DateFormatUtils.getTimesToNow(data.get(position).getReleaseTime()) + "    来自微信助手");
            ((ItemViewHolder) holder).et_content.setText(data.get(position).getContent());
            ((ItemViewHolder) holder).tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.get(position).getUserWechat().getIsfriend()){
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(data.get(position).getUserWechat().getWechat());
                        Toast.makeText(context, "已将对方微信号复制到剪切板", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "暂无对方更多信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                        Log.i("NoticeAdapter", "setOnClickListener");
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        Log.i("NoticeAdapter", "setOnLongClickListener");
                        return false;
                    }
                });
            }
        } else {
            Log.i("NoticeAdapter","FootViewHolder");
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

        ImageView iv_avatar;
        TextView tv_username;
        TextView tv_date;
        EditText et_content;
        TextView tv_more;

        public ItemViewHolder(View view) {
            super(view);
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            et_content = (EditText) view.findViewById(R.id.et_content);
            tv_more = (TextView) view.findViewById(R.id.tv_more);
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