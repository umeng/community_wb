package com.umeng.common.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.MessageCount;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.configure.MainItem;
import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.mvpview.MvpUnReadMsgView;
import com.umeng.common.ui.presenter.impl.NullPresenter;
import com.umeng.common.ui.widgets.MainIndicator;

import java.util.ArrayList;

/**
 * Created by wangfei on 16/7/28.
 */
public abstract class CommunityMainBaseFragment extends BaseFragment<Void, NullPresenter> implements
        View.OnClickListener, MvpUnReadMsgView {

    protected ViewPager mViewPager;
    protected String[] mTitles;
    protected Fragment mCurrentFragment;
    protected CommFragmentPageAdapter adapter;
    /**
     * 回退按钮的可见性
     */
    protected int mBackButtonVisible = View.VISIBLE;
    /**
     * 跳转到话题搜索按钮的可见性
     */
    protected int mTitleVisible = View.VISIBLE;
    /**
     * title的根布局
     */
    protected View mTitleLayout;
    /**
     * 右上角的个人信息Button
     */
    protected ImageView mProfileBtn;

    protected String mContainerClass;
    /**
     * tab视图
     */
    private MainIndicator indicator;
    /**
     * 未读消息的数量
     */
    private MessageCount mUnreadMsg = CommConfig.getConfig().mMessageCount;
    /**
     * 含有未读消息时的红点视图
     */
    private View mBadgeView;


    private IndicatorListerner mIndicatorListerner;

    private ArrayList<FeedListBaseFragment> mFeedListBaseFragments = new ArrayList<FeedListBaseFragment>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    @Override
    public void onFetchUnReadMsg(MessageCount unreadMsg) {
        this.mUnreadMsg = unreadMsg;
        if (mUnreadMsg.unReadTotal > 0) {
            mBadgeView.setVisibility(View.VISIBLE);
        }
    }
    protected void initViewPager(View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(ResFinder.getId("viewPager"));
        mViewPager.setOffscreenPageLimit(mTitles.length);
        adapter = new CommFragmentPageAdapter(getChildFragmentManager());

        mViewPager.setAdapter(adapter);

        indicator.setOnPageChangeListener(new MainIndicator.PageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                mCurrentFragment = getFragment(page);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        indicator.setViewPager(mViewPager, 0);
    }
    public ViewPager getViewPager() {
        return mViewPager;
    }

    protected class IndicatorListerner implements MainIndicator.IndicatorListener {
        @Override
        public void SetItemClick() {
            int cCount = indicator.getChildCount();
            for (int i = 0; i < cCount; i++) {
                final int j = i;
                View view = indicator.getChildAt(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(j);
                    }
                });
            }
        }
    }

    protected void initTitle(View rootView) {
        mIndicatorListerner = new IndicatorListerner();
        if (parseJson.mainItems.size() == 0) {
            mTitles = getResources().getStringArray(
                    ResFinder.getResourceId(ResFinder.ResType.ARRAY, "umeng_comm_feed_titles"));
        } else {
            mTitles = new String[parseJson.mainItems.size()];
            for (int i = 0 ;i<mTitles.length;i++){
                mTitles[i] = parseJson.mainItems.get(i).title;
            }

        }
        int titleLayoutResId = ResFinder.getId("topic_action_bar");
        mTitleLayout = rootView.findViewById(titleLayoutResId);
        mTitleLayout.setVisibility(View.GONE);

        int backButtonResId = ResFinder.getId("umeng_comm_back_btn");
        rootView.findViewById(backButtonResId).setOnClickListener(this);

        if (mBackButtonVisible != View.VISIBLE) {
            rootView.findViewById(backButtonResId).setVisibility(mBackButtonVisible);
        }

        mTitleLayout.setVisibility(mTitleVisible);

        mBadgeView = findViewById(ResFinder.getId("umeng_comm_badge_view"));
        mBadgeView.setVisibility(View.INVISIBLE);
        //
        mProfileBtn = (ImageView) rootView
                .findViewById(ResFinder.getId("umeng_comm_user_info_btn"));
        mProfileBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mBadgeView != null) {
                            mBadgeView.setVisibility(View.INVISIBLE);
                        }
                        gotoFindActivity(CommConfig.getConfig().loginedUser);
                    }
                }

        );
        if (!parseJson.isfind){
            mProfileBtn.setVisibility(View.GONE);
        }
        indicator = (MainIndicator) rootView.findViewById(ResFinder
                .getId("umeng_comm_segment_view"));
        // 设置tabs
