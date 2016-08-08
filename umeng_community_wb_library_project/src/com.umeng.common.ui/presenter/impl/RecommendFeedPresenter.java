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

package com.umeng.common.ui.presenter.impl;


import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.FeedItem.CATEGORY;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.common.ui.mvpview.MvpFeedView;

import java.util.Comparator;
import java.util.List;


public class RecommendFeedPresenter extends FeedListPresenter {

    public RecommendFeedPresenter(MvpFeedView feedViewInterface) {
        super(feedViewInterface);
    }

    @Override
    protected void loadDataOnRefresh() {
        super.loadDataOnRefresh();
        mCommunitySDK.fetchRecommendedFeeds(mRefreshListener);
    }

    @Override
    protected void beforeDeliveryFeeds(FeedsResponse response) {
        isNeedRemoveOldFeeds.set(false);
        for (FeedItem item : response.result) {
            item.category = CATEGORY.RECOMMEND;
        }
        DatabaseAPI.getInstance().getFeedDBAPI().clearRecommendFeed();
    }

    @Override
    public void loadDataFromDB() {
        mDatabaseAPI.getFeedDBAPI().loadRecommendFeedsFromDB(mDbFetchListener);
    }

    @Override
    public void sortFeedItems(List<FeedItem> items) {
    }

    @Override
    protected void saveDataToDB(List<FeedItem> newFeedItems) {
        mDatabaseAPI.getFeedDBAPI().saveRecommendFeedToDB(newFeedItems);
    }

    @Override
    protected void loadDataAfterLogin(Listeners.FetchListener<FeedsResponse> mLoginRefreshListener) {
        super.loadDataAfterLogin(mLoginRefreshListener);
        mCommunitySDK.fetchRecommendedFeeds(mLoginRefreshListener);
    }

    @Override
    public boolean isRefreshDataAfterLogin() {
        return true;
    }

    @Override
    protected Comparator<FeedItem> getFeedCompartator() {
        return null;
    }
}
