package com.umeng.comm.ui.fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.SearchActivity;
import com.umeng.common.ui.presenter.impl.HottestFeedPresenter;

/**
 * Created by wangfei on 16/1/19.
 */
public class HotFeedFragment extends PostBtnAnimFragment<HottestFeedPresenter> {
    private TextView mTipView; // 更新条数提示
    private boolean isShowToast = false; // 只有在显示的fragment是当前fragment时，才显示Toast

    private TextView button1, button2, button3, button4;

    @Override
    protected HottestFeedPresenter createPresenters() {
        super.createPresenters();
        HottestFeedPresenter presenter = new HottestFeedPresenter(this, true);
        presenter.setOnResultListener(mListener);
        return presenter;
    }

    /**
     * 用户回调显示更新数目
     */
    private Listeners.OnResultListener mListener = new Listeners.OnResultListener() {

        @Override
        public void onResult(int nums) {
            if (!isShowToast) {
                return;
            }
            if (nums <= 0) {
                mTipView.setText(ResFinder.getString("umeng_comm_no_newfeed_tips"));
            } else {
                mTipView.setText(ResFinder.getString("umeng_comm_newfeed_tips"));
            }
            showNewFeedTips();
        }
    };

    /**
     * 显示[更新N条新feed]】的View</br>
     */
    private void showNewFeedTips() {
        mTipView.setVisibility(View.VISIBLE);
        Animation showAnimation = new AlphaAnimation(0.2f, 1);
        showAnimation.setDuration(400);
        showAnimation.setFillAfter(true);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismissNewFeedTips();
            }
        });
        mTipView.startAnimation(showAnimation);
    }

    /**
     * 隐藏[更新N条feed]的View。注意：该方法必须由{@link #showNewFeedTips}的AnimationListener回调中被调用</br>
     */
    private void dismissNewFeedTips() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setStartOffset(800);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTipView.setVisibility(View.GONE);
            }
        });
        mTipView.startAnimation(animation);
    }

    @Override
    protected void showPostButtonWithAnim() {
        AlphaAnimation showAnim = new AlphaAnimation(0.5f, 1.0f);
        showAnim.setDuration(500);

        if (mPostBtn != null) {
            mPostBtn.setVisibility(View.VISIBLE);
            mPostBtn.startAnimation(showAnim);
        }
    }

    @Override
    public void initAdapter() {
        // 添加Header
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        View tipView = headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        FrameLayout.LayoutParams tipViewParams = (FrameLayout.LayoutParams) tipView.getLayoutParams();
        tipViewParams.topMargin = 0;
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtils.visitNum == 0) {
                    if (CommonUtils.isLogin(getActivity())) {
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int stCode, CommUser userInfo) {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    mProcessDialog.dismiss();
                                }
                                if (stCode == 0) {
                                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            }
                        });
                    }
                } else {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        mTipView = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_feeds_tips"));
        mFeedsListView.addHeaderView(headerView, null, false);
        initSwitchView();
        super.initAdapter();
    }

    public void initSwitchView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_switch_button"), null);

        button1 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
        button2 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
        button3 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
        button4 = (TextView) headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
        button4.setSelected(true);
        button1.setOnClickListener(switchListener);
        button2.setOnClickListener(switchListener);
        button3.setOnClickListener(switchListener);
        button4.setOnClickListener(switchListener);
        mLinearLayout.addView(headerView, 0);
    }

    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mPresenter.isRefreshing()) {
                return;
            }

            if (view.getId() == ResFinder.getId("umeng_switch_button_one")) {
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer(1);
                } else {
                    mPresenter.loadDataFromDB(1);
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_two")) {
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer(3);
                } else {
                    mPresenter.loadDataFromDB(3);
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_three")) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
                button4.setSelected(false);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer(7);
                } else {
                    mPresenter.loadDataFromDB(7);
                }
            } else if (view.getId() == ResFinder.getId("umeng_switch_button_four")) {
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(true);
                if (NetworkUtils.isConnectedToNetwork(getActivity())) {
                    mPresenter.loadDataFromServer(30);
                } else {
                    mPresenter.loadDataFromDB(30);
                }
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShowToast = isVisibleToUser;
    }
}
