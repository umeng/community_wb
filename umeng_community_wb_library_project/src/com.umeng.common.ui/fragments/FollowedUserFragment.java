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

package com.umeng.common.ui.fragments;

import android.os.Bundle;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.common.ui.adapters.ActiveUserAdapter;
import com.umeng.common.ui.adapters.FollowedUserAdapter;
import com.umeng.common.ui.presenter.impl.ActiveUserFgPresenter;
import com.umeng.common.ui.presenter.impl.FollowedUserFgPresenter;

import java.util.List;


/**
 * 已关注的用户的Fragment
 */
public class FollowedUserFragment extends ActiveUserFragment {

    protected String mUserId;

    @Override
    protected ActiveUserFgPresenter createPresenters() {
        String uid = getArguments().getString(Constants.USER_ID_KEY);
        return new FollowedUserFgPresenter(this, uid);
    }

    @Override
    public ActiveUserAdapter createAdapter() {
        ActiveUserAdapter adapter = new FollowedUserAdapter(getActivity());
        // 只有登录用户的个人中心的粉丝、关注列表才显示 加关注按钮
        boolean isCurrentUser = false;
        if (CommConfig.getConfig().loginedUser != null) {
            if (CommConfig.getConfig().loginedUser.id.equals(mUserId)) {
                isCurrentUser = true;
            }
        }
        adapter.isShowFollowBtn(isCurrentUser);
        return adapter;
    }

    public static FollowedUserFragment newFollowedUserFragment(String uid) {
        FollowedUserFragment followedUserFragment = new FollowedUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID_KEY, uid);
        followedUserFragment.setArguments(bundle);
        followedUserFragment.mUserId = uid;
        return followedUserFragment;
    }

    public void executeScrollTop() {
        if (mListView.getFirstVisiblePosition() > mListView.getCount() / 4) {
            mListView.smoothScrollToPosition(0);
        }
    }

    public void updateFollowedState(String uId, boolean followedState) {
        List<CommUser> data = mAdapter.getDataSource();
        int count = data.size();
        for (int i = 0; i < count; i++) {
            if (data.get(i).id.equals(uId)) {
                CommUser user = data.get(i);
                user.isFollowed = followedState;
                notifyDataSetChanged();
                break;
            }
        }
    }


}
