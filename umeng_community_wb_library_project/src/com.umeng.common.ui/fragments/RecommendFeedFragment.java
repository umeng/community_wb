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

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;

import com.umeng.common.ui.presenter.impl.RecommendFeedPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

/**
 * feed推荐页面
 */
public class RecommendFeedFragment extends FeedListFragment<RecommendFeedPresenter> {
    private LinearLayout mLinearLayout;

    public RecommendFeedFragment() {
        // 设置默认值
        setShowtopFeed(true);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mLinearLayout = mViewFinder.findViewById(ResFinder.getId("umeng_comm_ll"));
    }

    @Override
    protected RecommendFeedPresenter createPresenters() {
        RecommendFeedPresenter presenter = new RecommendFeedPresenter(this);
        presenter.setIsShowTopFeeds(isShowTopFeed);
        return presenter;
    }

    @Override
    protected void loadMoreFeed() {
        // 没有网络的情况下从数据库加载
        if (!DeviceUtils.isNetworkAvailable(getActivity())) {
            onRefreshEnd();
            mPresenter.loadDataFromDB();
            return;
        }
        mPresenter.fetchNextPageData();

    }


    @Override
    public void onResume() {
        super.onResume();
        showHotView(true);
    }
}
