package com.yukinohara.innogybot.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yukinohara.innogybot.R;
import com.yukinohara.innogybot.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YukiNoHara on 6/14/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Message> mList;
    private Context mContext;
    private final int SELF = 10;
    private final int BOT = 11;
    private OnItemClickListener listener;

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
    }

    public interface OnItemClickListener{
        void onItemClick(int id);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == SELF){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user, parent, false);
            return new MessageViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_bot, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mList.get(position);
        holder.textView.setText(message.getContent());
    }

    public void setData(List<Message> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mList.get(position);
        if (message.getPermission() == 1){
            return SELF;
        } else
        {
            return BOT;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public MessageViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_message);
        }

    }
}
