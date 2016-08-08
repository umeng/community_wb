package com.umeng.common.ui.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.presenter.impl.HotTopicFeedPresenter;
import com.umeng.common.ui.util.Filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 15/12/2.
 */
public class HotTopicFeedFragment extends TopicFeedFragment {
    private TextView button1, button2, button3, button4;
    private int hottype = 4;
    private ArrayList<Integer> hotlist = new ArrayList<Integer>();

    public HotTopicFeedFragment() {
        isShowPostButton(false);
        isSetPaddingToListView(false);
    }

    public static HotTopicFeedFragment newTopicFeedFrmg(final Topic topic) {
        HotTopicFeedFragment topicFeedFragment = new HotTopicFeedFragment();
        topicFeedFragment.mTopic = topic;
        topicFeedFragment.mFeedFilter = new Filter<FeedItem>() {

            @Override
            public List<FeedItem> doFilte(List<FeedItem> newItems) {
                if (newItems == null || newItems.size() == 0) {
                    return newItems;
                }
                Iterator<FeedItem> iterator = newItems.iterator();
                while (iterator.hasNext()) {
                    List<Topic> topics = iterator.next().topics;
                    if (!topics.contains(topic)) {
                        iterator.remove();
                    }
                }
                return newItems;
            }
        };
        return topicFeedFragment;
    }

    public void setHotList(ArrayList<Integer> list) {
        this.hotlist = list;
    }

    @Override
    protected HotTopicFeedPresenter createPresenters() {
        HotTopicFeedPresenter presenter = new HotTopicFeedPresenter(this);
        presenter.setId(mTopic.id);
        presenter.setIsShowTopFeeds(isShowTopFeed);
        return presenter;
    }

    public void initSwitchView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_switch_button"), null);

        if (hotlist.size() == 0) {
            button1 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
            button2 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
            button3 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
            button4 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
            button4.setSelected(true);
            button1.setOnClickListener(switchListener);
            button2.setOnClickListener(switchListener);
            button3.setOnClickListener(switchListener);
            button4.setOnClickListener(switchListener);
            button1.setTag(1);
            button2.setTag(3);
            button3.setTag(7);
            button4.setTag(30);
            mLinearLayout.addView(headerView, 0);
        } else if (hotlist.size() == 1) {

        } else if (hotlist.size() == 2) {
            button1 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
            button2 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
            button3 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
            button4 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
            button4.setSelected(true);
            button1.setOnClickListener(switchListener);
            button1.setText(gettext(hotlist.get(0)));
            button1.setTag(hotlist.get(0));
            button2.setVisibility(View.GONE);
            button3.setVisibility(View.GONE);
            button4.setOnClickListener(switchListener);
            button4.setText(gettext(hotlist.get(1)));
            button4.setTag(hotlist.get(1));
            mLinearLayout.addView(headerView, 0);
        } else if (hotlist.size() == 3) {
            button1 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
            button2 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
            button3 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
            button4 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
            button4.setSelected(true);
            button1.setOnClickListener(switchListener);
            button1.setText(gettext(hotlist.get(0)));
            button1.setTag(hotlist.get(0));
            button2.setOnClickListener(switchListener);
            button2.setText(gettext(hotlist.get(1)));
            button2.setTag(hotlist.get(1));
            button3.setVisibility(View.GONE);
            button4.setOnClickListener(switchListener);
            button4.setText(gettext(hotlist.get(2)));
            button4.setTag(hotlist.get(2));
            mLinearLayout.addView(headerView, 0);
        } else if (hotlist.size() == 4) {
            button1 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
            button2 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
            button3 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
            button4 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
            button4.setSelected(true);
            button1.setOnClickListener(switchListener);
            button2.setOnClickListener(switchListener);
            button3.setOnClickListener(switchListener);
            button4.setOnClickListener(switchListener);
            button1.setText(gettext(hotlist.get(0)));
            button2.setText(gettext(hotlist.get(1)));
            button3.setText(gettext(hotlist.get(2)));
            button4.setText(gettext(hotlist.get(3)));
            button1.setTag(hotlist.get(0));
            button2.setTag(hotlist.get(1));
            button3.setTag(hotlist.get(2));
            button4.setTag(hotlist.get(3));
            mLinearLayout.addView(headerView, 0);
        }
    }

    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == ResFinder.getId("umeng_switch_button_one")) {
                boolean isneed = false;
                if (hottype != 1) {
                    isneed = true;
                }
                hottype = (int) view.getTag();
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(false);
                ChangeFragment(hottype, isneed);
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_two")) {
                boolean isneed = false;
                if (hottype != 3) {
                    isneed = true;
                }
                hottype = (int) view.getTag();
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
                button4.setSelected(false);
                ChangeFragment(hottype, isneed);
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_three")) {
                boolean isneed = false;
                if (hottype != 7) {
                    isneed = true;
                }
                hottype = (int) view.getTag();
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
                button4.setSelected(false);
                ChangeFragment(hottype, isneed);
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_four")) {
                boolean isneed = false;
                if (hottype != 30) {
                    isneed = true;
                }
                hottype = (int) view.getTag();
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(true);
                ChangeFragment(hottype, isneed);
            }
        }
    };

    @Override
    protected void initWidgets() {
        super.initWidgets();
//        BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
        initSwitchView();
    }

    public void ChangeFragment(int hottype, boolean isneedsavenextpage) {

        if (mPresenter != null) {
            if (isneedsavenextpage) {
                mPresenter.setIsNeedRemoveOldFeeds();
            }
            ((HotTopicFeedPresenter) mPresenter).loadDataFromServer(hottype);
        }
    }

    private String gettext(int i) {
        switch (i) {
            case 1:
                return ResFinder.getString("umeng_comm_oneday");
            case 3:
                return ResFinder.getString("umeng_comm_threeday");
            case 7:
                return ResFinder.getString("umeng_comm_sevenday");
            case 30:
                return ResFinder.getString("umeng_comm_thrityday");
            default:
                return ResFinder.getString("umeng_comm_thrityday");
        }
    }
}
