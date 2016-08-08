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

import android.util.TypedValue;
import android.view.View;

import com.umeng.comm.core.beans.BaseBean;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.presenter.impl.PostedFeedPresenter;
import com.umeng.common.ui.util.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * 用户已经已经发布的feed页面
 */
public class PostedFeedsFragment extends FeedListFragment<PostedFeedPresenter> {

    /**
     * Feed 删除监听器,删除页面时回调给个人信息页面使得feed数量减1 [ TODO : 考虑是否和转发效果一样,使用广播 ]
     */
    private OnDeleteListener mDeleteListener;
    protected boolean isScrollEffective = false;
    private int counter = 0;

    public PostedFeedsFragment() {
        setRetainInstance(true);
        isSetPaddingToListView(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (mListener != null) {
            mRefreshLayout.setOnScrollDirectionListener(new OnResultListener() {

                @Override
                public void onResult(int status) {

                    if (!isScrollEffective || mFeedsListView == null) {
                        return;
                    }
                    int firstVisible = mFeedsListView.getFirstVisiblePosition();
                    int headerCount = mFeedsListView.getHeaderViewsCount();

                    int lastVisible = mFeedsListView.getLastVisiblePosition();
                    if ((status == 1 && firstVisible >= headerCount)
                            || (status == 0 && firstVisible == headerCount)) {

                        mListener.onResult(status);
                    }

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isScrollEffective = true;
    }

    @Override
    protected PostedFeedPresenter createPresenters() {
        PostedFeedPresenter presenter = new PostedFeedPresenter(this);
        presenter.setId(mUser.id);
        presenter.setCommUser(mUser);
        presenter.setIsShowTopFeeds(false);
        return presenter;
    }

    @Override
    protected void showPostButtonWithAnim() {
    }

    public static PostedFeedsFragment newInstance() {
        return new PostedFeedsFragment();
    }

    @Override
    public void initAdapter() {
        // 设置只显示当前用户创建的feeds,过滤掉其他用户的feed
        setFeedFilter(new Filter<FeedItem>() {

            @Override
            public List<FeedItem> doFilte(List<FeedItem> originItems) {
                Iterator<FeedItem> myIterator = originItems.iterator();
                while (myIterator.hasNext()) {
                    final FeedItem feedItem = myIterator.next();
                    // id等于当前用户的id或者列表中已经包含该feed,那么移除该feed
                    if (!feedItem.creator.id.equals(mUser.id)
                            || mFeedLvAdapter.getDataSource().contains(feedItem)) {
                        myIterator.remove();
                    }
                }
                return originItems;
            }
        });

        super.initAdapter();

    }

    @Override
    public void onRefreshStart() {
        mRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLayout.setRefreshing(true);
    }

    /**
     * @param user
     */
    public void setCurrentUser(CommUser user) {
        mUser = user;
    }

    protected void updateAfterDelete(FeedItem feedItem) {
        super.updateAfterDelete(feedItem);
        if (mDeleteListener != null) {
            mDeleteListener.onDelete(feedItem);
        }
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    /**
     * 删除监听器
     *
     * @author mrsimple
     */
    public static interface OnDeleteListener {
        public void onDelete(BaseBean item);
    }

    public void setOnAnimationResultListener(OnResultListener listener) {
        mListener = listener;
    }

    private OnResultListener mListener = null;

    public void executeScrollToTop() {
        if (mFeedsListView.getFirstVisiblePosition() > 10) {
            mFeedsListView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isScrollEffective = isVisibleToUser;
    }
}
