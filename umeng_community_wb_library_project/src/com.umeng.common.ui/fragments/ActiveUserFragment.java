package com.umeng.common.ui.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.ActiveUserAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.listener.LikeonClickListener;
import com.umeng.common.ui.mvpview.MvpActiveUserFgView;
import com.umeng.common.ui.presenter.impl.ActiveUserFgPresenter;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

/**
 * Created by wangfei on 16/7/13.
 */
public class ActiveUserFragment extends BaseFragment<List<CommUser>, ActiveUserFgPresenter>
        implements MvpActiveUserFgView {
    protected RefreshLvLayout mRefreshLvLayout;
    protected ActiveUserAdapter mAdapter;
    private Listeners.OnResultListener mAnimationListener = null;
    protected Button nextButton;

    protected BaseView mBaseView = null;

//    private boolean isVisit = true;

    protected ListView mListView = null;
    protected boolean isScrollEffective = false;

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_active_user_layout");
    }

    public ActiveUserAdapter createAdapter(){
        return new ActiveUserAdapter(getActivity());
    }

    @Override
    protected void initWidgets() {
        mRefreshLvLayout = (RefreshLvLayout) mRootView.findViewById(ResFinder
                .getId("umeng_comm_swipe_layout"));
        mAdapter = createAdapter();
        mListView = mRefreshLvLayout.findRefreshViewById(ResFinder
                .getId("umeng_comm_active_user_listview"));
        mRefreshLvLayout.setAdapter(mAdapter);
        mRefreshLvLayout.setEnabled(false);
        mAdapter.setFromFindPage(true);
        initLikeonListener();
        mAdapter.setFollowListener(mListener);
        if (mAnimationListener != null) {
            mRefreshLvLayout.setOnScrollDirectionListener(new Listeners.OnResultListener() {

                @Override
                public void onResult(int status) {
                    if (!isScrollEffective || mListView == null) {
                        return;
                    }
                    // 1:向上滑动且第一项显示 (up)
                    // 0:向下且大于第一项 (down)
                    int firstVisible = mListView.getFirstVisiblePosition();
                    int headerCount = mListView.getHeaderViewsCount();
                    if ((status == 1 && firstVisible >= headerCount)
                            || (status == 0 && firstVisible == headerCount)) {
                        mAnimationListener.onResult(status);
                    }
                }
            });
        }
        mRefreshLvLayout.setEnabled(true);

        mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                mPresenter.loadMoreData();
//                if(isCanLoadMore()){
//                    mPresenter.loadMoreData();
//                }else{
//                    onRefreshEnd();
//                }
            }
        });

        mRefreshLvLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });

        mBaseView = (BaseView) mRootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_user"));
        initnextBtton();
    }
    protected RecommendTopicAdapter.FollowListener<CommUser> mListener = new RecommendTopicAdapter.FollowListener<CommUser>() {

        @Override
        public void onFollowOrUnFollow(final CommUser user, final ToggleButton toggleButton,
                                       final boolean isFollow) {
            if (nextButton != null) {
                if (nextButton.getText().equals(ResFinder.getString("umeng_comm_skip"))) {
                    nextButton.setText(ResFinder.getString("umeng_comm_next"));
                }
            }
            if (!isFollow) {
                mPresenter.followUser(user, toggleButton);
            } else {
                mPresenter.cancelFollowUser(user, toggleButton);
            }
        }
    };
    @Override
    public void onRefreshStart() {
        mRefreshLvLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshEnd() {
        mRefreshLvLayout.setRefreshing(false);
        mRefreshLvLayout.setLoading(false);
    }

    @Override
    public List<CommUser> getBindDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideEmptyView() {
        mBaseView.hideEmptyView();
    }

    @Override
    public void showEmptyView() {
        mBaseView.showEmptyView();
    }

    public void setOnAnimationListener(final Listeners.OnResultListener listener) {
        mAnimationListener = listener;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isScrollEffective = isVisibleToUser;
    }

    @Override
    public void showVisitView() {
        if (!isAdded()) {
            return;
        }
        mRefreshLvLayout.disposeLoginTipsView(true);
    }

    @Override
    public void hideVisitView() {
        if (!isAdded()) {
            return;
        }
        mRefreshLvLayout.disposeLoginTipsView(false);
    }
    public  void initLikeonListener(){
        mAdapter.setLikeonClickListener(new LikeonClickListener() {
            @Override
            public void onClickListener(CommUser user) {
                command.navigateToUseProfile(user);

            }
        });
    }
    public  void initnextBtton(){

    }
    @Override
    protected ActiveUserFgPresenter createPresenters() {
        Bundle bundle = getArguments();
        Topic topic = bundle != null ? (Topic) bundle.getParcelable(Constants.TAG_TOPIC)
                : new Topic();
        mPresenter = new ActiveUserFgPresenter(this, topic);
        return mPresenter;
    }

    public static ActiveUserFragment newActiveUserFragment(Topic topic) {
        ActiveUserFragment userBaseFragment = new ActiveUserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.TAG_TOPIC, topic);
        userBaseFragment.setArguments(bundle);
        return userBaseFragment;
    }
}
