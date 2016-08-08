package com.umeng.common.ui.adapters.viewholders;

import android.content.Context;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.common.ui.adapters.FeedBaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pei on 16/7/14.
 */
public interface NavigationCommand extends Serializable{

    public void navigateToUseProfile(CommUser user);

    public void navigateToTopicDetail(Topic topic);

    public void navigateToFeedDetail(FeedItem feedItem,boolean isDisplayTopic);

    public void navigateToReplayComment(FeedItem feedItem);

    public void navigateToSearchTopic();

    public void navigateToTopic(String topicid);

    public void navigateToPostFeed(Topic topic);

    public void navigateToSearch();

    public void navigateToFeedNewMsg(CommUser user);

    public void navigateToMyPic(CommUser user);

    public void navigateToMyFollow(CommUser user);

    public FeedBaseAdapter createFeedAdapter(Context context);

    public FeedBaseAdapter createFavouriteAdapter(Context context);

    public void navigateToFeedDetailByComment(FeedItem feedItem,boolean isDisplayTopic);

    public void navigateToRelativeUser(ArrayList<CommUser> users,String nexturl);
}
