package com.linkdump.tchur.ld.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.Message;

import java.util.List;

/**
 * Created by tchurh on 11/5/2018.
 * Bow down to my greatness.
 */
public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {




    private List<Message> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private int lastPosition = -1;



    // data is passed into the constructor
    public GroupChatAdapter(Context context, List<Message> data)
    {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }




    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.my_message, parent, false);
        return new ViewHolder(view);
    }




    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        Message message = mData.get(position);
        setAnimation(holder.itemView, position);

        if (mData.get(position).getIsUser()){
            holder.theirLayout.setVisibility(View.GONE);
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.myMessageTextView.setText(message.getMessage());
        } else {
            holder.theirLayout.setVisibility(View.VISIBLE);
            holder.myLayout.setVisibility(View.GONE);
            holder.theirMessageTextView.setText(message.getMessage());
            holder.userName.setText(message.getUserName());
        }
    }




    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition){
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myMessageTextView;
        TextView theirMessageTextView;
        TextView userName;
        RelativeLayout theirLayout, myLayout;


        ViewHolder(View itemView) {
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






    // convenience method for getting data at click position
    public Message getItem(int id) {
        return mData.get(id);
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
