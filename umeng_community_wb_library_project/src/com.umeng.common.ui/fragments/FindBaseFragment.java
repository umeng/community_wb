package com.umeng.common.ui.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.MessageCount;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.common.ui.activities.SettingActivity;
import com.umeng.common.ui.adapters.MyAdapterInFind;
import com.umeng.common.ui.adapters.viewholders.NavigationCommand;
import com.umeng.common.ui.colortheme.ColorQueque;
import com.umeng.common.ui.configure.FindCell;
import com.umeng.common.ui.configure.FindItem;
import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.presenter.impl.NullPresenter;
import com.umeng.common.ui.presenter.impl.UserInfoPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.util.UserTypeUtil;
import com.umeng.common.ui.widgets.RoundImageView;

import java.util.ArrayList;

/**
 * Created by wangfei on 16/7/28.
 */
public  class FindBaseFragment extends BaseFragment<Void,NullPresenter> implements View.OnClickListener{
    protected CommUser mUser;
    protected String mContainerClass;
    protected MessageCount mUnReadMsg;
    protected View mMsgBadgeView;
    protected LinearLayout typeContainer;
    protected Dialog processDialog;
    protected LinearLayout listContainer;
    protected MyAdapterInFind msgAdapter;
   // protected Button mSettingBtn;
   // protected TextView mTitleTextView;
    protected Handler mFindActivityHandler = new Handler();

