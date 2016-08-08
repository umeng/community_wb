package com.umeng.common.ui.presenter.impl;

import android.app.Activity;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.LoginHelper;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.mvpview.MvpRecyclerView;
import com.umeng.common.ui.mvpview.MvpSearchTopicFgView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;

import java.util.List;

/**
 * Created by umeng on 12/10/15.
 */
public class SearchTopicPresenter extends TopicFgPresenter {

    public SearchTopicPresenter(MvpRecommendTopicView recommendTopicView) {
        super(recommendTopicView);
    }

    @Override
    public void loadDataFromServer() {
        mTopicView.onRefreshEnd();
    }

    @Override
    public void loadDataFromDB() {

    }

    public void executeSearch(String searchKeyWord){
        Log.e("xxxxxx","presenter");
        mCommunitySDK.searchTopic(searchKeyWord, new Listeners.SimpleFetchListener<TopicResponse>() {
            @Override
            public void onComplete(TopicResponse response) {
                if (response.errCode == ErrorCode.UNLOGIN_ERROR){
                    LoginHelper.UnLogin((Activity) mContext, new LoginListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onComplete(int stCode, CommUser userInfo) {

                        }
                    });
                    return;
                }
                if (NetworkUtils.handleResponseAll(response)) {
                    //  如果是网络错误，其结果可能快于DB查询
                    if (CommonUtils.isNetworkErr(response.errCode)) {
                        mTopicView.onRefreshEndNoOP();
                    } else {
                        mTopicView.onRefreshEnd();
                    }
                    return;
                }
                final List<Topic> results = response.result;
                dealNextPageUrl(response.nextPageUrl, true);
                fetchTopicComplete(results, true);
                dealGuestMode(response.isVisit);
                mTopicView.onRefreshEnd();
            }

            @Override
            public void onStart() {
                mTopicView.onRefreshStart();
            }
        });
    }

    public void fetchNextPageData(){

        if(!TextUtils.isEmpty(mNextPageUrl)){

            mCommunitySDK.fetchNextPageData(mNextPageUrl, TopicResponse.class, new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(TopicResponse response) {
                mTopicView.onRefreshEnd();
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                final List<Topic> results = response.result;
                dealNextPageUrl(response.nextPageUrl, false);
                fetchTopicComplete(results, false);
            }
        });
        }
    }
}
