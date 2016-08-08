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

package com.umeng.comm.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.ui.adapters.viewholders.NavigationCommandImpl;
import com.umeng.common.ui.activities.TopicDetailBaseActivity;
import com.umeng.common.ui.configure.TopicItem;
import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.fragments.ActiveUserFragment;
import com.umeng.common.ui.fragments.HotTopicFeedFragment;
import com.umeng.common.ui.fragments.LastestTopicFeedFragment;
import com.umeng.common.ui.fragments.RecommendTopicFeedFragment;
import com.umeng.common.ui.fragments.TopicFeedFragment;
import com.umeng.common.ui.presenter.impl.LatestTopicFeedPresenter;


/**
 * 话题详情页
 */
public class TopicDetailActivity extends TopicDetailBaseActivity {

    /**
     * 话题详情的Fragment
     */
//    private TopicFeedFragment mDetailFragment;
//    private ActiveUserFragment mActiveUserFragment;
//    private HotTopicFeedFragment hotTopicFeedFragment;
//    private RecommendTopicFeedFragment recommendTopicFeedFragment;
//    private LastestTopicFeedFragment lastestTopicFeedFragment;



    @Override
    protected void initTitles() {
        if (parseJson.topicItems.size() == 0) {
            mTitles = getResources().getStringArray(
                    ResFinder.getResourceId(ResType.ARRAY, "umeng_comm_topic_detail_tabs"));

        }
        else {
            mTitles = new String[parseJson.topicItems.size()];
            for (int i = 0 ;i<mTitles.length;i++){
                mTitles[i] = parseJson.topicItems.get(i).title;
            }
        }
        command = new NavigationCommandImpl(this);
        initFragment();

    }

    public void initFragment(){
        if (parseJson.topicItems.size() == 0){
            TopicFeedFragment mDetailFragment = TopicFeedFragment.newTopicFeedFrmg(mTopic);
            mDetailFragment.setNavigation(command);
            mDetailFragment.setOnAnimationListener(mListener);
            LastestTopicFeedFragment lastestTopicFeedFragment = LastestTopicFeedFragment.newTopicFeedFrmg(mTopic);
            lastestTopicFeedFragment.setNavigation(command);
            lastestTopicFeedFragment.setOnAnimationListener(mListener);
            RecommendTopicFeedFragment recommendTopicFeedFragment = RecommendTopicFeedFragment.newTopicFeedFrmg(mTopic);
            recommendTopicFeedFragment.setNavigation(command);
            recommendTopicFeedFragment.setOnAnimationListener(mListener);
            HotTopicFeedFragment hotTopicFeedFragment = HotTopicFeedFragment.newTopicFeedFrmg(mTopic);
            hotTopicFeedFragment.setNavigation(command);
            hotTopicFeedFragment.setOnAnimationListener(mListener);
            ActiveUserFragment mActiveUserFragment = ActiveUserFragment.newActiveUserFragment(mTopic);
            mActiveUserFragment.setNavigation(command);
            fragments.clear();
            fragments.add(mDetailFragment);
            fragments.add(lastestTopicFeedFragment);
            fragments.add(recommendTopicFeedFragment);
            fragments.add(hotTopicFeedFragment);
            fragments.add(mActiveUserFragment);
        }else {
            fragments.clear();
            for(TopicItem item:parseJson.topicItems){
                fragments.add(getFragmentByname(item));
            }
        }

    }

    @Override
    protected int getLayout() {
         return ResFinder.getLayout("umeng_comm_topic_detail_layout");
    }



    @Override
    protected void gotoPostFeedActivity() {
        Intent postIntent = new Intent(TopicDetailActivity.this, PostFeedActivity.class);
        postIntent.putExtra(Constants.TAG_TOPIC, mTopic);
        startActivity(postIntent);
    }

    /**
     * 获取对应的Fragment。
     * 
     * @param pos
     * @return
     */
    protected Fragment getFragment(int pos) {
        if (pos<fragments.size()){
            return fragments.get(pos);
        }
        return null;
    }

    private OnResultListener mListener = new OnResultListener() {

        @Override
        public void onResult(int status) {
//            if (status == 1) {// dismiss
//                mCustomAnimator.startDismissAnimation(mHeaderView);
//            } else if (status == 0) { // show
//                mCustomAnimator.startShowAnimation(mHeaderView);
//            }
        }
    };





}
