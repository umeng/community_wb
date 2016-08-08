package com.umeng.common.ui.fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;

import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.presenter.impl.HottestFeedPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umeng on 12/1/15.
 */
public class HotFeedsFragment extends FeedListFragment<HottestFeedPresenter> {

    HottestFeedPresenter mHottestFeedPresenter;
    private TextView button1, button2, button3, button4;
    private ArrayList<Integer> hotlist = new ArrayList<Integer>();

    public HotFeedsFragment(){
        setShowtopFeed(true);
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        initSwitchView();

    }

    @Override
    protected void showPostButtonWithAnim() {
        AlphaAnimation showAnim = new AlphaAnimation(0.5f, 1.0f);
        showAnim.setDuration(500);

    }

    public void setHotList(ArrayList<Integer> list) {
        this.hotlist = list;
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
            if (mPresenter.isRefreshing()) {
                return;
            }
            if (view.getId() == ResFinder.getId("umeng_switch_button_one")) {
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer((Integer) view.getTag());
                } else {
                    mPresenter.loadDataFromDB((Integer) view.getTag());
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_two")) {
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer((Integer) view.getTag());
                } else {
                    mPresenter.loadDataFromDB((Integer) view.getTag());
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_three")) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer((Integer) view.getTag());
                } else {
                    mPresenter.loadDataFromDB((Integer) view.getTag());
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_four")) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(true);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer((Integer) view.getTag());
                } else {
                    mPresenter.loadDataFromDB((Integer) view.getTag());
                }
            }
        }
    };

    @Override
    protected HottestFeedPresenter createPresenters() {
        mHottestFeedPresenter = new HottestFeedPresenter(this);
        mHottestFeedPresenter.setIsShowTopFeeds(isShowTopFeed);
        return mHottestFeedPresenter;
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

    private int getindex(String name) {
        if (name.equals(ResFinder.getString("umeng_comm_oneday"))) {
            return 1;
        } else if (name.equals(ResFinder.getString("umeng_comm_threeday"))) {
            return 3;
        } else if (name.equals(ResFinder.getString("umeng_comm_sevenday"))) {
            return 7;
        } else {
            return 30;
        }
    }
}
