package com.umeng.comm.ui.fragments;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.common.ui.configure.parseJson;
import com.umeng.common.ui.fragments.TopicBaseFragment;
import com.umeng.common.ui.fragments.TopicMainBaseFragment;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;

import java.util.ArrayList;


/**
 * Created by wangfei on 15/11/26.
 */
public class TopicMainFragment extends TopicMainBaseFragment {


    /**
     * 布局加载LayoutInflater
     */
    protected LayoutInflater mLayoutInflater;
    /**
     * 根视图
     */




    private boolean mIsInit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonUtils.saveComponentImpl(getActivity());// 注意此处必须保存登录组件的信息
        mLayoutInflater = inflater;
        mRootView = mLayoutInflater.inflate(ResFinder.getResourceId(ResFinder.ResType.LAYOUT,"umeng_comm_main_topic"), container, false);
        initFragment();
        init();
        initswitchListener();
        initSwitchView();
        return mRootView;
    }

    protected void init() {
        if (mIsInit) {
            return;
        }
        mIsInit = true;
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(ResFinder.
                    getResourceId(ResFinder.ResType.ID, "id_content"),topicBaseFragments.get(0)).commit();
        }
    }




    @Override
    public void onResume() {
        super.onResume();
        init();
//        if (transaction == null){
//            transaction = getFragmentManager().beginTransaction();
//        }
//
//                transaction.replace(ResFinder.getResourceId(ResFinder.ResType.ID,"id_content"),categoryFragment).commit();



    }
    public void ChangeFragment(int num){
        switch (num){
            case 0:
                getFragmentManager().beginTransaction().replace(ResFinder.getResourceId(ResFinder.ResType.ID, "id_content"), topicBaseFragments.get(0)).commit();
                break;
            case 1:
                getFragmentManager().beginTransaction().replace(ResFinder.getResourceId(ResFinder.ResType.ID, "id_content"), topicBaseFragments.get(1)).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(ResFinder.getResourceId(ResFinder.ResType.ID,"id_content"),topicBaseFragments.get(2)).commit();
                break;
        }
//        getFragmentManager().beginTransaction().commit();
    }

    @Override
    public void initFragment() {
        if (parseJson.ttc.size() == 0){
            topicBaseFragments.add(new TopicFragment());
        }else {
            for (String temp:parseJson.ttc){
                topicBaseFragments.add(getTopicFragment(temp)) ;
            }
        }

    }
    public Fragment getTopicFragment(String name){
        if (name.equals("focus")){
            return new FocusedTopicFragment();
        }else if (name.equals("alltopic")){
          return new TopicFragment();
        }else if (name.equals("allcategory")){
            return new CategoryFragment();
        }else if (name.equals("recommend")){
            RecommendTopicFragment RecommentFragment =  new RecommendTopicFragment();
            RecommentFragment.setShowActionbar(false);
            return RecommentFragment;
        }
        return new TopicFragment();
    }
}
