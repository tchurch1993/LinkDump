package com.linkdump.tchur.ld.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.recyclerview.widget.RecyclerView;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
            case 2:
                View view2 = mInflater.inflate(R.layout.my_image_message, parent, false);
                return new ImageMessageViewHolder(view2);
            default:
                View defaultView = mInflater.inflate(R.layout.my_message, parent, false);
                return new DefaultViewHolder(defaultView);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Message message = mData.get(position);
        setAnimation(viewHolder.itemView, position);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        switch (viewHolder.getItemViewType()) {
            case 0:
                DefaultViewHolder defaulVH = (DefaultViewHolder) viewHolder;
                Log.d(TAG, "in bindViewholder case 0");
                if (mData.get(position).getIsUser()) {
                    //if me
                    defaulVH.theirLayout.setVisibility(View.GONE);
                    defaulVH.myLayout.setVisibility(View.VISIBLE);
                    defaulVH.myMessageTextView.setText(message.getMessage());
                } else {
                    //if them
                    defaulVH.theirLayout.setVisibility(View.VISIBLE);
                    defaulVH.myLayout.setVisibility(View.GONE);
                    defaulVH.theirMessageTextView.setText(message.getMessage());
                    defaulVH.userName.setText(message.getUserName());
                }
                return;
            case 1:
                Log.d(TAG, "in bindViewholder case 1");
                LinkMessageViewHolder linkVH = ((LinkMessageViewHolder) viewHolder);
                if (mData.get(position).getIsUser()) {
                    //if me
                    linkVH.theirLayout.setVisibility(View.GONE);
                    linkVH.myLayout.setVisibility(View.VISIBLE);
                    linkVH.myMessageTextView.setText(message.getMessage());

                    if (message.getLinkTitle() != null) {
                        linkVH.linkTitle.setText(message.getLinkTitle());
                    } else {
                        linkVH.linkTitle.setVisibility(View.GONE);
                    }
                    if (message.getLinkDescription() != null) {
                        linkVH.linkDescription.setText(message.getLinkDescription());
                    } else {
                        linkVH.linkDescription.setVisibility(View.GONE);
                    }
                    if (message.getLinkVideo() != null) {

                    }
                    if (message.getLinkImage() != null) {
                        Log.d(TAG, "image url: " + message.getLinkImage());
                        if (getFormat(message.getLinkImage()).contains(".gif")) {
                            Glide.with(context).asGif().load(message.getLinkImage())
                                    .apply(
                                            new RequestOptions()
                                                    .transform(new RoundedCorners(8))
                                                    .placeholder(circularProgressDrawable)
                                                    .timeout(10000))
                                    .into(linkVH.linkImage);
                        } else {
                            Glide.with(context).load(message.getLinkImage())
                                    .apply(
                                            new RequestOptions()
                                                    .transform(new RoundedCorners(16))
                                                    .placeholder(circularProgressDrawable)
                                                    .timeout(10000))
                                    .into(linkVH.linkImage);
                        }

                    } else {
                        linkVH.linkImage.setVisibility(View.GONE);
                    }
                    if (message.getLinkUrl() != null) {
                        linkVH.richLink.setClickable(true);
                        linkVH.richLink.setOnClickListener(v -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(message.getLinkUrl()));
                            context.startActivity(i);
                        });
                    }

                } else {
                    //if them
                    linkVH.theirLayout.setVisibility(View.VISIBLE);
                    linkVH.myLayout.setVisibility(View.GONE);
                    linkVH.theirMessageTextView.setText(message.getMessage());
                    linkVH.userName.setText(message.getUserName());
                    if (message.getLinkTitle() != null) {
                        linkVH.theirLinkTitle.setText(message.getLinkTitle());
                    } else {
                        linkVH.theirLinkTitle.setVisibility(View.GONE);
                    }
                    if (message.getLinkDescription() != null) {
                        linkVH.theirLinkDescription.setText(message.getLinkDescription());
                    } else {
                        linkVH.theirLinkDescription.setVisibility(View.GONE);
                    }
                    if (message.getLinkVideo() != null) {

                    }
                    if (message.getLinkImage() != null) {
                        Log.d(TAG, "image url: " + message.getLinkImage());
                        if (getFormat(message.getLinkImage()).contains(".gif")) {
                            Glide.with(context).asGif().load(message.getLinkImage())
                                    .apply(
                                            new RequestOptions()
                                                    .transform(new RoundedCorners(16))
                                                    .placeholder(circularProgressDrawable)
                                                    .timeout(10000))
                                    .into(linkVH.theirLinkImage);
                        } else {
                            Glide.with(context).load(message.getLinkImage())
                                    .apply(
                                            new RequestOptions()
                                                    .transform(new RoundedCorners(16))
                                                    .placeholder(circularProgressDrawable)
                                                    .timeout(10000))
                                    .into(linkVH.theirLinkImage);
                        }

                    } else {
                        linkVH.theirLinkImage.setVisibility(View.GONE);
                    }
                    if (message.getLinkUrl() != null) {
                        linkVH.theirRichLink.setClickable(true);
                        linkVH.theirRichLink.setOnClickListener(v -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(message.getLinkUrl()));
                            context.startActivity(i);
                        });
                    }
                }
                return;
            case 2:
                ImageMessageViewHolder imageVH = (ImageMessageViewHolder) viewHolder;
                Log.d(TAG, "in bindViewholder case 2");
                if (mData.get(position).getIsUser()) {
                    //if me
                    imageVH.theirLayout.setVisibility(View.GONE);
                    imageVH.myLayout.setVisibility(View.VISIBLE);
                    if (message.getImageUrl() != null) {
                        if (getFormat(message.getImageUrl()).contains(".gif")) {
                            Glide.with(context).asGif().load(message.getImageUrl())
                                    .apply(
                                            new RequestOptions()
                                                    .placeholder(circularProgressDrawable)
                                                    .transform(new RoundedCorners(16))
                                                    .timeout(10000)
                                    )
                                    .into(imageVH.imageView);
                        } else {
                            Glide.with(context).load(message.getImageUrl())
                                    .apply(
                                            new RequestOptions()
                                                    .placeholder(circularProgressDrawable)
                                                    .transform(new RoundedCorners(16))
                                                    .timeout(10000)
                                    )
                                    .into(imageVH.imageView);
                        }
                    } else {
                        imageVH.imageView.setVisibility(View.GONE);
                    }
                } else {
                    //if them
                    imageVH.theirLayout.setVisibility(View.VISIBLE);
                    imageVH.myLayout.setVisibility(View.GONE);
                    imageVH.userName.setText(message.getUserName());
                    if (message.getImageUrl() != null) {
                        if (getFormat(message.getImageUrl()).contains(".gif")) {
                            Glide.with(context).asGif().load(message.getImageUrl())
                                    .apply(
                                            new RequestOptions()
                                                    .placeholder(circularProgressDrawable)
                                                    .transform(new RoundedCorners(16))
                                                    .timeout(10000)
                                    )
                                    .into(imageVH.theirImageView);
                        } else {
                            Glide.with(context).load(message.getImageUrl())
                                    .apply(
                                            new RequestOptions()
                                                    .placeholder(circularProgressDrawable)
                                                    .transform(new RoundedCorners(16))
                                                    .timeout(10000)
                                    )
                                    .into(imageVH.theirImageView);
                        }
                    } else {
                        imageVH.theirImageView.setVisibility(View.GONE);
                    }
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
        TextView theirMessageTextView, theirLinkTitle, theirLinkDescription;
        ImageView linkImage, theirLinkImage;
        TextView userName;
        RelativeLayout theirLayout, myLayout, richLink, theirRichLink;


        LinkMessageViewHolder(View itemView) {
            super(itemView);
            richLink = itemView.findViewById(R.id.richLinkLayout);
            linkImage = itemView.findViewById(R.id.linkImage);
            linkTitle = itemView.findViewById(R.id.linkTitle);
            linkDescription = itemView.findViewById(R.id.linkDescription);
            myMessageTextView = itemView.findViewById(R.id.my_message_body);
            theirRichLink = itemView.findViewById(R.id.theirRichLinkLayout);
            theirLinkImage = itemView.findViewById(R.id.theirLinkImage);
            theirLinkTitle = itemView.findViewById(R.id.theirLinkTitle);
            theirLinkDescription = itemView.findViewById(R.id.theirLinkDescription);
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

    public class ImageMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView imageView, theirImageView;
        TextView userName;
        RelativeLayout theirLayout, myLayout, richLink, theirRichLink;


        ImageMessageViewHolder(View itemView) {
            super(itemView);
            richLink = itemView.findViewById(R.id.richLinkLayout);
            imageView = itemView.findViewById(R.id.imageView);
            theirRichLink = itemView.findViewById(R.id.theirRichLinkLayout);
            theirImageView = itemView.findViewById(R.id.theirImageView);
            userName = itemView.findViewById(R.id.their_name);
            theirLayout = itemView.findViewById(R.id.their_relative_layout);
            myLayout = itemView.findViewById(R.id.my_relative_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
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
                case "IMAGE":
                    return 2;
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

    private String getFormat(String url) {
        int formatBeginIndex = url.lastIndexOf(".");
        String format = url.substring(formatBeginIndex, url.length());
        return format;
    }
}
