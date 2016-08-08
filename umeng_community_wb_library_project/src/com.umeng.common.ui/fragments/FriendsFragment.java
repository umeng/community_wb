
package com.umeng.common.ui.fragments;

import android.util.TypedValue;
import android.view.View;

import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.FeedListPresenter;
import com.umeng.common.ui.presenter.impl.FriendFeedPresenter;
import com.umeng.common.ui.widgets.BaseView;

/**
 * 我的关注页面
 */
public class FriendsFragment extends FeedListFragment<FeedListPresenter> {

    protected BaseView mBaseView = null;

    public FriendsFragment(){
        isShowPostButton(false);
        isSetPaddingToListView(true);
    }

    @Override
    protected FeedListPresenter createPresenters() {
        FriendFeedPresenter friendFeedPresenter = new FriendFeedPresenter(this);
        friendFeedPresenter.setIsShowTopFeeds(isShowTopFeed);
        return friendFeedPresenter;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mFeedsListView.setFooterDividersEnabled(false);
        // 隐藏发送跟设置按钮

        mBaseView = (BaseView) mRootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_feed"));
    }


    protected void checkListViewData() {
        if (mFeedLvAdapter.isEmpty()) {
            mBaseView.showEmptyView();
        } else {
            mBaseView.hideEmptyView();
        }
    }

    @Override
    public void onRefreshEnd() {
        super.onRefreshEnd();
        checkListViewData();
    }

    public static FriendsFragment newFriendsFragment() {
        return new FriendsFragment();
    }

    @Override
    public void onRefreshStart() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            mRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                            .getDisplayMetrics()));
            mRefreshLayout.setRefreshing(true);
        }
    }

}
