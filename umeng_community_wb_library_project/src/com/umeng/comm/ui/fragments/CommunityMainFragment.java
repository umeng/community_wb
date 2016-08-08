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

package com.umeng.comm.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.GuestStatusResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.FindActivity;
import com.umeng.comm.ui.adapters.viewholders.NavigationCommandImpl;
import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.fragments.CommunityMainBaseFragment;
import com.umeng.common.ui.fragments.FeedListBaseFragment;
import com.umeng.common.ui.fragments.TopicMainBaseFragment;

import java.util.ArrayList;


/**
 * 社区首页，包含关注、推荐、话题三个tab的页面，通过ViewPager管理页面之间的切换.
 */
public class CommunityMainFragment extends CommunityMainBaseFragment {



  private ArrayList<FeedListBaseFragment> mFeedListBaseFragments = new ArrayList<FeedListBaseFragment>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected int getFragmentLayout() {
        CommunitySDKImpl.getInstance().fetchCommunitystatus(new Listeners.SimpleFetchListener<GuestStatusResponse>() {
            @Override
            public void onComplete(GuestStatusResponse response) {
                CommonUtils.visitNum = response.guestStatus;
            }
        });
        return ResFinder.getLayout("umeng_comm_community_frag_layout");
    }

    protected void initWidgets() {
        mContainerClass = getActivity().getClass().getName();
        parseJson.getJson(getActivity(), "custom.json");
        initTitle(mRootView);
        command = new NavigationCommandImpl(getActivity());
        initFragment();
        initViewPager(mRootView);
        registerInitSuccessBroadcast();
        CommunitySDKImpl.getInstance().upLoadUI("weibo");
    }

    public void gotoFindActivity(CommUser user) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        Intent intent = new Intent(getActivity(), FindActivity.class);
        if (user == null) {// 来自开发者外部调用的情况
            intent.putExtra(Constants.TAG_USER, CommConfig.getConfig().loginedUser);
        } else {
            intent.putExtra(Constants.TAG_USER, user);
        }
        intent.putExtra(Constants.TYPE_CLASS, mContainerClass);
        getActivity().startActivity(intent);
    }

    @Override
    public TopicMainBaseFragment getTopicMainFragment() {
        TopicMainFragment topicMainFragment = new TopicMainFragment();
        return topicMainFragment;
    }


}
