/**
 *
 */
package com.umeng.common.ui.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.FollowDBAPI;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.FansResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.mvpview.MvpActiveUserFgView;
import com.umeng.common.ui.mvpview.MvpFollowedUserView;
import com.umeng.common.ui.presenter.BaseFragmentPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;


/**
 *
 */
public class FollowedUserFgPresenter extends ActiveUserFgPresenter {

    protected String mUid;
    protected FollowDBAPI mFollowDBAPI = DatabaseAPI.getInstance().getFollowDBAPI();
    protected String nextPageUrl;
//    private boolean hasRefresh = false;
//    protected boolean isFollowPage = true;

    public FollowedUserFgPresenter(MvpActiveUserFgView followedUserView, String uid) {
        super(followedUserView, true);

        this.mUid = uid;
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
        BroadcastUtils.registerUserBroadcast(mContext, mReceiver);
    }

    @Override
    public void detach() {
        BroadcastUtils.unRegisterBroadcast(mContext, mReceiver);
        super.detach();
    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchFollowedUser(mUid, new FetchListener<FansResponse>() {

            @Override
            public void onStart() {
                mActiveUserFgView.onRefreshStart();
            }

            @Override
            public void onComplete(FansResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    mActiveUserFgView.onRefreshEnd();
                    if (response.errCode == ErrorCode.NO_ERROR) {
                        nextPageUrl = "";
                    }
                    return;
                }

                final List<CommUser> followedUsers = response.result;
                // 保存数据
                if (CommonUtils.isMyself(new CommUser(mUid))) {
                    mFollowDBAPI.follow(followedUsers);
                }
//                mActiveUserFgView.executeCallback(followedUsers.size());
                // 更新GridView
                List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
//                followedUsers.removeAll(dataSource);
                dataSource.clear();
                dataSource.addAll(followedUsers);
                mActiveUserFgView.notifyDataSetChanged();
                // 解析下一页地址
                parseNextpageUrl(response, true);
                dealResult(response, true);
                mActiveUserFgView.onRefreshEnd();
            }
        });
    }

    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveUser(Intent intent) {
            if (mUid.equals(CommConfig.getConfig().loginedUser.id)) {
                CommUser user = getUser(intent);// 取消关注某个用户
                BroadcastUtils.BROADCAST_TYPE type = getType(intent);
                onUserFollowStateChange(user, type);
            }
        }
    };

    protected Activity convertContextToActivity() {
        if (mContext instanceof Activity) {
            return (Activity) mContext;
        }

        return null;
    }

    @Override
    public void loadDataFromDB() {
        if (!mUid.equals(CommConfig.getConfig().loginedUser.id)) {
            return;
        }
        mFollowDBAPI.loadFollowedUsersFromDB(mUid, new
                SimpleFetchListener<List<CommUser>>() {
                    @Override
                    public void onComplete(List<CommUser> fans) {
                        if (CommonUtils.isActivityAlive(convertContextToActivity())
                                && !CommonUtils.isListEmpty(fans)) {
                            List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
                            fans.removeAll(dataSource);
                            if (fans.size() > 0) {
                                dataSource.addAll(fans);
                                mActiveUserFgView.notifyDataSetChanged();
                            }
//                            mActiveUserFgView.executeCallback(fans.size());
                            mActiveUserFgView.onRefreshEnd();
                        }
                    }
                });
    }

    @Override
    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPageUrl)) {
            mActiveUserFgView.onRefreshEnd();
            return;
        }

        mCommunitySDK.fetchNextPageData(nextPageUrl, FansResponse.class,
                new SimpleFetchListener<FansResponse>() {

                    @Override
                    public void onComplete(FansResponse response) {
                        // 根据response进行Toast
                        if (NetworkUtils.handleResponseAll(response)) {
                            if (response.errCode == ErrorCode.NO_ERROR) {
                                nextPageUrl = "";
                            }
                            mActiveUserFgView.onRefreshEnd();
                            return;
                        }
                        // 保存到数据库
                        mFollowDBAPI.follow(response.result);
                        appendUsers(response.result);
                        parseNextpageUrl(response, false);
                        dealResult(response, false);
                        mActiveUserFgView.onRefreshEnd();
                    }
                });
    }

    /**
     * 追加已关注的用户，并刷新adapter</br>
     *
     * @param newUsers 新关注的好友
     */
    protected void appendUsers(List<CommUser> newUsers) {
        List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
        newUsers.removeAll(dataSource);
        dataSource.addAll(newUsers);
        mActiveUserFgView.notifyDataSetChanged();
    }

    /**
     * 在其他页面对某个用户进行取消关注、关注之后需要从关注列表中移除或者添加
     *
     * @param user
     * @param type
     */
    protected void onUserFollowStateChange(CommUser user, BroadcastUtils.BROADCAST_TYPE type) {
        List<CommUser> dataSource = mActiveUserFgView.getBindDataSource();
        if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
            if (!dataSource.contains(user)) {
                dataSource.add(user);
                mFollowDBAPI.follow(user);
            }
        } else {
            dataSource.remove(user);
            // 从DB中移除
            mFollowDBAPI.unfollow(user);
        }
        mActiveUserFgView.notifyDataSetChanged();
    }

    protected void parseNextpageUrl(FansResponse response, boolean fromRefersh) {
        if (fromRefersh && TextUtils.isEmpty(nextPageUrl)) {
            nextPageUrl = response.nextPageUrl;
        } else if (!fromRefersh) {
            nextPageUrl = response.nextPageUrl;
        }
    }
}
