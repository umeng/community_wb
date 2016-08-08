package com.umeng.common.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.adapters.viewholders.NavigationCommand;
import com.umeng.common.ui.colortheme.ColorQueque;
import com.umeng.common.ui.configure.TopicItem;
import com.umeng.common.ui.fragments.ActiveUserFragment;
import com.umeng.common.ui.fragments.BaseFragment;
import com.umeng.common.ui.fragments.HotTopicFeedFragment;
import com.umeng.common.ui.fragments.LastestTopicFeedFragment;
import com.umeng.common.ui.fragments.RecommendTopicFeedFragment;
import com.umeng.common.ui.fragments.TopicFeedFragment;
import com.umeng.common.ui.mvpview.MvpTopicDetailView;
import com.umeng.common.ui.presenter.impl.TopicDetailPresenter;
import com.umeng.common.ui.presenter.impl.TopicFeedPresenter;
import com.umeng.common.ui.widgets.TopicIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangfei on 16/1/22.
 */
public abstract class TopicDetailBaseActivity extends BaseFragmentActivity implements View.OnClickListener,
        MvpTopicDetailView {
    protected TopicDetailPresenter mPresenter;
    protected Topic mTopic;
    protected String[] mTitles = null;
    protected TopicIndicator mIndicator;
    protected ViewPager mViewPager;
    protected FragmentPagerAdapter mAdapter;
    protected View postBtn;
    protected ToggleButton favouriteBtn;
    protected boolean isClick = true;
    protected TextView titleTextView;
    protected NavigationCommand command;
    protected ArrayList<BaseFragment> fragments = new ArrayList<BaseFragment>();
    @Override
    public void setToggleButtonStatus(boolean status) {
        favouriteBtn.setClickable(true);
        favouriteBtn.setChecked(status);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_setting_back")) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mPresenter = new TopicDetailPresenter(this, this);
        setContentView(getLayout());
        mTopic = getIntent().getExtras().getParcelable(Constants.TAG_TOPIC);
        if (mTopic == null) {
            finish();
            return;
        }
        initTitles();
//        mTitles = getResources().getStringArray(
//                ResFinder.getResourceId(ResFinder.ResType.ARRAY, "umeng_commm_topic_detail_tabs"));
        // 根据话题的id信息初始化fragment
        initView();
        mPresenter.onCreate(arg0);
    }
    protected abstract void initTitles();
    protected void initView() {
        mIndicator = (TopicIndicator) findViewById(ResFinder.getId("indicator"));
        mViewPager = (ViewPager) findViewById(ResFinder.getId("viewPager"));
        mIndicator.setTabItemTitles(mTitles);
        mIndicator.setVisibleTabCount(mTitles.length);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int pos) {
                return getFragment(pos);
            }
        };
        mViewPager.setAdapter(mAdapter);
        // 设置关联的ViewPager
        mIndicator.setViewPager(mViewPager, 0);

        // 初始化Header的控件跟数据
//        initHeader();
        initTitle();
    }
    protected abstract int getLayout();
    protected abstract Fragment getFragment(int pos) ;
    protected void initTitle() {
        findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(this);
        titleTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_setting_title"));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        titleTextView.setTextColor(ColorQueque.getColor("umeng_comm_category_title_color"));
        titleTextView.setText(mTopic.name);
        findViewById(ResFinder.getId("umeng_comm_save_bt")).setVisibility(View.GONE);
        favouriteBtn = (ToggleButton) findViewById(ResFinder.getId("umeng_comm_favourite_btn"));
        favouriteBtn.setVisibility(View.VISIBLE);
        postBtn = findViewById(ResFinder.getId("umeng_comm_post_btn"));
        postBtn.setVisibility(View.GONE);
        setTopicStatus();
        mPresenter.SetFavouriteButton(favouriteBtn);
        favouriteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                favouriteBtn.setClickable(false);


                CommonUtils.checkLoginAndFireCallback(TopicDetailBaseActivity.this,
                        new Listeners.SimpleFetchListener<LoginResponse>() {

                            @Override
                            public void onComplete(LoginResponse response) {
                                favouriteBtn.setChecked(!favouriteBtn.isChecked());
                                if (response.errCode != ErrorCode.NO_ERROR) {
                                    favouriteBtn.setChecked(!favouriteBtn.isChecked());
                                    return;
                                }
                                if (favouriteBtn.isChecked()) {
                                    mPresenter.cancelFollowTopic(mTopic);
                                } else {
                                    mPresenter.followTopic(mTopic);
                                }
                                isClick = true;

//                                    favouriteBtn.setClickable(true);
                            }
                        });

            }
        });
        postBtn.setOnClickListener(new com.umeng.comm.core.listener.Listeners.LoginOnViewClickListener() {
            @Override
            protected void doAfterLogin(View v) {
                gotoPostFeedActivity();
            }
        });
        favouriteBtn.setTextOff("");
        favouriteBtn.setTextOn("");
        mIndicator.SetIndictorClick(new IndicatorListerner());
    }
    protected class IndicatorListerner implements TopicIndicator.IndicatorListener {
        @Override
        public void SetItemClick() {
            int cCount = mIndicator.getChildCount();
            for (int i = 0; i < cCount; i++) {
                final int j = i;
                View view = mIndicator.getChildAt(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            mViewPager.setCurrentItem(j);

                    }
                });
            }
        }
    }
    protected abstract void gotoPostFeedActivity();
    /**
     * 检查当前登录用户是否已关注该话题，并设置ToggleButton的状态</br>
     */
    protected void setTopicStatus() {
        String loginUserId = CommConfig.getConfig().loginedUser.id;
        if (TextUtils.isEmpty(loginUserId)) {
            Log.d("###", "### user dont login...");
            return;
        }
        com.umeng.comm.core.utils.Log.d("topic","is focused"+mTopic.isFocused);
        favouriteBtn.setChecked(mTopic.isFocused);
    }
    public BaseFragment getFragmentByname(TopicItem item){
        String name = item.style;
        if (name.equals("topicnew")){
            TopicFeedFragment fragment = TopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if (name.equals("topiclastreply")){
            LastestTopicFeedFragment fragment = LastestTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if(name.equals("topicrecommend")){
            RecommendTopicFeedFragment fragment = RecommendTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if(name.equals("topichot")){
            HotTopicFeedFragment fragment = HotTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setHotList(item.hotIems);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if(name.equals("active")){
            ActiveUserFragment fragment = ActiveUserFragment.newActiveUserFragment(mTopic);
            fragment.setNavigation(command);
            return fragment;
        }else {
            TopicFeedFragment fragment = TopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }
    }
}
