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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.CommentResponse;
import com.umeng.comm.core.nets.responses.LikesResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.receiver.BaseBroadcastReceiver;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.mvpview.MvpFeedDetailView;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * Feed详情页的Presenter
 *
 * @author mrsimple
 */
public class FeedDetailPresenter extends BaseFeedPresenter {

    private MvpFeedDetailView mDetailView;
    private FeedItem mFeedItem;

    private String mNextPageUrlcomment;
    private String mNextPageUrllike;

    // 用于处理点赞，取消点赞
    private LikePresenter mLikePresenter;

    private FEED_DETAIL_PRESENTER_STYLE mStyle = FEED_DETAIL_PRESENTER_STYLE.WEIBO;

    private COMMENT_MODE mCurrentCommentMode = COMMENT_MODE.ALL;

    private LoginReceiver mLoginReceiver;

    public FeedDetailPresenter(MvpFeedDetailView feedDetailView, FeedItem feedItem) {
        this.mDetailView = feedDetailView;
        this.mFeedItem = feedItem;
        mLikePresenter = new LikePresenter(mDetailView);

        mLoginReceiver = new LoginReceiver();
    }

    public void setPresenterStyle(FEED_DETAIL_PRESENTER_STYLE style) {
        this.mStyle = style;
    }


    @Override
    public void attach(Context context) {
        super.attach(context);
        registerReceiver();
        mLikePresenter.attach(context);
    }

    @Override
    public void detach() {
        unregisterReceiver();
        mLikePresenter.detach();
        super.detach();
    }

    public void setFeedItem(FeedItem feedItem) {
        this.mFeedItem = feedItem;
        mLikePresenter.setFeedItem(feedItem);
    }