//        String[] titles = new String[]{"热门", "推荐", "关注", "话题"};
        indicator.setVisibleTabCount(mTitles.length);
        indicator.setTabItemTitles(mTitles);

        indicator.SetIndictorClick(mIndicatorListerner);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mUnreadMsg.unReadTotal > 0 && CommonUtils.isLogin(getActivity())) {
            mBadgeView.setVisibility(View.VISIBLE);
        } else {
            mBadgeView.setVisibility(View.INVISIBLE);
        }
    }
    public abstract  void gotoFindActivity(CommUser user);
    /**
     * 设置回退按钮的可见性
     *
     * @param visible
     */
    public void setBackButtonVisibility(int visible) {
        if (visible == View.VISIBLE || visible == View.INVISIBLE || visible == View.GONE) {
            this.mBackButtonVisible = visible;
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == ResFinder.getId("umeng_comm_back_btn")) {
            getActivity().finish();
        }
    }
    class CommFragmentPageAdapter extends FragmentPagerAdapter {

        public CommFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            return getFragment(pos);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

    }
    /**
     * 初始化Fragment</br>
     */
    protected void initFragment() {

        if (parseJson.mainItems.size() != 0) {
            for (MainItem item : parseJson.mainItems) {
                if (!item.style.equals("topic")&&!item.style.equals("recommenduser")&&!item.style.equals("find")) {
                    mFeedListBaseFragments.add((FeedListBaseFragment) converToFragment(item));
                }
                fragments.add(converToFragment(item));
            }
        } else {
            HotFeedsFragment hotFeedsFragment = new HotFeedsFragment();
            hotFeedsFragment.setNavigation(command);
            hotFeedsFragment.isShowSearchBar(false);
            hotFeedsFragment.isShowPostButton(true);
            mFeedListBaseFragments.add(hotFeedsFragment);
            fragments.add(hotFeedsFragment);
            RecommendFeedFragment recommendFeedFragment = new RecommendFeedFragment();
            recommendFeedFragment.setNavigation(command);
            recommendFeedFragment.isShowSearchBar(true);
            recommendFeedFragment.isShowPostButton(true);
            mFeedListBaseFragments.add(recommendFeedFragment);
            fragments.add(recommendFeedFragment);
            AllFeedsFragment allFeedsFragment = new AllFeedsFragment();
            allFeedsFragment.setNavigation(command);
            allFeedsFragment.isShowSearchBar(true);
            allFeedsFragment.isShowPostButton(true);
            mFeedListBaseFragments.add(allFeedsFragment);
            fragments.add(allFeedsFragment);
            fragments.add(getTopicMainFragment());
            mCurrentFragment = mFeedListBaseFragments.get(0);
        }

    }
    public abstract TopicMainBaseFragment getTopicMainFragment();
    /*hot = 最热feed流 realtime=实时feed流 recommend = 推荐feed流 allfocus = 关注feed流*/
    public Fragment converToFragment(MainItem item) {
        String name = item.style;
        if (name.equals("realtime")) {
            RealTimeFeedFragment mRealTimeFeedFragment = new RealTimeFeedFragment();
            mRealTimeFeedFragment.setNavigation(command);
            mRealTimeFeedFragment.setShowActionbar(false);
            mRealTimeFeedFragment.isShowSearchBar(item.isSearch);
            mRealTimeFeedFragment.isShowPostButton(item.isPostBtn);
            mRealTimeFeedFragment.setShowtopFeed(item.isShowTop);
            return  mRealTimeFeedFragment;
        } else if (name.equals("recommend")) {
            RecommendFeedFragment recommendFeedFragment = new RecommendFeedFragment();
            recommendFeedFragment.setNavigation(command);
            recommendFeedFragment.isShowSearchBar(item.isSearch);
            recommendFeedFragment.isShowPostButton(item.isPostBtn);
            recommendFeedFragment.setShowtopFeed(item.isShowTop);
            return recommendFeedFragment;
        } else if (name.equals("hot")) {
            HotFeedsFragment hotFeedsFragment = new HotFeedsFragment();
            hotFeedsFragment.setNavigation(command);
            hotFeedsFragment.isShowSearchBar(item.isSearch);
            hotFeedsFragment.isShowPostButton(item.isPostBtn);
            hotFeedsFragment.setHotList(item.hotIems);
            hotFeedsFragment.setShowtopFeed(item.isShowTop);
            return hotFeedsFragment;
        } else if (name.equals("allfocus")) {
            AllFeedsFragment allFeedsFragment = new AllFeedsFragment();
            allFeedsFragment.setNavigation(command);
            allFeedsFragment.isShowSearchBar(item.isSearch);
            allFeedsFragment.isShowPostButton(item.isPostBtn);
            allFeedsFragment.setShowtopFeed(item.isShowTop);
            return allFeedsFragment;
        }else if (name.equals("topic")) {
//            TopicMainFragment topicMainFragment = new TopicMainFragment();
//            return topicMainFragment;
            return getTopicMainFragment();
        } else if (name.equals("friend")) {
            FriendsFragment friendsFragment = new FriendsFragment();
            friendsFragment.setNavigation(command);
            friendsFragment.isShowSearchBar(item.isSearch);
            friendsFragment.isShowPostButton(item.isPostBtn);
            friendsFragment.setShowtopFeed(item.isShowTop);
            return friendsFragment;
        }else if (name.equals("nearby")) {
            NearbyFeedFragment nearbyFeedFragment = new NearbyFeedFragment();
            nearbyFeedFragment.setNavigation(command);
            nearbyFeedFragment.isShowSearchBar(item.isSearch);
            nearbyFeedFragment.isShowPostButton(item.isPostBtn);
            nearbyFeedFragment.setShowtopFeed(item.isShowTop);
            return nearbyFeedFragment;
        }else if (name.equals("favourite")) {
            FavoritesFragment favoritesFragment = new FavoritesFragment();
            favoritesFragment.setNavigation(command);
            favoritesFragment.isShowSearchBar(item.isSearch);
            favoritesFragment.isShowPostButton(item.isPostBtn);
            favoritesFragment.setShowtopFeed(item.isShowTop);
            return favoritesFragment;
        }else if (name.equals("topicnew")) {
            Topic mTopic = new Topic();
            mTopic.id = item.topicid;
            mTopic.name = Constants.TOPIC_GAT+item.title+Constants.TOPIC_GAT;
            TopicFeedFragment fragment = TopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowSearchBar(item.isSearch);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if (name.equals("topicrecommend")) {
            Topic mTopic = new Topic();
            mTopic.id = item.topicid;
            mTopic.name = Constants.TOPIC_GAT+item.title+Constants.TOPIC_GAT;
            RecommendTopicFeedFragment fragment = RecommendTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowSearchBar(item.isSearch);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if (name.equals("topiclastreply")) {
            Topic mTopic = new Topic();
            mTopic.id = item.topicid;
            mTopic.name = Constants.TOPIC_GAT+item.title+Constants.TOPIC_GAT;
            LastestTopicFeedFragment fragment = LastestTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowSearchBar(item.isSearch);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if (name.equals("topichot")) {
            Topic mTopic = new Topic();
            mTopic.id = item.topicid;
            mTopic.name = Constants.TOPIC_GAT+item.title+Constants.TOPIC_GAT;
            HotTopicFeedFragment fragment = HotTopicFeedFragment.newTopicFeedFrmg(mTopic);
            fragment.setNavigation(command);
            fragment.isShowPostButton(item.isPostBtn);
            fragment.isShowSearchBar(item.isSearch);
            fragment.setHotList(item.hotIems);
            fragment.setShowtopFeed(item.isShowTop);
            return fragment;
        }else if (name.equals("recommenduser")) {
            RecommendUserFragment mRecommendUserFragment = new RecommendUserFragment();
            mRecommendUserFragment.setSaveButtonInvisiable();
            mRecommendUserFragment.setShowActionbar(false);
            mRecommendUserFragment.setNavigation(command);
            return mRecommendUserFragment;
        }
        else if (name.equals("find")) {
            FindBaseFragment fragment = new FindBaseFragment();

            fragment.setNavigation(command);
            return fragment;
        }
        else {
            AllFeedsFragment allFeedsFragment = new AllFeedsFragment();
            allFeedsFragment.setNavigation(command);
            return allFeedsFragment;
        }
    }
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mInitConfigReceiver);
        super.onDestroy();
    }
    protected BroadcastReceiver mInitConfigReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onFetchUnReadMsg(CommConfig.getConfig().mMessageCount);
        }
    };
    /**
     * 注册登录成功时的广播</br>
     */
    protected void registerInitSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_INIT_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mInitConfigReceiver,
                filter);
    }
    /**
     * 主动调用加载数据。 【注意】该接口仅仅在退出登录时，跳转到FeedsActivity清理数据后重新刷新数据</br>
     */
    public void repeatLoadDataFromServer() {

        for (FeedListBaseFragment temp : mFeedListBaseFragments) {
            temp.loadDataFromServer();
        }
    }

    /**
     * clean sub fragment data</br>
     */
    public void cleanAdapterData() {

        for (FeedListBaseFragment temp : mFeedListBaseFragments) {
            temp.clearListView();
        }
    }
    /**
     * 隐藏MianFeedFragment的输入法，当退出fragment or activity的时候</br>
     */
    public void hideCommentLayoutAndInputMethod() {

    }
    protected Fragment getFragment(int pos) {
        Fragment fragment = null;

        if (pos < fragments.size()) {
            fragment = fragments.get(pos);
        }

        return fragment;
    }
    /**
     * 获取当前页面被选中的Fragment</br>
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
