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

package com.umeng.common.ui.presenter.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.mvpview.MvpFeedView;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.util.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Feed列表相关的Presenter，该Presenter从网络、数据库中读取Feed，如果数据是从网络上获取的，那么需要将数据存储到数据库中。
 * 在获取数据后会通过MvpFeedView的{@link MvpFeedView#getBindDataSource()}
 * 函数获取到列表的数据集,然后对新获取的数据进行去重、排序，再将新获取到的数据添加到列表的数据集合中，最后调用
 * {@link MvpFeedView#notifyDataSetChanged()} 函数更新对应的列表视图。
 *
 * @author mrsimple
 */
public class BaseFeedListPresenter extends BaseFeedPresenter implements Filter<FeedItem> {

    protected MvpFeedView mFeedView;
    protected String mNextPageUrl;


    private boolean isRegisterReceiver = true;
    private boolean isAttached = false;

    // feed更新时回调，用于提示
    protected OnResultListener mOnResultListener;

    /**
     * 是否显示置顶feed，默认为不显示
     */
    private boolean isShowTopFeeds = false;
    /**
     * 保存当前置顶feed
     */
    private List<FeedItem> mTopFeeds = new ArrayList();

    /**
     * 是否需要清空数据库缓存的标志位
     */
    protected AtomicBoolean isNeedRemoveOldFeeds = new AtomicBoolean(true);

    /**
     * 是否为访客模式
     */
    private boolean isGuestMode = true;

    /**
     * 是否在在加载中
     */
    private boolean isLoading = false;

    public BaseFeedListPresenter(MvpFeedView view) {
        this.mFeedView = view;
    }

    public BaseFeedListPresenter(MvpFeedView view, boolean isRegisterReceiver) {
        this.mFeedView = view;
        this.isRegisterReceiver = isRegisterReceiver;
    }

    /**
     * 设置回调</br>
     *
     * @param listener
     */
    public void setOnResultListener(OnResultListener listener) {
        this.mOnResultListener = listener;
    }

    public void setIsNeedRemoveOldFeeds() {
        mNextPageUrl = "";
        isNeedRemoveOldFeeds.set(true);
    }

    /**
     * 改方法为对外统一接口，包含置顶逻辑，禁止子类重写
     */
    @Override
    public final void loadDataFromServer() {
//        mCommunitySDK.fetchLastestFeeds(mRefreshListener);
        // 设置加载状态
        if (isLoading()) {
            return;
        } else {
            setLoadingState(true);
        }

        if (isShowTopFeeds()) {
            // 由全局置顶和话题下置顶
            loadTopFeeds(new SimpleFetchListener<FeedsResponse>() {

                @Override
                public void onStart() {
//                    mFeedView.onRefreshStart();
                    if (mTopFeeds != null) {
                        mTopFeeds.clear();
                    }
                }

                @Override
                public void onComplete(FeedsResponse response) {
                    if (response.errCode == ErrorCode.NO_ERROR) {
                        mTopFeeds = response.result;
                        for (int i = 0; i < mTopFeeds.size(); i++) {
                            mTopFeeds.get(i).isTop = 1;
                        }
                    }
                    loadDataOnRefresh();
                }
            });
        } else {
            loadDataOnRefresh();
        }
    }

    /**
     * 加载置顶feed，默认为全局置顶
     *
     * @param listener
     */
    protected void loadTopFeeds(FetchListener<FeedsResponse> listener) {
        mCommunitySDK.fetchTopFeeds(listener);
    }

    /**
     * 加载对应列表feed
     */
    protected void loadDataOnRefresh() {
    }

    /**
     * 下拉数据的Listener
     */
    protected FetchListener<FeedsResponse> mRefreshListener = new SimpleFetchListener<FeedsResponse>() {

        @Override
        public void onStart() {
            mFeedView.onRefreshStart();
        }

        // [注意]：mFeedView.onRefreshEnd方法不可提前统一调用，该方法会被判断是否显示空视图的逻辑
        @Override
        public void onComplete(FeedsResponse response) {
            // 更新加载状态
            setLoadingState(false);

            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response) && !isHasTopFeed()) {
                mFeedView.onRefreshEnd();
                return;
            }

            if (Constants.IS_CLEAR_DATA_AFTER_REFRESH) {
                mNextPageUrl = response.nextPageUrl;
            } else {
                // 对于下拉刷新，仅在下一个地址为空（首次刷新）时设置下一页地址
                if (TextUtils.isEmpty(mNextPageUrl) && isNeedRemoveOldFeeds.get()) {
                    mNextPageUrl = response.nextPageUrl;
                }
            }

            beforeDeliveryFeeds(response);

            // 处理置顶逻辑
            final List<FeedItem> newFeedItems = response.resultWithoutTop;
            if (isShowTopFeeds()) {
                addTopFeedToHeader(newFeedItems);
            }

            // 拼接数据到list中
            int news = appendFeedItemsToHeader(newFeedItems);
            if (mOnResultListener != null) {
                mOnResultListener.onResult(news);
            }

            // 保存加载的数据。如果该数据存在于DB中，则替换成最新的，否则Insert一条新纪录
            saveDataToDB(newFeedItems);

            // 处理访客模式的逻辑
            boolean guestMode = response.isVisit;
            dealGuestMode(guestMode);

            // 回调页面加载完成
            mFeedView.onRefreshEnd();
        }
    };

    /**
     * modify by pei 2016.3.8
     *
     * @param newFeeds
     */
    protected void addTopFeedToHeader(List<FeedItem> newFeeds) {
        if (mTopFeeds != null && !mTopFeeds.isEmpty()) {
            newFeeds.removeAll(mTopFeeds);
            newFeeds.addAll(0, mTopFeeds);
            mTopFeeds.clear();
        }
    }

    @Override
    protected void saveDataToDB(List<FeedItem> newFeedItems) {
        super.saveDataToDB(newFeedItems);
    }

    protected void beforeDeliveryFeeds(FeedsResponse response) {
        // 第一次从网络上下拉到数据,那么则清除从数据库中加载进来的数据,避免下一页地址出问题.
        clearFeedDBCache();
//        List<NormalFeed> list = new ArrayList<>();
//        for (FeedItem item:response.result){
//            NormalFeed feed = new NormalFeed();
//            feed.id = item.id;
//            feed.feedId = item.id;
//            list.add(feed);
//        }
//        mDatabaseAPI.getFeedDBAPI().saveNormalFeedToDB(list);
    }

    /**
     * 第一次从网上下拉到数据时清空数据库中的缓存,然后会调用{@see saveFeedsToDB}将新的数据存储到缓存中
     */
    private void clearFeedDBCache() {
        if (isNeedRemoveOldFeeds.get()) {
            // 清空原来的缓存数据
            // mFeedView.clearListView(); //
            // 此时暂时不清空Adapter数据，确保手机显示的数据是集为：cache+server端数据
            // 清空数据库中的缓存数据
//            mDatabaseAPI.getFeedDBAPI().clearFollowingFeed();
            if (!TextUtils.isEmpty(CommConfig.getConfig().loginedUser.id)) {
                isNeedRemoveOldFeeds.set(false);
            }
        }
    }

    public void fetchNextPageData() {
        if (!isCanLoadMore()) {
            mFeedView.onRefreshEnd();
            return;
        }

        if (TextUtils.isEmpty(mNextPageUrl)) {
            mFeedView.onRefreshEnd();
            return;
        }

        // 设置加载状态，必须在真正的请求之前设置，否则会出现bug
        if (isLoading()) {
            return;
        } else {
            setLoadingState(true);
        }

        mCommunitySDK.fetchNextPageData(mNextPageUrl,
                FeedsResponse.class, new SimpleFetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        // 更新加载状态
                        setLoadingState(false);

                        mFeedView.onRefreshEnd();
                        // 根据response进行Toast
                        if (NetworkUtils.handleResponseAll(response)) {
                            // 如果返回的数据是空，则需要置下一页地址为空
                            if (response.errCode == ErrorCode.NO_ERROR) {
                                mNextPageUrl = "";
                            }
                            return;
                        }
                        mNextPageUrl = response.nextPageUrl;
                        // 去掉重复的feed
                        final List<FeedItem> feedItems = response.resultWithoutTop;
                        if (feedItems != null && feedItems.size() > 0) {
                            // 追加数据
                            appendFeedItems(feedItems, false);
//                            saveDataToDB(feedItems);
                        }
                    }
                });
    }

    /**
     * 去重并更新adapter的数据,将数据插到前边。</br>
     *
     * @param feedItems
     */
    protected int appendFeedItemsToHeader(List<FeedItem> feedItems) {
        feedItems = doFilte(feedItems);
        List<FeedItem> oldFeeds = mFeedView.getBindDataSource();
        int size = oldFeeds.size();
        int news = oldFeeds.size() - size;
        mergeFeedItems(feedItems, true);
        sortFeedItems(mFeedView.getBindDataSource());
        mFeedView.notifyDataSetChanged();
        return news;
    }

    /**
     * 去重并更新adapter的数据,追加到后面。</br>
     *
     * @param feedItems
     */
    protected List<FeedItem> appendFeedItems(List<FeedItem> feedItems, boolean fromDB) {
        if (fromDB && !mFeedView.getBindDataSource().isEmpty()) {
            return feedItems;
        }
        // TODO 置顶存在的问题 关注列表中存在置顶对象
        doFilte(feedItems);
        if (feedItems != null && !feedItems.isEmpty()) {
            mergeFeedItems(feedItems, false);
            sortFeedItems(mFeedView.getBindDataSource());
            mFeedView.notifyDataSetChanged();
            mFeedView.onRefreshEnd();
        }
        return feedItems;
    }

    /**
     * 合并数据</br>
     *
     * @param newFeeds
     * @param isRefresh
     */
    private void mergeFeedItems(List<FeedItem> newFeeds, boolean isRefresh) {
        List<FeedItem> topFeeds = new ArrayList();
        for (FeedItem feed : mFeedView.getBindDataSource()) {
            if (feed.isTop == 1) {
                topFeeds.add(feed);
            }
        }

        if (isRefresh) {
            if (Constants.IS_CLEAR_DATA_AFTER_REFRESH) {
                mFeedView.getBindDataSource().clear();
            }

            // 刷新操作 更新置顶数据
            mFeedView.getBindDataSource().removeAll(topFeeds);
            topFeeds.clear();

            mFeedView.getBindDataSource().removeAll(newFeeds);
            mFeedView.getBindDataSource().addAll(0, newFeeds);
        } else {
            newFeeds.removeAll(topFeeds);

            mFeedView.getBindDataSource().removeAll(newFeeds);
            mFeedView.getBindDataSource().addAll(newFeeds);
        }

        for (FeedItem temp : newFeeds) {
            if (temp.isTop == 1) {
                topFeeds.remove(temp);
                topFeeds.add(temp);
            }
        }
        if (topFeeds != null && !topFeeds.isEmpty()) {
            mFeedView.getBindDataSource().removeAll(topFeeds);
            mFeedView.getBindDataSource().addAll(0, topFeeds);
        }
    }

    /**
     * 过滤feed</br>
     *
     * @param newItems 要过滤的新数据
     * @return
     */
    @Override
    public List<FeedItem> doFilte(List<FeedItem> newItems) {
        // 移除status>=2的feed，具体值得的含义参考文档说明
        Iterator<FeedItem> iterator = newItems.iterator();
        while (iterator.hasNext()) {
            FeedItem item = iterator.next();
            if (item.status >= FeedItem.STATUS_SPAM && item.status != FeedItem.STATUS_LOCK) {
                iterator.remove();
            }
        }
        return newItems;
    }

    /**
     * 排序feed</br>
     *
     * @param items
     */
    public void sortFeedItems(List<FeedItem> items) {
        // 所有feed都按照feed的时间降序排列。【该代码避免用户首次登录时，推荐的feed时间较新，但是管理员的帖子较旧的情况】
        Comparator<FeedItem> comparator = getFeedCompartator();
        if (comparator != null) {
            Collections.sort(items, comparator);
        }
    }

    /**
     * 获取所有的feed</br>
     */
    @Override
    public void loadDataFromDB() {
        mDatabaseAPI.getFeedDBAPI().loadFeedsFromDB(mDbFetchListener);
    }

    /**
     * 获取某个用户的feed</br>
     *
     * @param uid
     */
    // 注意这里不能重构
    public void loadFeedsFromDB(String uid) {
        mDatabaseAPI.getFeedDBAPI().loadFeedsFromDB(uid, mDbFetchListener);
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
        if (isRegisterReceiver && !isAttached) {
            registerBroadcast();
        }
        isAttached = true;
        // 启动页面是判断是否登录，关注列表页
        if (!CommonUtils.isLogin(mContext)) {
            mFeedView.showLoginView();
        }
    }

    @Override
    public void detach() {
        if (isRegisterReceiver && isAttached) {
            unRegisterBroadcast();
        }
        isAttached = false;
        super.detach();
    }

    protected void registerBroadcast() {
        registerLoginSuccessBroadcast();
        // registerLogout Receiver
        BroadcastUtils.registerUserBroadcast(mContext, mReceiver);
    }

    protected void unRegisterBroadcast() {

        mContext.unregisterReceiver(mLoginReceiver);
        BroadcastUtils.unRegisterBroadcast(mContext, mReceiver);

    }

    /**
     * 注册登录成功时的广播</br>
     */
    private void registerLoginSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_LOGIN_SUCCESS);
        mContext.registerReceiver(mLoginReceiver, filter);
    }

    /**
     * 监听用户登录</br>
     */
    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            afterUserLogin();
        }
    };

    /**
     * 监听用户退出登录</br>
     */
    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_LOGOUT) {
                afterUserLogout();
            }
        }
    };

    /**
     * 用于数据库的SimpleFetchListener</br>
     */
    protected SimpleFetchListener<List<FeedItem>> mDbFetchListener =
            new SimpleFetchListener<List<FeedItem>>() {

                @Override
                public void onComplete(List<FeedItem> response) {
                    appendFeedItems(response, true);
                    mFeedView.onRefreshEnd();
                }
            };

    /**
     * 从server加载数据。【注意：该接口仅仅在登录成功的时候调用，需要对此种情况有特殊的逻辑处理】</br>
     */
    private void fetchDataFromServerByLogin() {
        if(!isRefreshDataAfterLogin()){
            return;
        }

        if (isShowTopFeeds()) {
            // 由全局置顶和话题下置顶
            loadTopFeeds(new SimpleFetchListener<FeedsResponse>() {

                @Override
                public void onStart() {
                    mFeedView.onRefreshStart();
                    if (mTopFeeds != null) {
                        mTopFeeds.clear();
                    }
                }

                @Override
                public void onComplete(FeedsResponse response) {
                    if (response.errCode == ErrorCode.NO_ERROR) {
                        mTopFeeds = response.result;
                        for (int i = 0; i < mTopFeeds.size(); i++) {
                            mTopFeeds.get(i).isTop = 1;
                        }
                    }
                    loadDataAfterLogin(mLoginRefreshListener);
                }
            });
        } else {
            loadDataAfterLogin(mLoginRefreshListener);
        }
    }

    /**
     * 用户登录刷新数据回调
     * @param mLoginRefreshListener
     */
    protected void loadDataAfterLogin(FetchListener<FeedsResponse> mLoginRefreshListener) {

    }

    /**
     * 处理登录刷新的listener</br>
     */
    private FetchListener<FeedsResponse> mLoginRefreshListener = new FetchListener<FeedsResponse>() {

        @Override
        public void onStart() {
        }

        @Override
        public void onComplete(FeedsResponse response) {
            // 首先清理未登录时存储的数据 2016.7.19 注释掉 1，缓存大小整体清理；2，登录后会删除；3，即使不删除，也不影响缓存展示
//            mDatabaseAPI.getFeedDBAPI().deleteAllFeedsFromDB();
            // 清理Adapter中的数据
            mFeedView.getBindDataSource().clear();
            if (NetworkUtils.handleResponseAll(response) && !isHasTopFeed()) {
                mFeedView.onRefreshEnd();
                return;
            }
            // 更新下一页地址
            mNextPageUrl = response.nextPageUrl;
            if (!TextUtils.isEmpty(mNextPageUrl)) {
                isNeedRemoveOldFeeds.set(false);
            }
            beforeDeliveryFeeds(response);
            // 处理置顶逻辑
            final List<FeedItem> newFeedItems = response.resultWithoutTop;
            if (isShowTopFeeds()) {
                addTopFeedToHeader(newFeedItems);
            }
            // 拼接数据到list中
            int news = appendFeedItemsToHeader(newFeedItems);
            if (mOnResultListener != null) {
                mOnResultListener.onResult(news);
            }
            // 保存加载的数据。如果该数据存在于DB中，则替换成最新的，否则Insert一条新纪录
            saveDataToDB(newFeedItems);
            mFeedView.onRefreshEnd();
        }
    };

    /**
     * 子类可以修改排序规则</br>
     *
     * @return
     */
    protected Comparator<FeedItem> getFeedCompartator() {
        return mComparator;
    }

    /**
     * 排序规则</br>
     */
    private Comparator<FeedItem> mComparator = new Comparator<FeedItem>() {

        @Override
        public int compare(FeedItem lhs, FeedItem rhs) {
            int isTop = rhs.isTop - lhs.isTop;
            return isTop != 0 ? isTop : rhs.publishTime.compareTo(lhs.publishTime);
        }
    };

    /**
     * 处理用户登录后的逻辑<br>
     */
    protected void afterUserLogin() {
        // 刷新列表数据
        fetchDataFromServerByLogin();
        // 用户登录之后，不受访客/非访客模式限制
        dealGuestMode(true);
        // 隐藏登录view
        mFeedView.hideLoginView();
    }

    /**
     * 处理用户退出登录后的逻辑<br>
     */
    protected void afterUserLogout() {
        // 显示登录view
        mFeedView.showLoginView();
    }

    /**
     * 处理访客/非访客模式子类，刷新之后调用此方法，将访客/非访客模式通知到父类</br>
     *
     * @param guestMode
     */
    protected final void dealGuestMode(boolean guestMode) {
        this.isGuestMode = guestMode;
        if (isCanLoadMore()) { // 是否显示提示（登录后查看更多）与是否加载更多的状态一致
            mFeedView.hideVisitView();
        } else {
            mFeedView.showVisitView();
        }
    }

    /**
     * 是否能够加载更多，访客/非访客模式</br>
     *
     * @return
     */
    protected final boolean isCanLoadMore() {
        // 访客模式或者已登录
        boolean isLogin = (mContext != null) && CommonUtils.isLogin(mContext);
        return isGuestMode || isLogin;
    }

    /**
     * 处理发送feed的逻辑</br>
     */
    public void postFeed() {
        if (!CommonUtils.isLogin(mContext)) {
            CommunitySDKImpl.getInstance().login(mContext, new LoginListener() {
                @Override
                public void onStart() {
                    mFeedView.showProgressBar();
                }

                @Override
                public void onComplete(int stCode, CommUser userInfo) {
                    mFeedView.hideProgressBar();
                    if (stCode == 0) {
                        mFeedView.gotoPostFeedActivity();
                    }
                }
            });
        } else {
            mFeedView.gotoPostFeedActivity();
        }
    }

    /**
     * 是否正在加载中
     *
     * @return
     */
    protected boolean isLoading() {
        return isLoading;
    }

    /**
     * 设置Loading的状态
     *
     * @param loading
     */
    public void setLoadingState(boolean loading) {
        isLoading = loading;
    }

    /**
     * 该方法与fetchNextPageData()作用相同
     */
    @Override
    public void loadMoreData() {
        super.loadMoreData();
        fetchNextPageData();
    }

    /**
     * 是否存在置顶feed
     *
     * @return
     */
    protected boolean isHasTopFeed() {
        return mTopFeeds != null && !mTopFeeds.isEmpty();
    }

    /**
     * 是否显示置顶feed
     *
     * @return
     */
    protected boolean isShowTopFeeds() {
        return isShowTopFeeds;
    }

    /**
     * 设置是否显示置顶feed
     *
     * @param isShowTopFeeds
     */
    public void setIsShowTopFeeds(boolean isShowTopFeeds) {
        this.isShowTopFeeds = isShowTopFeeds;
    }

    /**
     * 用户登录后是否刷新数据
     * @return
     */
    public boolean isRefreshDataAfterLogin(){
        return false;
    }
}