    protected RecommendTopicFragment mRecommendTopicFragment;
    protected RecommendUserFragment mRecommendUserFragment;
    protected FriendsFragment mFriendsFragment;
    protected SettingFragment mSettingFragment;
    protected NearbyFeedFragment mNearbyFeedFragment;
    protected FavoritesFragment mFavoritesFragment;
    protected RealTimeFeedFragment mRealTimeFeedFragment;
    protected NearByUserFragment mNearByUserFragment;
    public Fragment mCurrentFragment;
    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_find_frg");
    }

    @Override
    protected void initWidgets() {
        processDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
      //  findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(this);

        findViewById(ResFinder.getId("user_have_login")).setOnClickListener(this);
        findViewById(ResFinder.getId("user_haveno_login")).setOnClickListener(this);

        typeContainer = (LinearLayout) findViewById(ResFinder.getId("user_type_icon_container"));

       // mSettingBtn = (Button) findViewById(ResFinder.getId("umeng_comm_save_bt"));
      //  mSettingBtn.setOnClickListener(this);
      //  mSettingBtn.setCompoundDrawablesWithIntrinsicBounds(ResFinder.getDrawable("umeng_comm_setting_bt"), null, null, null);

      //  mTitleTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_setting_title"));

      //  mTitleTextView.setText(ResFinder.getString("umeng_comm_mine"));

        listContainer = (LinearLayout) findViewById(ResFinder.getId("lists"));
        mFragmentManager = getChildFragmentManager();
        initList();
        parseIntentData();
        setupUnreadFeedMsgBadge();
        mUser = CommonUtils.getLoginUser(getActivity());
        registerInitSuccessBroadcast();
        initUserInfo();
    }
    protected void initList() {
        if (parseJson.findItems.size() == 0 ) {
            TextView first = new TextView(getActivity());
            first.setBackgroundColor(ColorQueque.getColor("umeng_comm_feed_list_bg"));
            first.setTextSize(14);
            first.setPadding(DeviceUtils.dp2px(getActivity(), 10), DeviceUtils.dp2px(getActivity(), 5), 0, DeviceUtils.dp2px(getActivity(), 5));
            first.setTextColor(ColorQueque.getColor("umeng_comm_active_user_name_textcolor"));
            first.setText(ResFinder.getString("umeng_comm_mine"));
            listContainer.addView(first);
            View divide = new View(getActivity());
            divide.setBackgroundColor(ColorQueque.getColor("umeng_comm_divider"));
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dp2px(getActivity(), 1));
            divide.setLayoutParams(dlp);
            listContainer.addView(divide);
            ListView listView1 = new ListView(getActivity());
            listView1.setDividerHeight(0);
            ArrayList<FindCell> firstlist = new ArrayList<FindCell>();
            FindCell cell11 = new FindCell(ResFinder.getString("umeng_comm_user_notification"),"umeng_comm_user_notification","umeng_comm_notification_icon");
            FindCell cell12 = new FindCell(ResFinder.getString("umeng_comm_user_favorites"),"umeng_comm_user_favorites","umeng_comm_favortes_icon");
            FindCell cell13 = new FindCell(ResFinder.getString("umeng_comm_recommend_friends"),"umeng_comm_recommend_friends","umeng_comm_firends_icon");
            FindCell cell14 = new FindCell(ResFinder.getString("umeng_comm_myfocus"),"umeng_comm_myfocus","umeng_comm_mytopics_icon");
            FindCell cell15 = new FindCell(ResFinder.getString("umeng_comm_mypics"),"umeng_comm_mypics","umeng_comm_mypics_icon");
            firstlist.add(cell11);
            firstlist.add(cell12);
            firstlist.add(cell13);
            firstlist.add(cell14);
            firstlist.add(cell15);
            MyAdapterInFind adapterInFind = new MyAdapterInFind(getActivity(), firstlist);
            if (firstlist.contains(ResFinder.getString("umeng_comm_user_notification"))) {
                msgAdapter = adapterInFind;
            }
            listView1.setAdapter(adapterInFind);
            listView1.setVerticalScrollBarEnabled(false);
            listView1.setOnItemClickListener(listener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, firstlist.size() * DeviceUtils.dp2px(getActivity(), 48));//每行固定高48
            listContainer.addView(listView1, lp);


            TextView second = new TextView(getActivity());
            second.setBackgroundColor(ColorQueque.getColor("umeng_comm_feed_list_bg"));
            second.setTextSize(14);
            second.setPadding(10, 5, 0, 5);
            second.setTextColor(ColorQueque.getColor("umeng_comm_active_user_name_textcolor"));
            second.setText(ResFinder.getString("umeng_comm_recommend"));
            listContainer.addView(second);

            View divide2 = new View(getActivity());
            divide2.setBackgroundColor(ColorQueque.getColor("umeng_comm_divider"));
            divide2.setLayoutParams(dlp);
            listContainer.addView(divide2);

            ListView listView2 = new ListView(getActivity());
            ArrayList<FindCell> secondlist = new ArrayList<FindCell>();
            FindCell cell21 = new FindCell(ResFinder.getString("umeng_comm_recommend_nearby"),"umeng_comm_recommend_nearby","umeng_comm_nearby_user_icon");
            FindCell cell22 = new FindCell(ResFinder.getString("umeng_comm_nearby_user"),"umeng_comm_nearby_user","umeng_comm_realtime_icon");
            FindCell cell23 = new FindCell(ResFinder.getString("umeng_comm_realtime"),"umeng_comm_realtime","umeng_comm_recommend_user_icon");
            FindCell cell24 = new FindCell(ResFinder.getString("umeng_comm_recommend_user"),"umeng_comm_recommend_user","umeng_comm_recommend_topic_icon");
            FindCell cell25 = new FindCell(ResFinder.getString("umeng_comm_recommend_topic"),"umeng_comm_recommend_topic","umeng_comm_recommend_topic_icon");
            secondlist.add(cell21);
            secondlist.add(cell22);
            secondlist.add(cell23);
            secondlist.add(cell24);
            secondlist.add(cell25);
            MyAdapterInFind adapterInFind2 = new MyAdapterInFind(getActivity(), secondlist);
            if (secondlist.contains(ResFinder.getString("umeng_comm_user_notification"))) {
                msgAdapter = adapterInFind2;
            }
            listView2.setDividerHeight(0);
            listView2.setAdapter(adapterInFind2);
            listView2.setVerticalScrollBarEnabled(false);
            listView2.setOnItemClickListener(listener);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, secondlist.size() * DeviceUtils.dp2px(getActivity(), 48));//每行固定高48
            listContainer.addView(listView2, lp2);
        } else {
            if (parseJson.findItems.get(0).list.size()!=0) {
                FindItem item= parseJson.findItems.get(0);
                TextView first = new TextView(getActivity());
                first.setBackgroundColor(ColorQueque.getColor("umeng_comm_feed_list_bg"));
                first.setTextSize(14);
                first.setPadding(DeviceUtils.dp2px(getActivity(), 10), DeviceUtils.dp2px(getActivity(), 5), 0, DeviceUtils.dp2px(getActivity(), 5));
                first.setTextColor(ColorQueque.getColor("umeng_comm_active_user_name_textcolor"));
                first.setText(item.title);
                listContainer.addView(first);
                View divide = new View(getActivity());
                divide.setBackgroundColor(ColorQueque.getColor("umeng_comm_divider"));
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dp2px(getActivity(), 1));
                divide.setLayoutParams(dlp);

                listContainer.addView(divide);

                ListView listView1 = new ListView(getActivity());
                listView1.setDividerHeight(0);
             //   ArrayList<FindCell> firstlist = new ArrayList<FindCell>();

                MyAdapterInFind adapterInFind = new MyAdapterInFind(getActivity(), item.list);
                for (FindCell cell:item.list){
                    if (cell.style.equals("umeng_comm_user_notification") ){
                        msgAdapter = adapterInFind;
                    }
                }
                listView1.setAdapter(adapterInFind);
                listView1.setVerticalScrollBarEnabled(false);
                listView1.setOnItemClickListener(listener);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, item.list.size() * DeviceUtils.dp2px(getActivity(), 48));//每行固定高48
                listContainer.addView(listView1, lp);
            }
            if (parseJson.findItems.get(1).list.size()!=0) {
                FindItem item= parseJson.findItems.get(1);
                TextView second = new TextView(getActivity());
                second.setBackgroundColor(ColorQueque.getColor("umeng_comm_feed_list_bg"));
                second.setTextSize(14);
                second.setPadding(10, 5, 0, 5);
                second.setTextColor(ColorQueque.getColor("umeng_comm_active_user_name_textcolor"));
                second.setText(item.title);
                listContainer.addView(second);

                View divide2 = new View(getActivity());
                divide2.setBackgroundColor(ColorQueque.getColor("umeng_comm_divider"));
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dp2px(getActivity(), 1));
                divide2.setLayoutParams(dlp);
                listContainer.addView(divide2);

                ListView listView2 = new ListView(getActivity());
              //  ArrayList<String> secondlist = new ArrayList<String>();


                MyAdapterInFind adapterInFind2 = new MyAdapterInFind(getActivity(), item.list);
                for (FindCell cell:item.list){
                    if (cell.style.equals("umeng_comm_user_notification") ){
                        msgAdapter = adapterInFind2;
                    }
                }
                listView2.setDividerHeight(0);
                listView2.setAdapter(adapterInFind2);
                listView2.setVerticalScrollBarEnabled(false);
                listView2.setOnItemClickListener(listener);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, item.list.size() * DeviceUtils.dp2px(getActivity(), 48));//每行固定高48
                listContainer.addView(listView2, lp2);
            }
        }
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /**     */
            final String itemStr = (String) view.getTag();
            String realTimeStr = ResFinder.getString("umeng_comm_realtime");
            String recommendUserStr = ResFinder.getString("umeng_comm_recommend_user");
            String recommendTopicStr = ResFinder.getString("umeng_comm_recommend_topic");
            if (itemStr.equals(realTimeStr) || itemStr.equals(recommendUserStr) || itemStr.equals(recommendTopicStr)) {
                showFragment(itemStr);
            } else {
                if (!CommonUtils.isLogin(getActivity())) {
                    CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                        @Override
                        public void onStart() {
                            processDialog.show();
                        }

                        @Override
                        public void onComplete(int stCode, CommUser userInfo) {
                            processDialog.dismiss();
                            if (stCode == 0) {
                                showFragment(itemStr);
                            }
                        }
                    });
                } else {
                    showFragment(itemStr);
                }
            }
        }
    };
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == ResFinder.getId("user_have_login")) { // 个人中心
            gotoUserInfoActivity();
        } else if (id == ResFinder.getId("user_haveno_login")) { // 个人中心
            CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                @Override
                public void onStart() {
                    processDialog.show();
                }

                @Override
                public void onComplete(int stCode, CommUser userInfo) {
                    initUserInfo();
                    processDialog.dismiss();
                }
            });
        }
    }
    protected void initUserInfo() {
        if (CommonUtils.isLogin(getActivity())) {
            CommUser user = CommConfig.getConfig().loginedUser;
            findViewById(ResFinder.getId("user_have_login")).setVisibility(View.VISIBLE);
            findViewById(ResFinder.getId("user_haveno_login")).setVisibility(View.GONE);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon"))).setImageDrawable(ColorQueque.getDrawable("umeng_comm_defaul_icon"));
            ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon"))).setImageUrl(user.iconUrl, option);
            ((TextView) findViewById(ResFinder.getId("user_name_tv"))).setText(user.name);

            StringBuffer content = new StringBuffer(ResFinder.getString("umeng_comm_my_fans"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.fansCount));
            ((TextView) findViewById(ResFinder.getId("user_fanscount"))).setText(content.toString());

            content.delete(0, content.length());
            content.append(ResFinder.getString("umeng_comm_followed_user"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.followCount));
            ((TextView) findViewById(ResFinder.getId("user_focuscount"))).setText(content.toString());

            content.delete(0, content.length());
            content.append(ResFinder.getString("umeng_comm_user_socre"));
            content.append(" ").append(CommonUtils.getLimitedCount(user.point));
            ((TextView) findViewById(ResFinder.getId("user_score"))).setText(content.toString());
        } else {
            findViewById(ResFinder.getId("user_haveno_login")).setVisibility(View.VISIBLE);
            findViewById(ResFinder.getId("user_have_login")).setVisibility(View.GONE);
            ((RoundImageView) findViewById(ResFinder.getId("userinfo_headicon_nologin"))).setImageDrawable(ColorQueque.getDrawable("umeng_comm_defaul_icon"));
            ((TextView) findViewById(ResFinder.getId("user_name_tv_nologin"))).setText("立即登陆");
        }
        mUser = CommConfig.getConfig().loginedUser;
        displayUserMedal();
        if (mUser.medals == null || mUser.medals.isEmpty()) {
            loadUserFromDB(mUser.id);
        }
    }
    protected void displayUserMedal() {
        CommUser user = CommConfig.getConfig().loginedUser;
        if (user.medals == null || user.medals.isEmpty()) {
            typeContainer.setVisibility(View.GONE);
        } else {
            typeContainer.setVisibility(View.VISIBLE);
            UserTypeUtil.SetUserType(getActivity(), user, typeContainer);
        }
    }

    protected void loadUserFromDB(final String uId) {
        DatabaseAPI.getInstance().getUserDBAPI().loadUserFromDB(uId, new Listeners.SimpleFetchListener<CommUser>() {
            @Override
            public void onComplete(CommUser user) {
                if (!getActivity().isFinishing()) {
                    if (user != null) {
                        CommConfig.getConfig().loginedUser.medals = user.medals;
                        displayUserMedal();
                    }
                }
            }
        });
    }

    protected void parseIntentData() {
//        mUser = getIntent().getExtras().getParcelable(Constants.TAG_USER);
//        mContainerClass = getIntent().getExtras().getString(Constants.TYPE_CLASS);
//        mUnReadMsg = CommConfig.getConfig().mMessageCount;
    }
    protected void setupUnreadFeedMsgBadge() {
        if (msgAdapter != null&&mUnReadMsg!=null) {
            msgAdapter.setUnReadcount(mUnReadMsg.unReadTotal);
            msgAdapter.notifyDataSetChanged();
        }

    }
    /**
     * 跳转到我关注的话题Activity</br>
     */
    protected  void gotoMyFollowActivity() {
        command.navigateToMyFollow( CommConfig.getConfig().loginedUser);
    }

    /**
     * 跳转到我的相册Activity</br>
     */
    protected  void gotoMyPicActivity(){
        command.navigateToMyPic( CommConfig.getConfig().loginedUser);
    }
    /**
     * 跳转到我的消息Activity</br>
     */
    protected  void gotoFeedNewMsgActivity(){
        command.navigateToFeedNewMsg( CommConfig.getConfig().loginedUser);
    }
    /**
     * 跳转到用户中心Activity</br>
     */
    protected  void gotoUserInfoActivity() {
        command.navigateToUseProfile( CommConfig.getConfig().loginedUser);
    }
    /**
     * 跳转到用户中心Activity</br>
     */
    protected  void gotoSettingActivity() {
        Intent setting = new Intent(getActivity(), SettingActivity.class);
        setting.putExtra(Constants.TYPE_CLASS, mContainerClass);
        startActivity(setting);
    }
    /**
     * 显示附近推荐Feed</br>
     */

    protected void showNearbyFeed() {
        if (mNearbyFeedFragment == null) {
            mNearbyFeedFragment = NearbyFeedFragment.newNearbyFeedRecommend();
            mNearbyFeedFragment.setShowActionbar(false);
            mNearbyFeedFragment.setNavigation(command);
            mNearbyFeedFragment.isShowSearchBar(false);
        }
        showCommFragment(mNearbyFeedFragment);
    }

    /**
     * 显示附近用户Feed</br>
     */
    protected void showNearByUser() {
        if (mNearByUserFragment == null) {
            mNearByUserFragment = NearByUserFragment.newNearbyUserFragment();
            mNearByUserFragment.setNavigation(command);
        }
        showCommFragment(mNearByUserFragment);
    }

    /**
     * 显示实时内容的Fragment</br>
     */
    /**
     * 显示实时内容的Fragment</br>
     */

    protected void showRealTimeFeed() {
        if (mRealTimeFeedFragment == null) {
            mRealTimeFeedFragment = RealTimeFeedFragment.newRealTimeFeedRecommend();
            mRealTimeFeedFragment.setShowActionbar(false);
            mRealTimeFeedFragment.setNavigation(command);
            mRealTimeFeedFragment.isShowSearchBar(false);
            mRealTimeFeedFragment.isShowPostButton(false);
        }
        showCommFragment(mRealTimeFeedFragment);
    }

    /**
     * 显示收藏Feed</br>
     */
    protected void showFavoritesFeed() {
        if (mFavoritesFragment == null) {
            mFavoritesFragment = FavoritesFragment.newFavoritesFragment();
            mFavoritesFragment.setNavigation(command);
            mFavoritesFragment.setShowActionbar(false);
            mFavoritesFragment.isShowSearchBar(false);
        }
        showCommFragment(mFavoritesFragment);
    }

    /**
     * 显示推荐话题</br>
     */

    protected void showRecommendTopic() {
        if (mRecommendTopicFragment == null) {
            mRecommendTopicFragment = RecommendTopicFragment.newRecommendTopicFragment();
            mRecommendTopicFragment.setSaveButtonInVisiable();
            mRecommendTopicFragment.isShowSearchBar(false);
            mRecommendTopicFragment.setShowActionbar(false);
            mRecommendTopicFragment.setNavigation(command);
        }
        showCommFragment(mRecommendTopicFragment);
    }
    protected void showSetting() {
        if (mSettingFragment == null) {
            mSettingFragment = new SettingFragment();
            mSettingFragment.setNavigation(command);
            mSettingFragment.setLogoutBtnClick(new SettingFragment.LogoutBtnClick() {
                @Override
                public void onClick() {
                    CommConfig.getConfig().loginedUser = CommonUtils.getLoginUser(getActivity());
//                    onBackBtnClick();
                }
            });
//            mSettingBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mSettingFragment.dealSaveLogic();
//                }
//            });
        }
        showCommFragment(mSettingFragment);
    }
    /**
     * 显示朋友圈Fragment</br>
     */
    protected void showFriendsFragment() {
        if (mFriendsFragment == null) {
            mFriendsFragment = FriendsFragment.newFriendsFragment();
            mFriendsFragment.setShowActionbar(false);
            mFriendsFragment.isShowSearchBar(false);
            mFriendsFragment.setNavigation(command);
        }
        showCommFragment(mFriendsFragment);
    }

    /**
     * 显示推荐用户fragment</br>
     */
    protected void showRecommendUserFragment() {
        if (mRecommendUserFragment == null) {
            mRecommendUserFragment = new RecommendUserFragment();
            mRecommendUserFragment.setSaveButtonInvisiable();
            mRecommendUserFragment.setShowActionbar(false);
            mRecommendUserFragment.setNavigation(command);
        }
        showCommFragment(mRecommendUserFragment);
    }
    protected void registerInitSuccessBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_INIT_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mInitConfigReceiver,
                filter);
        BroadcastUtils.registerCountBroadcast(getActivity().getApplicationContext(), mReceiver);
    }

    protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        @Override
        public void onReceiveCount(Intent intent) {
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            int count = getCount(intent);

            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_COUNT_USER) {
                if (Math.abs(count) <= 1) {// follow or unFollow 情况
                    CommConfig.getConfig().loginedUser = CommonUtils.getLoginUser(getActivity());
                    initUserInfo();
                }
            }
        }
    };
    protected BroadcastReceiver mInitConfigReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mUnReadMsg = CommConfig.getConfig().mMessageCount;
            setupUnreadFeedMsgBadge();
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mInitConfigReceiver);
        BroadcastUtils.unRegisterBroadcast(getActivity().getApplicationContext(), mReceiver);
        super.onDestroy();
    }
    /**
     * 处理跳转逻辑
     *
     * @param item
     */
    protected void showFragment(final String item) {
        final StringBuffer fragmentTitleResName = new StringBuffer();
        if (item.equals(ResFinder.getString("umeng_comm_user_notification"))) {
            gotoFeedNewMsgActivity();
        } else if (item.equals(ResFinder.getString("umeng_comm_user_favorites"))) {
            fragmentTitleResName.append("umeng_comm_favoriets_list");
            showFavoritesFeed();
        } else if (item.equals(ResFinder.getString("umeng_comm_recommend_friends"))) {
            fragmentTitleResName.append("umeng_comm_recommend_friends");
            showFriendsFragment();
        } else if (item.equals(ResFinder.getString("umeng_comm_myfocus"))) {
            gotoMyFollowActivity();
        } else if (item.equals(ResFinder.getString("umeng_comm_mypics"))) {
            gotoMyPicActivity();
        } else if (item.equals(ResFinder.getString("umeng_comm_recommend_nearby"))) {
            fragmentTitleResName.append("umeng_comm_recommend_nearby");
            showNearbyFeed();
        } else if (item.equals(ResFinder.getString("umeng_comm_nearby_user"))) {
            fragmentTitleResName.append("umeng_comm_nearby_user");
            showNearByUser();
        } else if (item.equals(ResFinder.getString("umeng_comm_realtime"))) {
            fragmentTitleResName.append("umeng_comm_realtime");
            showRealTimeFeed();
        } else if (item.equals(ResFinder.getString("umeng_comm_recommend_user"))) {
            fragmentTitleResName.append("umeng_comm_recommend_user");
            showRecommendUserFragment();
        } else if (item.equals(ResFinder.getString("umeng_comm_recommend_topic"))) {
            fragmentTitleResName.append("umeng_comm_recommend_topic");
            showRecommendTopic();
        }else if (item.equals(ResFinder.getString("umeng_comm_setting"))) {
           gotoSettingActivity();
        }

        mFindActivityHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(fragmentTitleResName.toString())) {
                    String title = ResFinder.getString(fragmentTitleResName.toString());
                  //  mTitleTextView.setText(title);
//                    if (item.equals(ResFinder.getString("umeng_comm_setting"))){
//                        mSettingBtn.setText(ResFinder.getString("umeng_comm_save"));
//                        mSettingBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//
//                    }else {
//                        mSettingBtn.setVisibility(View.GONE);
//                    }
                }
            }
        });
    }
    @Deprecated
    protected void showCommFragment(Fragment fragment) {
       int container = ResFinder.getId("container");
        showFragmentInContainer(ResFinder.getId("container"), fragment);
    }
    public void showFragmentInContainer(int container, Fragment fragmentShow) {


        if (mCurrentFragment != fragmentShow) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                // 首先隐藏原来显示的Fragment
                transaction.hide(mCurrentFragment);
            }
            // 然后再显示传递进来的Fragment
            if (mFragmentManager.findFragmentByTag(fragmentShow.getClass().getName()) == null) {
                transaction
                        .add(container, fragmentShow, fragmentShow.getClass().getName());
            } else {
                transaction.show(fragmentShow);
            }
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragmentShow;
        }
    }
}
