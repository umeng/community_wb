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

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.SearchTopicActivity;
import com.umeng.comm.ui.activities.TopicDetailActivity;
import com.umeng.comm.ui.adapters.TopicAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;

import com.umeng.common.ui.fragments.TopicBaseFragment;
import com.umeng.common.ui.listener.TopicToTopicDetail;

import com.umeng.common.ui.presenter.impl.TopicBasePresenter;
import com.umeng.common.ui.presenter.impl.TopicFgPresenter;


/**
 * 主页的三个tab中的话题页面
 */
public class TopicFragment extends TopicBaseFragment {

    protected Dialog mProcessDialog;

    public TopicFragment() {
        super();
    }

    public static TopicFragment newTopicFragment() {
        return new TopicFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_search");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
    }

    @Override
    protected TopicBasePresenter createPresenters() {
        return new TopicFgPresenter(this);
    }

    @Override
    protected void initSearchView(View rootView) {
        View headerView = LayoutInflater.from(getActivity()).inflate(ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtils.visitNum == 0) {
                    if (CommonUtils.isLogin(getActivity())) {
                        Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int code, CommUser userInfo) {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.dismiss();
                                }
                                if (code == 0) {
                                    Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            }
                        });
                    }
                } else {
                    Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        TextView searchtv = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        searchtv.setText(ResFinder.getString("umeng_comm_search_topic"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchtv.getLayoutParams();
        params.bottomMargin = CommonUtils.dip2px(getActivity(), 8);
        mTopicListView.addHeaderView(headerView, null, false);
    }

    @Override
    protected void setAdapterGotoDetail() {
        ((TopicAdapter) mAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(getActivity(), TopicDetailActivity.class);
                intent.setComponent(componentName);
                intent.putExtra(Constants.TAG_TOPIC, topic);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected void initAdapter() {
        mAdapter = new TopicAdapter(getActivity());
        ((TopicAdapter) mAdapter).setFollowListener(new RecommendTopicAdapter.FollowListener<Topic>() {

            @Override
            public void onFollowOrUnFollow(Topic topic, ToggleButton toggleButton,
                                           boolean isFollow) {
                mPresenter.checkLoginAndExecuteOp(topic, toggleButton, isFollow);
            }
        });
        mTopicListView.setAdapter(mAdapter);
    }

    @Override
    protected void initRefreshView(View rootView) {
        super.initRefreshView(rootView);
        mRefreshLvLayout.setProgressViewOffset(false, 60,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mRefreshLvLayout.setRefreshing(true);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
        }
    }
}
