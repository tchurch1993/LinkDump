package com.linkdump.tchur.ld.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.objects.Event;

import java.util.Calendar;
import java.util.List;

/**
 * Created by tchurh on 11/5/2018.
 * Bow down to my greatness.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Event> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Event> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_event, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mData.get(position);
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(event.getStartTime());
        int hour = startTime.get(Calendar.HOUR_OF_DAY);
        String meridiem = "AM";
        if (hour == 0) {
            hour = 12;
        }
        if (hour > 12) {
            meridiem = "PM";
            hour = hour % 12;
        }
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventTimeTextView.setText(hour + ":" + String.format("%02d", startTime.get(Calendar.MINUTE)) + " " + meridiem);
        holder.eventTypeTextView.setText(event.getCourseName());
        holder.eventDescrTextView.setText(event.getDescription());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView eventNameTextView;
        TextView eventTimeTextView;
        TextView eventTypeTextView;
        TextView eventDescrTextView;

        ViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventTimeTextView = itemView.findViewById(R.id.eventTimeTextView);
            eventTypeTextView = itemView.findViewById(R.id.eventTypeTextView);
            eventDescrTextView = itemView.findViewById(R.id.descriptionTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Event getItem(int id) {
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
