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
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.CommonUtils;

import com.umeng.common.ui.adapters.FeedBaseAdapter;
import com.umeng.common.ui.presenter.impl.TopicFeedPresenter;
import com.umeng.common.ui.util.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * 某个话题的所有feed页面,即话题的详情页面.
 */
public class TopicFeedFragment extends FeedListFragment<TopicFeedPresenter> {

    protected Topic mTopic;
    private OnResultListener mAnimationListener = null;

    /**
     * 先创建一个TopicFeedFragment对象
     *
     * @return
     */
    public static TopicFeedFragment newTopicFeedFrmg(final Topic topic) {
        TopicFeedFragment topicFeedFragment = new TopicFeedFragment();
        topicFeedFragment.mTopic = topic;
        topicFeedFragment.isShowPostButton(true);
        topicFeedFragment.mFeedFilter = new Filter<FeedItem>() {

            @Override
            public List<FeedItem> doFilte(List<FeedItem> newItems) {
                if (newItems == null || newItems.size() == 0) {
                    return newItems;
                }
                Iterator<FeedItem> iterator = newItems.iterator();
                while (iterator.hasNext()) {
                    List<Topic> topics = iterator.next().topics;
                    if (!topics.contains(topic)) {
                        iterator.remove();
                    }
                }
                return newItems;
            }
        };
        return topicFeedFragment;
    }

    public TopicFeedFragment() {
        setShowtopFeed(true);
        isSetPaddingToListView(true);
    }

    @Override
    protected FeedBaseAdapter createListViewAdapter() {
        super.createListViewAdapter();
        FeedBaseAdapter adapter = command.createFeedAdapter(getActivity());
        adapter.setIsDisplayTopic(false);
        return adapter;
    }

    @Override
    protected void showPostButtonWithAnim() {
        AlphaAnimation showAnim = new AlphaAnimation(0.5f, 1.0f);
        showAnim.setDuration(500);

    }

    @Override
    protected void initViews() {
        super.initViews();
        setIsDisplayTopic(false);
        if (mAnimationListener != null) {
            mRefreshLayout.setOnScrollDirectionListener(new OnResultListener() {

                @Override
                public void onResult(int status) {
                    if (!isScrollEffective) {
                        return;
                    }
                    // 1:向上滑动且第一项显示 (up)
                    // 0:向下且大于第一项 (down)
                    int firstVisible = mFeedsListView.getFirstVisiblePosition();
                    int headerCount = mFeedsListView.getHeaderViewsCount();
                    if ((status == 1 && firstVisible >= headerCount)
                            || (status == 0 && firstVisible == headerCount)) {
                        mAnimationListener.onResult(status);
                    }
                }
            });
        }
    }

    @Override
    public void gotoPostFeedActivity() {
        command.navigateToPostFeed(mTopic);

    }

    @Override
    protected void loadMoreFeed() {
        mPresenter.fetchNextPageData();
    }

    @Override
    protected TopicFeedPresenter createPresenters() {
        TopicFeedPresenter presenter = new TopicFeedPresenter(this);
        presenter.setId(mTopic.id);
        presenter.setIsShowTopFeeds(isShowTopFeed);
        presenter.setIsShowTopFeeds(true);
        return presenter;
    }

    @Override
    public void onRefreshStart() {
        if (!isAdded()) {
            return;
        }
        mRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLayout.setRefreshing(true);
    }

    public void setOnAnimationListener(final OnResultListener listener) {
        mAnimationListener = listener;
    }

    private boolean isScrollEffective = false; // 是否滚动生效，避免两个页面的滚动事件冲突，只有在显示的时候才考虑滚动

    // private boolean isExecuteScroll = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isScrollEffective = isVisibleToUser;
    }

}
