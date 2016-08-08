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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.FeedItem.CATEGORY;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners.OnItemViewClickListener;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;

import com.umeng.common.ui.adapters.FeedBaseAdapter;
import com.umeng.common.ui.adapters.viewholders.NavigationCommand;
import com.umeng.common.ui.presenter.impl.FeedListPresenter;


/**
 * 这是Feed流列表页面，包含当前最新的消息列表.从该页面可以跳转到话题搜索页面、消息发布页面，可以浏览消息流中的图片、评论某项消息、进入某个好友的主页等.
 */
public abstract class FeedListFragment<P extends FeedListPresenter> extends PostBtnAnimFragment<P, FeedBaseAdapter> {

    protected View headerView;
    protected TextView mTipView; // 更新条数提示
    protected boolean isShowSearch;
    protected boolean isShowTopFeed = false;

    protected FeedBaseAdapter createListViewAdapter() {
        return command.createFeedAdapter(getActivity());
    }
    private boolean isDisplayTopic = true;
    /**
     * 初始化适配器
     */
    protected void initAdapter() {
        if (mFeedLvAdapter == null) {
            mFeedLvAdapter = createListViewAdapter();
            mFeedLvAdapter.setCommentClickListener(new OnItemViewClickListener<FeedItem>() {

                @Override
                public void onItemClick(int position, FeedItem item) {

                    //先进入feed详情页面，再弹出评论编辑键盘
                    command.navigateToFeedDetailByComment(item,isDisplayTopic);
                }
            });

        }
        mRefreshLayout.setAdapter(mFeedLvAdapter);
        mFeedsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int realPosition = position - mFeedsListView.getHeaderViewsCount();
                final FeedItem feedItem = mFeedLvAdapter.getItem(realPosition < 0 ? 0
                        : realPosition);
                if (feedItem != null && (feedItem.status >= FeedItem.STATUS_SPAM && feedItem.status != FeedItem.STATUS_LOCK)
                        && feedItem.category == CATEGORY.FAVORITES) {
                    ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_spam_deleted");
                    return;
                }
                command.navigateToFeedDetail(feedItem,isDisplayTopic);

            }
        });
        // 添加Header
        headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtils.visitNum == 0) {
                    if (CommonUtils.isLogin(getActivity())) {
                        command.navigateToSearch();
                    } else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int stCode, CommUser userInfo) {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.dismiss();
                                }
                                if (stCode == 0) {
                                    command.navigateToSearch();
                                }
                            }
                        });
                    }
                } else {
                    command.navigateToSearch();
                }
            }
        });
        mTipView = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_feeds_tips"));
        if (isShowSearch) {
            mFeedsListView.addHeaderView(headerView, null, false);
        }
    }

    public void setShowtopFeed(boolean showtopFeed) {
        isShowTopFeed = showtopFeed;
    }

    public void isShowSearchBar(boolean isShow) {
        isShowSearch = isShow;
        if (headerView != null && mFeedsListView != null) {

            if (isShow) {
                if (mFeedsListView.getHeaderViewsCount() == 0) {
                    mFeedsListView.addHeaderView(headerView, null, false);
                }
                headerView.setVisibility(View.VISIBLE);
            } else {
                headerView.setVisibility(View.GONE);
                if (mFeedsListView != null)
                    mFeedsListView.removeHeaderView(headerView);
            }
        }
    }
    protected void setIsDisplayTopic(boolean isDisplayTopic) {
        this.isDisplayTopic = isDisplayTopic;
    }
    /**
     * 跳转至发送新鲜事页面</br>
     */
    @Override
    public void gotoPostFeedActivity() {
        super.gotoPostFeedActivity();
        command.navigateToPostFeed(null);
    }
}
