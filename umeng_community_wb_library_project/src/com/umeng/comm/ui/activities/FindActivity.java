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

package com.umeng.comm.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.ui.adapters.viewholders.NavigationCommandImpl;
import com.umeng.common.ui.activities.AlbumActivity;
import com.umeng.common.ui.activities.FindBaseActivity;

/**
 * 发现的Activity
 */
public class FindActivity extends FindBaseActivity implements OnClickListener {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        command = new NavigationCommandImpl(this);
    }

    @Override
    protected void gotoMyFollowActivity() {
        Intent intent = new Intent(FindActivity.this, FollowedTopicActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, mUser.id);
        startActivity(intent);
    }

    @Override
    protected void gotoMyPicActivity() {
        Intent intent = new Intent(FindActivity.this, AlbumActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, mUser.id);
        startActivity(intent);
    }

//    protected void gotoNotificationActivity() {
//        Intent intent = new Intent(FindActivity.this, NotificationActivity.class);
//        intent.putExtra(Constants.USER, mUser);
//        startActivity(intent);
//    }

    @Override
    protected void gotoFeedNewMsgActivity() {
        Intent intent = new Intent(FindActivity.this, NewMsgActivity.class);
        intent.putExtra(Constants.USER, mUser);
        startActivity(intent);
    }

    /**
     * 跳转到用户中心Activity</br>
     */
    @Override
    protected void gotoUserInfoActivity() {
        Intent intent = new Intent(FindActivity.this, UserInfoActivity.class);
        intent.putExtra(Constants.TAG_USER, CommConfig.getConfig().loginedUser);
        startActivity(intent);
    }
}
