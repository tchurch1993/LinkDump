package com.linkdump.tchur.ld.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.Message;

import java.util.List;

/**
 * Created by tchurh on 11/5/2018.
 * Bow down to my greatness.
 */
public class NewGroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = NewGroupChatAdapter.class.getSimpleName();

    private List<Message> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private int lastPosition = -1;

    // data is passed into the constructor
    public NewGroupChatAdapter(Context context, List<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = mInflater.inflate(R.layout.my_message, parent, false);
                return new DefaultViewHolder(view);
            case 1:
                View view1 = mInflater.inflate(R.layout.my_link_message, parent, false);
                return new LinkMessageViewHolder(view1);
            default:
                View defaultView = mInflater.inflate(R.layout.my_message, parent, false);
                return new DefaultViewHolder(defaultView);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Message message = mData.get(position);
        setAnimation(viewHolder.itemView, position);
        switch (viewHolder.getItemViewType()) {
            case 0:
                Log.d(TAG, "in bindViewholder case 0");
                if (mData.get(position).getIsUser()) {
                    //if me
                    ((DefaultViewHolder) viewHolder).theirLayout.setVisibility(View.GONE);
                    ((DefaultViewHolder) viewHolder).myLayout.setVisibility(View.VISIBLE);
                    ((DefaultViewHolder) viewHolder).myMessageTextView.setText(message.getMessage());
                } else {
                    //if them
                    ((DefaultViewHolder) viewHolder).theirLayout.setVisibility(View.VISIBLE);
                    ((DefaultViewHolder) viewHolder).myLayout.setVisibility(View.GONE);
                    ((DefaultViewHolder) viewHolder).theirMessageTextView.setText(message.getMessage());
                    ((DefaultViewHolder) viewHolder).userName.setText(message.getUserName());
                }
                return;
            case 1:
                Log.d(TAG, "in bindViewholder case 1");

                if (mData.get(position).getIsUser()) {
                    //if me
                    ((LinkMessageViewHolder) viewHolder).theirLayout.setVisibility(View.GONE);
                    ((LinkMessageViewHolder) viewHolder).myLayout.setVisibility(View.VISIBLE);
                    ((LinkMessageViewHolder) viewHolder).myMessageTextView.setText(message.getMessage());
                    if (message.getLinkData() != null) {
                        Log.d(TAG, "linkData is not null");
                        Log.d(TAG, message.getLinkData().values().toString());
                        if (message.getLinkData().get("og:title") != null) {
                            ((LinkMessageViewHolder) viewHolder).linkTitle.setText(message.getLinkData().get("og:title").toString());
                        }
                        if (message.getLinkData().get("og:description") != null) {
                            ((LinkMessageViewHolder) viewHolder).linkDescription.setText(message.getLinkData().get("og:description").toString());
                        }
                        if (message.getLinkData().get("og:image") != null) {
                            Log.d(TAG, "image url: " + message.getLinkData().get("og:image"));
                            Glide.with(context).load(message.getLinkData().get("og:image"))
                                    .into(((LinkMessageViewHolder) viewHolder).linkImage);
                        }
                    }
                } else {
                    //if them
                    ((LinkMessageViewHolder) viewHolder).theirLayout.setVisibility(View.VISIBLE);
                    ((LinkMessageViewHolder) viewHolder).myLayout.setVisibility(View.GONE);
                    ((LinkMessageViewHolder) viewHolder).theirMessageTextView.setText(message.getMessage());
                    ((LinkMessageViewHolder) viewHolder).userName.setText(message.getUserName());
                }
        }


    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myMessageTextView;
        TextView theirMessageTextView;
        TextView userName;
        RelativeLayout theirLayout, myLayout;


        DefaultViewHolder(View itemView) {
            super(itemView);
            myMessageTextView = itemView.findViewById(R.id.my_message_body);
            theirMessageTextView = itemView.findViewById(R.id.their_message_body);
            userName = itemView.findViewById(R.id.their_name);
            theirLayout = itemView.findViewById(R.id.their_relative_layout);
            myLayout = itemView.findViewById(R.id.my_relative_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public class LinkMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myMessageTextView, linkTitle, linkDescription;
        TextView theirMessageTextView;
        ImageView linkImage;
        TextView userName;
        RelativeLayout theirLayout, myLayout;


        LinkMessageViewHolder(View itemView) {
            super(itemView);
            linkImage = itemView.findViewById(R.id.linkImage);
            linkTitle = itemView.findViewById(R.id.linkTitle);
            linkDescription = itemView.findViewById(R.id.linkDescription);
            myMessageTextView = itemView.findViewById(R.id.my_message_body);
            theirMessageTextView = itemView.findViewById(R.id.their_message_body);
            userName = itemView.findViewById(R.id.their_name);
            theirLayout = itemView.findViewById(R.id.their_relative_layout);
            myLayout = itemView.findViewById(R.id.my_relative_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Message getItem(int id) {
        return mData.get(id);
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getMessageType() != null) {
            switch (mData.get(position).getMessageType()) {
                case "TEXT":
                    return 0;
                case "LINK":
                    return 1;
                default:
                    return 0;
            }
        } else {
            return 0;
        }


    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
