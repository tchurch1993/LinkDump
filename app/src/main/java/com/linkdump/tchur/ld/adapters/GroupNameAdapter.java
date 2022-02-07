package com.linkdump.tchur.ld.adapters;

import android.content.Context;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.GroupItem;
import com.linkdump.tchur.ld.utils.GroupItemDiffCallback;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.List;

/**
 * Created by tchurch on 11/5/2018.
 * Bow down to my greatness.
 */
public class GroupNameAdapter extends RecyclerView.Adapter<GroupNameAdapter.ViewHolder> {

    private List<GroupItem> groupItems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public GroupNameAdapter(Context context, List<GroupItem> groupItems) {
        this.mInflater = LayoutInflater.from(context);
        this.groupItems = groupItems;
    }

    public void updateGroupItemListItems(List<GroupItem> groupItems){
        final GroupItemDiffCallback diffCallback = new GroupItemDiffCallback(this.groupItems, groupItems);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

//        this.groupItems.clear();
//        this.groupItems.addAll(groupItems);
        diffResult.dispatchUpdatesTo(this);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_group, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupItem groupItem = groupItems.get(position);
        PrettyTime p = new PrettyTime();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(groupItem.getLastSentTime());
        String prettyDate = p.format(c);
        if (!groupItem.getLastMessageType().equals("IMAGE")){
            holder.lastMessageTextView.setText(groupItem.getLastMessage());
        } else {
            holder.lastMessageTextView.setText("sent an image");
        }
        holder.myTextView.setText(groupItem.getGroupName());
        holder.lastMessageSentTimeTextView.setText(prettyDate);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return groupItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView, lastMessageTextView, lastMessageSentTimeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.groupNameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            lastMessageSentTimeTextView = itemView.findViewById(R.id.lastMessageSentTimeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public GroupItem getItem(int id) {
        return groupItems.get(id);
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
