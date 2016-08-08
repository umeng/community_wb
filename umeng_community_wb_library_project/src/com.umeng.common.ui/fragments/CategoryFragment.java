package com.umeng.common.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Category;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.BackupAdapter;
import com.umeng.common.ui.adapters.CategoryAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.mvpview.MvpCategoryView;
import com.umeng.common.ui.presenter.impl.CategoryPresenter;
import com.umeng.common.ui.util.FontUtils;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;

import java.util.List;

/**
 * Created by wangfei on 16/7/14.
 */
public  class CategoryFragment extends BaseFragment<List<Category>, CategoryPresenter>
        implements MvpCategoryView {
    protected CategoryAdapter mAdapter;
    protected ListView mCategoryListView;
    protected RefreshLvLayout mRefreshLvLayout;
    protected BaseView mBaseView;
    protected View mSearchLayout;
    protected boolean isShowSearchBar = true;
    private InputMethodManager mInputMan;
    public View headerView;
    protected Dialog mProcessDialog;

    @Override
    protected void initWidgets() {
        super.initWidgets();
        FontUtils.changeTypeface(mRootView);
        initRefreshView(mRootView);
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
        initSearchView();
        initAdapter();
        mInputMan = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }
    public void isShowSearchBar(boolean isShow) {
        isShowSearchBar = isShow;
        if (headerView != null) {
            if (isShow) {
                headerView.setVisibility(View.VISIBLE);
                if(mCategoryListView.getHeaderViewsCount()==0){
                    mCategoryListView.addHeaderView(headerView, null, false);
                }

            } else {
                headerView.setVisibility(View.GONE);
                mCategoryListView.removeHeaderView(headerView);
            }
        }

    }
    protected  void initSearchView(){
         headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CommonUtils.visitNum == 0){
                    if (CommonUtils.isLogin(getActivity())){
                       command.navigateToSearchTopic();
                    } else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int code, CommUser userInfo) {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.dismiss();
                                }
                                if (code == 0) {
                                    command.navigateToSearchTopic();
                                }
                            }
                        });
                    }
                }else {
                    command.navigateToSearchTopic();
                }
            }
        });
        TextView searchtv = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        searchtv.setText(ResFinder.getString("umeng_comm_search_topic"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)searchtv.getLayoutParams();
        params.bottomMargin = CommonUtils.dip2px(getActivity(), 8);
        mSearchLayout = findViewById(ResFinder.getId("umeng_comm_topic_search_title_layout"));
        mSearchLayout.setVisibility(View.GONE);
        if (isShowSearchBar){
            mCategoryListView.addHeaderView(headerView, null, false);
        }
    }

    protected  void initAdapter(){
        CategoryAdapter adapter = new CategoryAdapter(getActivity());

        mAdapter = adapter;

        mCategoryListView.setAdapter(mAdapter);
        mCategoryListView.setDividerHeight(CommonUtils.dip2px(getActivity(),1));
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                command.navigateToTopic(getBindDataSource().get(i-1).id);

            }
        });
    }

    protected void initRefreshView(View rootView) {
        int refreshResId = ResFinder.getId("umeng_comm_topic_refersh");
        mRefreshLvLayout = (RefreshLvLayout) rootView.findViewById(refreshResId);

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


        int listViewResId = ResFinder.getId("umeng_comm_topic_listview");
        mCategoryListView = mRefreshLvLayout.findRefreshViewById(listViewResId);

        mRefreshLvLayout.setDefaultFooterView();


        mBaseView = (BaseView) rootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_category_recommend");
    }

    @Override
    public List<Category> getBindDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshEndNoOP() {
        mRefreshLvLayout.setRefreshing(false);
        mRefreshLvLayout.setLoading(false);
        mBaseView.hideEmptyView();
    }


    @Override
    public void onRefreshStart() {
        mRefreshLvLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshEnd() {
        onRefreshEndNoOP();
        if (mAdapter.getCount() == 0) {
            mBaseView.showEmptyView();
        } else {
            mBaseView.hideEmptyView();
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
        }
    }
    @Override
    protected CategoryPresenter createPresenters() {
        return new CategoryPresenter(this);
    }

}
