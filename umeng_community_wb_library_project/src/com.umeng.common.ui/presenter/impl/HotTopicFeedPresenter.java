package com.umeng.common.ui.presenter.impl;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.common.ui.mvpview.MvpFeedView;

import java.util.Comparator;


/**
 * Created by wangfei on 15/12/2.
 */
public class HotTopicFeedPresenter extends TopicFeedPresenter {
    private int hottype = 1;

    public HotTopicFeedPresenter(MvpFeedView view) {
        super(view);
    }

    public void loadDataFromServer(int days) {
        if (hottype != days) {
            mFeedView.getBindDataSource().clear();
            mFeedView.notifyDataSetChanged();
            hottype = days;
        }
        hottype = days;
        loadDataFromServer();
    }

    @Override
    protected void loadDataOnRefresh() {
        super.loadDataOnRefresh();
        mCommunitySDK.fetchTopicHotestFeeds(mId, mRefreshListener, hottype, 0);
    }

    @Override
    protected Comparator<FeedItem> getFeedCompartator() {
        return null;
    }

    @Override
    public void loadDataFromDB() {
    }

    @Override
    public boolean isAddToFeedList(FeedItem feedItem) {
        return false;
    }
}
