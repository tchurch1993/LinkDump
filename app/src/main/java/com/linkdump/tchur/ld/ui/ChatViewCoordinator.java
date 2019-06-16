package com.linkdump.tchur.ld.ui;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.abstractions.EventBased.IOnCompletedListener;
import com.linkdump.tchur.ld.adapters.NewGroupChatAdapter;
import com.linkdump.tchur.ld.utils.MyEditText;

public class ChatViewCoordinator extends ViewCoordinator {

    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;
    public ImageButton imageButton;
    public MyEditText chatEditText;
    public Toolbar toolbar;
    public NewGroupChatAdapter adapter;

    public IOnCompletedListener onCompletedListener;

    public ChatViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        super(context,appCompatActivity);
    }

    public ChatViewCoordinator registerOnCompletedListener(IOnCompletedListener listener){
        this.onCompletedListener = listener;
        return this;
    }

    @Override
    public void PostViewInit(View view) {

        mRecyclerView = view.findViewById(R.id.chat_recyclerview);
        toolbar = view.findViewById(R.id.chatToolbar);
        imageButton = view.findViewById(R.id.imageButton);
        chatEditText = view.findViewById(R.id.chat_message_edit_text);

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

       /* adapter = new NewGroupChatAdapter(context, messages);


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mLayoutManager.smoothScrollToPosition(mRecyclerView,
                        null,
                        adapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(adapter);*/

        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        //actionBar.setTitle(groupName);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24px);
        }




    }
}
