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

package com.umeng.common.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.dialogs.ClipImageDialog;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.fragments.SettingFragment;
import com.umeng.common.ui.mvpview.MvpUserProfileSettingView;
import com.umeng.common.ui.presenter.impl.UserSettingPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng.common.ui.widgets.SwitchButton;


/**
 * 设置页面 注意：此Activity的名字不能修改，数据层需要回调此Activity
 */
public class SettingActivity extends BaseFragmentActivity   {
    protected TextView mTitleTextView;
   private SettingFragment settingFragment;
    protected Button mSaveButton;
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(ResFinder.getLayout("umeng_comm_setting_activity"));
        initViews();
        settingFragment = new SettingFragment();
        settingFragment.setLogoutBtnClick(new SettingFragment.LogoutBtnClick() {
            @Override
            public void onClick() {
                finish();
            }
        });
        int container = ResFinder.getId("umeng_comm_setting_content");
        setFragmentContainerId(container);
        showFragmentInContainer(container,settingFragment);
    }

    /**
     * 初始化相关View</br>
     */
    private void initViews() {
        findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleTextView = (TextView) findViewById(ResFinder.getId("umeng_comm_setting_title"));
        mTitleTextView.setText(ResFinder.getString("umeng_comm_setting"));
        mSaveButton = (Button) findViewById(ResFinder.getId("umeng_comm_save_bt"));
        mSaveButton.setVisibility(View.VISIBLE);
        mSaveButton.setText(ResFinder.getString("umeng_comm_save"));
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                settingFragment.dealSaveLogic();
            }
        });
    }

}
