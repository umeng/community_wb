package com.umeng.common.ui.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.CategoryTopicPresenter;
import com.umeng.common.ui.presenter.impl.TopicBasePresenter;

/**
 * Created by wangfei on 15/12/22.
 */
public class CategoryTopicFragment extends TopicFragment {
    private String uid;
    public CategoryTopicFragment() {
        super();
    }

    public static CategoryTopicFragment newTopicFragment(String uid) {
        CategoryTopicFragment categoryTopicFragment = new CategoryTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("topic_uid", uid);
        categoryTopicFragment.setArguments(bundle);
        return categoryTopicFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_search");
    }

    @Override
    protected TopicBasePresenter createPresenters() {
        uid = getArguments()==null?null:getArguments().getString("topic_uid");
        return new CategoryTopicPresenter(this,uid);
    }

    // dont impl searchView
    @Override
    protected void initSearchView(View rootView) {
    }

    @Override
    protected void initRefreshView(View rootView) {
        super.initRefreshView(rootView);
        mRefreshLvLayout.setProgressViewOffset(false, 60,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLvLayout.setRefreshing(true);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }
}