    /**
     * 获取该feed的点赞</br>
     */
    private void loadLikesFromServer() {
        mCommunitySDK.fetchFeedLikes(mFeedItem.id, new SimpleFetchListener<LikesResponse>() {
            @Override
            public void onComplete(LikesResponse response) {
                if (NetworkUtils.handleResponseComm(response)) {
                    return;
                }
                if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
//                    ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                    return;
                }
                if (response.errCode != ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                    return;
                }
                List<Like> likes = response.result;
                likes.removeAll(mFeedItem.likes);
                mFeedItem.likes.addAll(likes);
                mNextPageUrllike = response.nextPageUrl;
                mDetailView.updateLikeView(response.nextPageUrl);
                mDetailView.onRefreshEnd();
                saveLikesToDB(likes);
            }
        });
    }

    /**
     * 保存Like数据到数据库</br>
     *
     * @param likes
     */
    private void saveLikesToDB(List<Like> likes) {
        if (CommonUtils.isListEmpty(likes)) {
            return;
        }
        mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem);// 由于like数据变化，此时需要级联更新对应的feed数据
        mDatabaseAPI.getLikeDBAPI().saveLikesToDB(mFeedItem);
    }

    /**
     * 只看楼主评论列表</br>
     */
    public void loadCommentsByuserFromServer() {
        mCurrentCommentMode = COMMENT_MODE.FEED_CREATOR;
        mCommunitySDK.fetchFeedCommentsByuser(mFeedItem.id, mFeedItem.creator.id, getCommentOrder(),
                new SimpleFetchListener<CommentResponse>() {
                    @Override
                    public void onComplete(CommentResponse response) {
                        mDetailView.onRefreshEnd();
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.showOwnerComment(false);
                            return;
                        }

                        if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                            mDetailView.showOwnerComment(false);
                            return;
                        }
                        if (response.errCode != ErrorCode.NO_ERROR) {
                            ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                            mDetailView.showOwnerComment(false);
                            return;
                        }
                        mNextPageUrlcomment = response.nextPageUrl;
//                    if (TextUtils.isEmpty(mNextPageUrl) && mUpdateNextPageUrl.get()) {
//                        mNextPageUrl = response.nextPageUrl;
//                        mUpdateNextPageUrl.set(false);
//                    }
                        List<Comment> comments = response.result;
                        mFeedItem.comments.clear();
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mDetailView.updateCommentView();
                        mDetailView.showOwnerComment(true);
                    }
                });


    }

    /**
     * 获取feed的评论列表</br>
     */
    public void loadCommentsFromServer() {
        mCurrentCommentMode = COMMENT_MODE.ALL;
        mCommunitySDK.fetchFeedComments(mFeedItem.id, getCommentOrder(),
                new SimpleFetchListener<CommentResponse>() {
                    @Override
                    public void onComplete(CommentResponse response) {
                        mDetailView.onRefreshEnd();
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.showAllComment(false);
                            return;
                        }

                        if (response.errCode == ErrorCode.ERR_CODE_FEED_UNAVAILABLE) {
                            ToastMsg.showShortMsgByResName("umeng_comm_discuss_feed_unavailable");
                            mDetailView.showAllComment(false);
                            return;
                        }
                        if (response.errCode != ErrorCode.NO_ERROR) {
                            ToastMsg.showShortMsgByResName("umeng_comm_load_failed");
                            mDetailView.showAllComment(false);
                            return;
                        }
                        mNextPageUrlcomment = response.nextPageUrl;
//                if (TextUtils.isEmpty(mNextPageUrl) && mUpdateNextPageUrl.get()) {
//                    mNextPageUrl = response.nextPageUrl;
//                    mUpdateNextPageUrl.set(false);
//                }
                        List<Comment> comments = response.result;
                        mFeedItem.comments.clear();
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mDetailView.updateCommentView();
                        mDetailView.showAllComment(true);
                        saveCommentsToDB(response.result);

                    }
                });
    }

    public void loadMoreComments() {
        if (TextUtils.isEmpty(mNextPageUrlcomment)) {
            mDetailView.onRefreshEnd();
            return;
        }

        mCommunitySDK.fetchNextPageData(mNextPageUrlcomment, CommentResponse.class,
                new SimpleFetchListener<CommentResponse>() {

                    @Override
                    public void onComplete(CommentResponse response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.onRefreshEnd();
                            return;
                        }
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            mNextPageUrlcomment = response.nextPageUrl;
                            mDetailView.loadMoreComment(response.result);
                            saveCommentsToDB(response.result);
                        } else {
                            ToastMsg.showShortMsgByResName("umeng_comm_request_failed");
                        }
                        mDetailView.onRefreshEnd();
                    }
                });
    }

    /**
     * 保存评论数据到数据库</br>
     *
     * @param comments
     */
    private void saveCommentsToDB(List<Comment> comments) {
        if (CommonUtils.isListEmpty(comments)) {
            return;
        }
        mDatabaseAPI.getFeedDBAPI().saveFeedToDB(mFeedItem); // 由于comment数据变化，此时需要级联更新对应的feed数据
        mDatabaseAPI.getCommentAPI().saveCommentsToDB(mFeedItem);

    }

    public void loadMoreLikes() {
        if (TextUtils.isEmpty(mNextPageUrllike)) {
            mDetailView.onRefreshEnd();
            return;
        }

        mCommunitySDK.fetchNextPageData(mNextPageUrllike, LikesResponse.class,
                new Listeners.SimpleFetchListener<LikesResponse>() {

                    @Override
                    public void onComplete(LikesResponse response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            mDetailView.onRefreshEnd();
                            return;
                        }
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            mNextPageUrllike = response.nextPageUrl;
                            mDetailView.loadMoreLike(response.result);
                            saveLikesToDB(response.result);
                        } else {
                            ToastMsg.showShortMsgByResName("umeng_comm_request_failed");
                        }
                        mDetailView.onRefreshEnd();
                    }
                });
    }

    public void postLike(final String feedId) {
        mLikePresenter.postLike(feedId);
    }

    public void postUnlike(final String feedId) {
        mLikePresenter.postUnlike(feedId);
    }

    /**
     * 从数据库中加载Like</br>
     */
    private void loadLikesFromDB() {
        SimpleFetchListener<List<Like>> listener = new SimpleFetchListener<List<Like>>() {

            @Override
            public void onComplete(List<Like> likes) {
                likes.removeAll(mFeedItem.likes);
                mFeedItem.likes.addAll(likes);
                // mFeedItem.likeCount = mFeedItem.likes.size();
                mDetailView.updateLikeView("");
            }
        };
        mDatabaseAPI.getLikeDBAPI().loadLikesFromDB(mFeedItem, listener);
    }

    /**
     * 从数据库中加载评论</br>
     */
    private void loadCommentsFromDB() {
        mDatabaseAPI.getCommentAPI()
                .loadCommentsFromDB(mFeedItem.id, new SimpleFetchListener<List<Comment>>() {

                    @Override
                    public void onComplete(List<Comment> comments) {
                        comments = filterInvalidComment(comments);
                        comments.removeAll(mFeedItem.comments);
                        mFeedItem.comments.addAll(comments);
                        if (mFeedItem.commentCount == 0) {
                            mFeedItem.commentCount = mFeedItem.comments.size();
                        }
                        sortComments();
                        mDetailView.updateCommentView();
                    }
                });
    }

    private List<Comment> filterInvalidComment(List<Comment> data) {
        List<Comment> comments = new LinkedList<Comment>();
        for (Comment comment : data) {
            if (!(comment.status <= 5 && comment.status >= 2)) {
                comments.add(comment);
            }
        }
        return comments;
    }

    /**
     * 对评论进行排序
     */
    protected void sortComments() {
        Comparator comparator = getCommentComparator();
        if (comparator != null) {
            Collections.sort(mFeedItem.comments, comparator);
        }
    }

    /**
     * 获取compartor
     *
     * @return
     */
    private Comparator getCommentComparator() {
        Comparator comparator = null;
        switch (mStyle) {
            case WEIBO:
                comparator = mCreateTimeComparator;
                break;
            case DISCUSS:
                comparator = mFloorComparator;
                break;
            case SIMPLIFY:
                comparator = mCreateTimeComparator;
            default:
                break;
        }
        return comparator;
    }

    /**
     * 论坛版，按楼层排序
     */
    private Comparator<Comment> mFloorComparator = new Comparator<Comment>() {

        @Override
        public int compare(Comment lhs, Comment rhs) {
            if (lhs != null && rhs != null) {
                return lhs.floor < rhs.floor ? -1 : 1;
            }
            return lhs == null ? -1 : 0;
        }
    };

    /**
     * 微博版，按时间排序
     */
    private Comparator<Comment> mCreateTimeComparator = new Comparator<Comment>() {

        @Override
        public int compare(Comment lhs, Comment rhs) {
            if (rhs != null && rhs.createTime != null && lhs != null && lhs.createTime != null) {
                return rhs.createTime.compareTo(lhs.createTime);
            }
            return (lhs == null || lhs.createTime == null) ? -1 : 1;
        }
    };

    @Override
    public void loadDataFromServer() {
        switch (mStyle) {
            case WEIBO:
                loadLikesFromServer();
                break;

            default:
                break;
        }
        if (mCurrentCommentMode == COMMENT_MODE.ALL) {
            loadCommentsFromServer();
        } else {
            loadCommentsByuserFromServer();
        }
    }

    @Override
    public void loadDataFromDB() {
        switch (mStyle) {
            case WEIBO:
                loadLikesFromDB();
                break;

            default:
                break;
        }
        loadCommentsFromDB();
    }

    /**
     * feed详情presenter的action style
     * 微博与论坛版的区别
     * 1 加否加载点赞
     * 2排序规则不同
     * 简版与微博版相同
     */
    public enum FEED_DETAIL_PRESENTER_STYLE {
        WEIBO,
        DISCUSS,
        SIMPLIFY
    }

    public enum COMMENT_MODE {
        ALL,// 全部评论
        FEED_CREATOR // 只看楼主评论
    }

    /**
     * 根据不同的style，选择不同的排序规则
     *
     * @return
     */
    private Comment.CommentOrder getCommentOrder() {
        Comment.CommentOrder commentOrder = Comment.CommentOrder.ASC;
        switch (mStyle) {
            case WEIBO:
                commentOrder = Comment.CommentOrder.DESC;
                break;
            case DISCUSS:
                commentOrder = Comment.CommentOrder.ASC;
                break;
            case SIMPLIFY:
                commentOrder = Comment.CommentOrder.DESC;
                break;
            default:
                break;
        }
        return commentOrder;
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        mContext.registerReceiver(mLoginReceiver, new IntentFilter(Constants.ACTION_LOGIN_SUCCESS));
        BroadcastUtils.registerFeedBroadcast(mContext, mReceiver);
    }

    /**
     * 取消注册广播
     */
    private void unregisterReceiver() {
        mContext.unregisterReceiver(mLoginReceiver);
        BroadcastUtils.unRegisterBroadcast(mContext, mReceiver);
    }

    /**
     * 登录成功广播
     */
    private class LoginReceiver extends BaseBroadcastReceiver {
        @Override
        protected void onReceiveIntent(Context context, Intent intent) {
            if (Constants.ACTION_LOGIN_SUCCESS.equals(intent.getAction())) {
                loadDataFromServer();
            }
        }
    }

    /**
     * 微社区广播
     */
    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveFeed(android.content.Intent intent) {
            FeedItem feedItem = getFeed(intent);
            if (feedItem == null) {
                return;
            }
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_POST == type) {
                updateForwarCount(feedItem, 1);
            } else if (BroadcastUtils.BROADCAST_TYPE.TYPE_FEED_DELETE == type) {
                updateForwarCount(feedItem, -1);
            }
        }
    };

    private void updateForwarCount(FeedItem item, int count) {
        if (TextUtils.isEmpty(item.sourceFeedId)) {
            return;
        }
        mDetailView.updateForwardCount(count);
    }

}
