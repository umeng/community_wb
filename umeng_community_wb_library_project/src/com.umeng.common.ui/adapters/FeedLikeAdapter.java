package com.umeng.common.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;


import com.umeng.comm.core.beans.Like;


import com.umeng.common.ui.adapters.viewholders.FeedLikeViewHolder;

import com.umeng.common.ui.adapters.viewholders.NavigationCommand;
import com.umeng.common.ui.colortheme.ColorQueque;


/**
 * Created by wangfei on 16/1/25.
 */
public class FeedLikeAdapter extends CommonAdapter<Like, FeedLikeViewHolder> {
    private NavigationCommand command;
    @Override
    protected FeedLikeViewHolder createViewHolder() {
        return new FeedLikeViewHolder();
    }

    @Override
    protected void setItemData(int position, FeedLikeViewHolder viewHolder, View rootView) {
        final Like like = getItem(position);
        viewHolder.usericon.setImageDrawable(ColorQueque.getDrawable("umeng_comm_defaul_icon"));
        viewHolder.usericon.setImageUrl(like.creator.iconUrl);
        viewHolder.username.setText(like.creator.name);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command.navigateToUseProfile(like.creator);

            }
        });
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command.navigateToUseProfile(like.creator);
            }
        });
        viewHolder.usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command.navigateToUseProfile(like.creator);
            }
        });

    }

    public FeedLikeAdapter(Context context, NavigationCommand command) {
        super(context);
        this.mContext = context;
        this.command = command;
    }

}
