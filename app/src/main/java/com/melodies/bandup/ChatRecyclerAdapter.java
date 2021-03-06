package com.melodies.bandup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bergthor on 8.1.2017.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {

    private ArrayList<ChatMessage> mMessages;
    private String mUserId;

    public ChatRecyclerAdapter(Context context, ArrayList<ChatMessage> items, String userId) {
        if (items == null) {
            mMessages = new ArrayList<>();
        } else {
            mMessages = items;
        }
        mUserId = userId;
    }

    @Override
    public ChatRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_cell, parent, false);
        return new ChatRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage chatMessage = mMessages.get(position);



        if (chatMessage.senderUserId.equals(mUserId)) {
            ((LinearLayout)holder.mView).setGravity(Gravity.END);
            holder.mMessage.setBackgroundResource(R.drawable.bubble_chat_sent);
        } else {
            ((LinearLayout)holder.mView).setGravity(Gravity.START);
            holder.mMessage.setBackgroundResource(R.drawable.bubble_chat_received);
        }

        holder.mMessage.setText(chatMessage.text);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addMessage(ChatMessage message, Boolean notify) {
        if (mMessages == null) {
            return;
        }

        mMessages.add(0, message);

        if (mMessages.size() == 0) {
            return;
        }

        if (notify) {
            notifyItemInserted(0);
        }
    }

    public void clearChatHistory() {
        if (mMessages == null) {
            return;
        }
        mMessages.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mMessage;

        ViewHolder(View view) {
            super(view);
            mView = view;


            mMessage = (TextView) view.findViewById(R.id.txtChatMessageText);
        }
    }
}
