/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.common.ui.adapters;


import android.content.Context;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;

import com.umeng.common.ui.adapters.viewholders.ActiveUserViewHolder;

import com.umeng.common.ui.colortheme.ColorQueque;


/**
 * 对某条Feed点赞的用户列表Adapter
 */
public class FollowedUserAdapter extends ActiveUserAdapter {

    /**
     * @param context
     */
    public FollowedUserAdapter(Context context) {
        super(context);
    }

    public void setFollowStatus(final ActiveUserViewHolder viewHolder, final CommUser user,
                                boolean status) {

        viewHolder.mToggleButton.setTextOn("");
        viewHolder.mToggleButton.setTextOff("");
            boolean isFollowed = user.isFollowed;
            boolean isFollowing = user.isFollowingMe;
            if (isFollowed && isFollowing) {
                viewHolder.mToggleButton.setBackgroundDrawable(ColorQueque.getDrawable("umeng_comm_user_inter_follow_btn"));
            } else if (isFollowed) {
                viewHolder.mToggleButton.setBackgroundDrawable(ColorQueque.getDrawable("umeng_comm_user_cancel_follow_btn"));
            } else {
                viewHolder.mToggleButton.setBackgroundDrawable(ColorQueque.getDrawable("umeng_comm_user_add_follow_btn"));
            }


        viewHolder.mToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFollowListener.onFollowOrUnFollow(user, viewHolder.mToggleButton, user.isFollowed);

            }
        });
    }

    /**
     * 关注列表、粉丝列表不显示性别
     * @return
     */
    @Override
    protected boolean isShowUserGender() {
        return false;
    }
}
