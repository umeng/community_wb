package com.umeng.common.ui.presenter.impl;


import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;

import com.umeng.common.ui.mvpview.MvpRecommendTopicView;


import java.util.List;

/**
 * Created by wangfei on 15/12/22.
 */
public class CategoryTopicPresenter extends TopicFgPresenter {
    String uid;
    public CategoryTopicPresenter(MvpRecommendTopicView mTopicView,String uid) {
        super(mTopicView);
        this.uid = uid;
    }


    public void loadDataFromServer() {
        mCommunitySDK.fetchCategoryTopics(new Listeners.FetchListener<TopicResponse>() {

            @Override
            public void onStart() {
                mTopicView.onRefreshStart();
            }

            @Override
            public void onComplete(final TopicResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    mTopicView.onRefreshEnd(); // [注意]:不可移动，该方法的回调会决定是否显示空视图
                    return;
                }
                final List<Topic> results = response.result;
                dealNextPageUrl(response.nextPageUrl, true);
                fetchTopicComplete(results, true);
                dealGuestMode(response.isVisit);
                mTopicView.onRefreshEnd();
            }
        }, uid);
    }

    // no cache
    @Override
    public void loadDataFromDB() {

    }
}
