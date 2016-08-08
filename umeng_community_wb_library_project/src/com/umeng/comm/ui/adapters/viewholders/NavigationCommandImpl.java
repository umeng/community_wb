package com.umeng.comm.ui.adapters.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.ui.activities.FeedDetailActivity;
import com.umeng.comm.ui.activities.FollowedTopicActivity;
import com.umeng.comm.ui.activities.NewMsgActivity;
import com.umeng.comm.ui.activities.PostFeedActivity;
import com.umeng.comm.ui.activities.RelativeUserActivity;
import com.umeng.comm.ui.activities.SearchActivity;
import com.umeng.comm.ui.activities.SearchTopicActivity;
import com.umeng.comm.ui.activities.TopicActivity;
import com.umeng.comm.ui.activities.TopicDetailActivity;
import com.umeng.comm.ui.activities.UserInfoActivity;
import com.umeng.comm.ui.adapters.FavouriteFeedAdapter;
import com.umeng.comm.ui.adapters.FeedAdapter;
import com.umeng.common.ui.activities.AlbumActivity;
import com.umeng.common.ui.adapters.FeedBaseAdapter;
import com.umeng.common.ui.adapters.viewholders.BaseNavigationCommand;

import java.util.ArrayList;

/**
 * Created by pei on 16/7/15.
 */
public class NavigationCommandImpl extends BaseNavigationCommand {

    public NavigationCommandImpl(Activity activity) {
        super(activity);
    }

    @Override
    public void navigateToUseProfile(CommUser user) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Constants.TAG_USER, user);
        startActivity(intent);
    }

    @Override
    public void navigateToTopicDetail(Topic topic) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TopicDetailActivity.class);
        intent.putExtra(Constants.TAG_TOPIC, topic);
        startActivity(intent);
    }

    @Override
    public void navigateToFeedDetail(FeedItem feedItem,boolean isDisplayTopic) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, FeedDetailActivity.class);
        String commentId = feedItem.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
        feedItem.extraData.clear();
        intent.putExtra(Constants.FEED, feedItem);
        startActivity(intent);
        feedItem.extraData.putString(HttpProtocol.COMMENT_ID_KEY, commentId);
    }

    @Override
    public void navigateToReplayComment(FeedItem feedItem) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.setExtrasClassLoader(ImageItem.class.getClassLoader());
        intent.putExtra(Constants.FEED, feedItem);
        String commentId = feedItem.extraData.getString(HttpProtocol.COMMENT_ID_KEY);
        // 传递评论的id
        intent.putExtra(HttpProtocol.COMMENT_ID_KEY, commentId);
//            Intent intent = new Intent(getActivity(), FeedDetailActivity.class);
//            intent.putExtra(Constants.TAG_FEED, feedItem);
        intent.putExtra(Constants.TAG_IS_COMMENT, true);
        intent.putExtra(Constants.TAG_IS_SCROLL, true);
        startActivity(intent);
    }

    @Override
    public void navigateToSearchTopic() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, SearchTopicActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToTopic(String topicid) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra("uid", topicid);
        startActivity(intent);
    }

    @Override
    public void navigateToPostFeed(Topic topic) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent postIntent = new Intent(context, PostFeedActivity.class);
        if (topic != null) {
            postIntent.putExtra(Constants.TAG_TOPIC, topic);
        }
        startActivity(postIntent);
    }

    @Override
    public void navigateToSearch() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToFeedNewMsg(CommUser user) {
        Intent intent = new Intent(getContext(), NewMsgActivity.class);
        intent.putExtra(Constants.USER, user);
        startActivity(intent);
    }

    @Override
    public void navigateToMyPic(CommUser user) {
        Intent intent = new Intent(getContext(), AlbumActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, user.id);
        startActivity(intent);
    }

    @Override
    public void navigateToMyFollow(CommUser user) {
        Intent intent = new Intent(getContext(), FollowedTopicActivity.class);
        intent.putExtra(Constants.USER_ID_KEY, user.id);
        startActivity(intent);
    }

    @Override
    public void navigateToFeedDetailByComment(FeedItem feedItem,boolean isDisplayTopic) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(Constants.TAG_FEED, feedItem);
        intent.putExtra(Constants.TAG_IS_COMMENT, true);
        intent.putExtra(Constants.TAG_IS_SCROLL, true);
        startActivity(intent);
    }

    @Override
    public void navigateToRelativeUser(ArrayList<CommUser> users, String nexturl) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, RelativeUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.TAG_USERS, users);
        bundle.putString(HttpProtocol.NAVIGATOR_KEY, nexturl);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public FeedBaseAdapter createFeedAdapter(Context context) {
        return new FeedAdapter(context);
    }

    @Override
    public FeedBaseAdapter createFavouriteAdapter(Context context) {
        return new FavouriteFeedAdapter(context);
    }


}
