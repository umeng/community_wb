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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;

import com.umeng.common.ui.activities.SearchBaseActivity;
import com.umeng.common.ui.adapters.SearchUsersAdapter;
import com.umeng.common.ui.mvpview.MvpSearchFgView;
import com.umeng.common.ui.presenter.impl.SearchPresenter;
import com.umeng.common.ui.presenter.impl.SearchTopicPresenter;
import com.umeng.common.ui.widgets.EmptyView;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索Fragment
 */
public class SearchFragment extends FeedListFragment<SearchPresenter> implements
        OnClickListener, MvpSearchFgView {

    private RecyclerView mRecyclerView;
    private View mMoreView;
    private SearchUsersAdapter mAdapter;
    private EmptyView mUserEmptyView;
    private EmptyView mFeedEmptyView;
    private EditText mSearchEditText;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_search");
    }

    @Override
    protected SearchPresenter createPresenters() {
        SearchPresenter searchPresenter = new SearchPresenter(this);
        searchPresenter.setIsShowTopFeeds(false);
        return new SearchPresenter(this);
    }
    public void executeSearch(){
        String keyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastMsg.showShortMsgByResName("umeng_comm_topic_search_no_keyword");
            return;
        }
        ((SearchPresenter) mPresenter).executeSearch(mSearchEditText
                .getText().toString());
    }
    @Override
    protected void initViews() {
        super.initViews();
        mRecyclerView = (RecyclerView) mRootView.findViewById(ResFinder
                .getId("umeng_comm_relative_user_recyclerView"));
        mAdapter = new SearchUsersAdapter(getActivity(),command);
        mAdapter.getDataSource().clear();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mMoreView = mRootView.findViewById(ResFinder.getId("search_more_img_view"));
        mMoreView.setOnClickListener(this);
        // 隐藏发送按钮

        showRelativeUserView(null);

        mRootView.findViewById(ResFinder.getId("umeng_comm_back")).setOnClickListener(this);
        // TODO 比较合理的方式先检测输入是否为空，接着再检查是否登录。此时需要将两个回调单独成成员变量，

        mRootView.findViewById(ResFinder.getId("umeng_comm_topic_search")).setOnClickListener(

                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                                                String keyword = mSearchEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(keyword)) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_search_no_keyword");
                            return;
                        }
                        ((SearchPresenter) mPresenter).executeSearch(mSearchEditText
                                .getText().toString());
                    }

                }
        );

        mSearchEditText = (EditText) mRootView.findViewById(ResFinder
                .getId("umeng_comm_topic_edittext"));
        mSearchEditText.setHint(ResFinder.getString("umeng_comm_search_content"));
        mSearchEditText.requestFocus();
        // showInputMethod();
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    ((SearchPresenter) mPresenter).executeSearch(textView.getText()
                            .toString().trim());
                }
                return false;
            }
        });
        mUserEmptyView = (EmptyView) mRootView.findViewById(ResFinder
                .getId("umeng_comm_user_empty"));
        mUserEmptyView.setShowText("umeng_comm_no_search_user");
        mFeedEmptyView = (EmptyView) mRootView.findViewById(ResFinder
                .getId("umeng_comm_feed_empty"));
        mFeedEmptyView.setShowText("umeng_comm_no_search_feed");
        showInputMethod();
        mRefreshLayout.setEnabled(false);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();

    }

    @Override
    protected void onBaseResumeDeal() {

    }

    /**
     * 添加相关用户显示</br>
     * 
     * @param users
     */
    @Override
    public void showRelativeUserView(List<CommUser> users) {
        mAdapter.getDataSource().clear();
        if (users == null || users.size() == 0) {
            mMoreView.setVisibility(View.GONE);
            // invalidate
            mAdapter.notifyDataSetChanged();
            return;
        }
        int itemWidth = mAdapter.computeWidth();
        LayoutParams params = mRecyclerView.getLayoutParams();
        if (users.size() > 4) {
            mMoreView.setVisibility(View.VISIBLE);
            params.width = (SearchUsersAdapter.MAX_SHOW_NUM - 1) * itemWidth;
        } else {
            params.width = users.size() * itemWidth;
            mMoreView.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutParams(params);
        mRecyclerView.invalidate();
        mAdapter.getDataSource().addAll(users);
        mAdapter.notifyDataSetChanged();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(400);
        alphaAnimation.setFillAfter(true);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(
                alphaAnimation, 0.4f);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);

        mRecyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    ((SearchPresenter) mPresenter).loadMoreUser();
                }
            }
        });
    }

    @Override
    public void onPause() {
        hideInputMethod();
        super.onPause();
    }

    /**
     * 隐藏输入法</br>
     */
    @Override
    public void hideInputMethod() {
        if (getActivity()!=null&&!getActivity().isFinishing()) {
            ((SearchBaseActivity) getActivity()).hideInputMethod(mSearchEditText);
        }
    }

    /**
     * 显示输入法</br>
     */
    private void showInputMethod() {
        ((SearchBaseActivity) getActivity()).showInputMethod(mSearchEditText);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_back")) {// 返回事件
            hideInputMethod();
            getActivity().finish();
        } else if (id == mMoreView.getId()) {// 点击更多按钮
            // 跳转到相关用户页面

            ArrayList<CommUser> users = new ArrayList<CommUser>(mAdapter.getDataSource());

            String nextUrl = ((SearchPresenter) mPresenter).getUserNextUrl();
            command.navigateToRelativeUser(users,nextUrl);
        }
    }

    @Override
    public void onRefreshStart() {
        mRefreshLayout.setLoading(true);
    }

    @Override
    public void onRefreshEnd() {
        mRefreshLayout.setLoading(false);
    }

    @Override
    public List<CommUser> getUserDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        mFeedLvAdapter.notifyDataSetChanged();
    }

    @Override
    public void showFeedEmptyView() {
        mFeedEmptyView.show();
    }

    @Override
    public void hideFeedEmptyView() {
        mFeedEmptyView.hide();
    }

    @Override
    public void showUserEmptyView() {
        mUserEmptyView.show();
    }

    @Override
    public void hideUserEmptyView() {
        mUserEmptyView.hide();
    }

    @Override
    public void clearListView() {
        super.clearListView();
        mAdapter.getDataSource().clear();
        mAdapter.notifyDataSetChanged();
    }
}
