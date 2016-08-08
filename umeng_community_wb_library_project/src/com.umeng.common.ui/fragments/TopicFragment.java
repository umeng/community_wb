package com.umeng.common.ui.fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.BackupAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.adapters.TopicAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.listener.TopicToTopicDetail;
import com.umeng.common.ui.mvpview.MvpRecommendTopicView;
import com.umeng.common.ui.presenter.impl.TopicBasePresenter;
import com.umeng.common.ui.presenter.impl.TopicFgPresenter;
import com.umeng.common.ui.util.FontUtils;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

public class TopicFragment extends BaseFragment<List<Topic>, TopicBasePresenter>
        implements MvpRecommendTopicView {
    public View headerView;
    protected BackupAdapter<Topic, ?> mAdapter;
    protected Dialog mProcessDialog;
    protected ListView mTopicListView;
    protected RefreshLvLayout mRefreshLvLayout;
    protected BaseView mBaseView;
    protected boolean isShowSearchBar = true;
//    private boolean isVisit = true;

    @Override
    protected void initWidgets() {
        FontUtils.changeTypeface(mRootView);
        initRefreshView(mRootView);
        initTitleView(mRootView);
        initSearchView(mRootView);
        initAdapter();
        setAdapterGotoDetail();
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
    }

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

    protected void setAdapterGotoDetail() {
        ((RecommendTopicAdapter) mAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                command.navigateToTopicDetail(topic);
            }
        });
    }

    protected void initTitleView(View rootView) {
    }

    protected void initSearchView(View rootView) {
        headerView = LayoutInflater.from(getActivity()).inflate(ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtils.visitNum == 0) {
                    if (CommonUtils.isLogin(getActivity())) {
                        command.navigateToSearchTopic();
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
                                    command.navigateToSearchTopic();
                                }
                            }
                        });
                    }
                } else {
                    command.navigateToSearchTopic();
                }
            }
        });
        TextView searchtv = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        searchtv.setText(ResFinder.getString("umeng_comm_search_topic"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchtv.getLayoutParams();
        params.bottomMargin = CommonUtils.dip2px(getActivity(), 8);
        if (isShowSearchBar ){
            mTopicListView.addHeaderView(headerView, null, false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
        }
    }

    public void isShowSearchBar(boolean isShow) {
        isShowSearchBar = isShow;
        if (headerView != null) {
            if (isShow) {
                headerView.setVisibility(View.VISIBLE);
                if(mTopicListView.getHeaderViewsCount()==0){
                    mTopicListView.addHeaderView(headerView, null, false);
                }

            } else {
                headerView.setVisibility(View.GONE);
                mTopicListView.removeHeaderView(headerView);
            }
        }

    }

    /**
     * 初始化刷新相关的view跟事件</br>
     *
     * @param rootView
     */
    protected void initRefreshView(View rootView) {
        int refreshResId = ResFinder.getId("umeng_comm_topic_refersh");
        mRefreshLvLayout = (RefreshLvLayout) rootView.findViewById(refreshResId);
        mRefreshLvLayout.setDefaultFooterView();
        mRefreshLvLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });
        mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.loadMoreData();

            }
        });

        // listview
        int listViewResId = ResFinder.getId("umeng_comm_topic_listview");
        mTopicListView = mRefreshLvLayout.findRefreshViewById(listViewResId);

        // emptyview
        mBaseView = (BaseView) rootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mRefreshLvLayout.setProgressViewOffset(false, 60,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLvLayout.setRefreshing(true);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_search");
    }

    @Override
    protected TopicBasePresenter createPresenters() {
        return new TopicFgPresenter(this);
    }

    @Override
    public List<Topic> getBindDataSource() {
        if (mAdapter != null) {
            return mAdapter.getDataSource();
        } else
            return null;
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshStart() {
        if (mRefreshLvLayout != null) {
            mRefreshLvLayout.setRefreshing(true);
        }
    }

    @Override
    public void onRefreshEnd() {
        onRefreshEndNoOP();
        if (mAdapter != null) {
            if (mAdapter.isEmpty()) {
                mBaseView.showEmptyView();
            } else {
                mBaseView.hideEmptyView();
            }
        }
    }

    @Override
    public void onRefreshEndNoOP() {
        if (mRefreshLvLayout != null) {
            mRefreshLvLayout.setRefreshing(false);
            mRefreshLvLayout.setLoading(false);
        }
        if (mBaseView != null) {
            mBaseView.hideEmptyView();
        }
    }

//    @Override
//    public void setIsVisitBtn(boolean isVisit) {
//        if (!isAdded()) {
//            return;
//        }
//        this.isVisit = isVisit;
//        if (!isVisit && !CommonUtils.isLogin(getActivity())) {
//            mRefreshLvLayout.disposeLoginTipsView(true);
//        } else {
//            mRefreshLvLayout.disposeLoginTipsView(false);
//        }
//    }
//
//    @Override
//    public void onUserLogin() {
//        if (!isVisit && CommonUtils.isLogin(getActivity())) {
//            isVisit = true;
//            mRefreshLvLayout.disposeLoginTipsView(false);
//        }
//    }

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

    @Override
    public void showLoginView() {

    }

    @Override
    public void hideLoginView() {

    }

//    protected boolean isCanLoadMore() {
//        boolean isLogin = (getActivity() != null) && CommonUtils.isLogin(getActivity());
//        return isVisit || isLogin;
//    }

}
