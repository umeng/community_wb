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

package com.umeng.common.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.umeng.comm.core.push.NullPushImpl;
import com.umeng.comm.core.push.Pushable;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.comm.core.sdkmanager.PushSDKManager;
import com.umeng.comm.core.strategy.logout.InnerLogoutStrategy;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.activities.BaseFragmentActivity;
import com.umeng.common.ui.activities.SearchTopicBaseActivity;
import com.umeng.common.ui.dialogs.ClipImageDialog;
import com.umeng.common.ui.dialogs.ConfirmDialog;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.mvpview.MvpUserProfileSettingView;
import com.umeng.common.ui.presenter.impl.NullPresenter;
import com.umeng.common.ui.presenter.impl.UserSettingPresenter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng.common.ui.widgets.SwitchButton;


/**
 * 设置页面,是各个子类型设置页面的入口
 */
public class SettingFragment extends BaseFragment<CommUser, UserSettingPresenter> implements OnClickListener,MvpUserProfileSettingView {

    protected Dialog mProgressDialog;




    protected ClipImageDialog mClipImageDialog;

    protected CommUser user;
    protected EditText nikcnameEdit;
    protected TextView genderEdit;
    protected SwitchButton switchButton;
    protected Button logoutButton;
    protected RoundImageView headicon;
    protected CommUser.Gender mGender;
    protected Dialog mDialog;
    protected CommConfig mSDKConfig = CommConfig.getConfig();
    private LogoutBtnClick logoutBtnClick;
    protected boolean isFirst = false;
    protected boolean isRegisterUserNameInvalid = false;

    private int loginStyle  = 0;
    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_setting");
    }

    public void setLogoutBtnClick(LogoutBtnClick logoutBtnClick) {
        this.logoutBtnClick = logoutBtnClick;
    }

    @Override
    protected void initWidgets() {
        mProgressDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_update_user_info"));
        judgeIsFirst();
        nikcnameEdit = (EditText)findViewById(ResFinder.getId("setting_username"));
        genderEdit = (TextView)findViewById(ResFinder.getId("setting_gender"));
        switchButton = (SwitchButton)findViewById(ResFinder.getId("umeng_common_switch_button"));
        logoutButton = (Button)findViewById(ResFinder.getId("setting_loginout"));
        nikcnameEdit.setHint(user.name);
        nikcnameEdit.setText(user.name);
        headicon = (RoundImageView)findViewById(ResFinder.getId("user_head_icon"));
        ImgDisplayOption option = ImgDisplayOption.getOptionByGender(user.gender);
        headicon.setImageUrl(user.iconUrl,option);
        headicon.setOnClickListener(this);
        genderEdit.setHint(user.gender.toString().equals("male") ? ResFinder.getString("umeng_comm_male") :ResFinder.getString("umeng_comm_female"));
        genderEdit.setOnClickListener(this);
        switchButton.setChecked(mSDKConfig.isPushEnable(getActivity()));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 保存配置
                CommConfig.getConfig().setSDKPushable(getActivity(), isChecked);
            }
        });

        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunitySDKImpl.getInstance().logout(getActivity(), new LoginListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int stCode, CommUser userInfo) {
                        BroadcastUtils.sendUserLogoutBroadcast(getActivity().getApplication());
                       logoutBtnClick.onClick();
                    }
                });
            }
        });
        if(isFirst){
            logoutButton.setVisibility(View.GONE);
        }

        mGender = user.gender;
    }

    @Override
    protected UserSettingPresenter createPresenters() {
        return new UserSettingPresenter(getActivity(),this);
    }
    public void hideInputMethod() {
        if (getActivity()!=null&&!getActivity().isFinishing()) {
            ((BaseFragmentActivity) getActivity()).hideInputMethod(nikcnameEdit);
        }
    }
    public void dealSaveLogic() {
        registerOrUpdateUserInfo();

    }
    public void registerOrUpdateUserInfo() {
        boolean flag = checkData();
        if (!flag) {
            return;
        }

        mProgressDialog.setCanceledOnTouchOutside(false);
        if (isRegisterUserNameInvalid) {
            register();
        } else {
            updateUserInfo();
        }


    }
    private boolean checkData() {
        String name = nikcnameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)&&isFirst&&!isRegisterUserNameInvalid){
            name = user.name;
        }
        if (TextUtils.isEmpty(name)) {
            ToastMsg.showShortMsgByResName("umeng_comm_user_center_no_name");
            return false;
        }
        return true;

    }
    private void updateUserInfo() {

        final CommUser newUser = CommConfig.getConfig().loginedUser;
        if (TextUtils.isEmpty(nikcnameEdit.getText().toString())){
            newUser.name = nikcnameEdit.getHint().toString();
        }else {
            newUser.name = nikcnameEdit.getText().toString();
        }


        newUser.gender = mGender;

        mPresenter.updateUserProfile(newUser);

    }
    private void register() {
        user.name = nikcnameEdit.getText().toString().trim();
        user.gender = mGender;
        if(loginStyle == 1){

            mPresenter.registertowsq(user);
        }else {
            mPresenter.register(user);
        }
    }
    private void dealBackLogic() {
        getActivity().finish();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_setting_back")) { // 返回事件
            hideInputMethod();
            dealBackLogic();
        } else if (id == ResFinder.getId("umeng_comm_save_bt")) { // 保存事件
            dealSaveLogic();
        } else if (id == ResFinder.getId("user_head_icon")){
            if (isRegisterUserNameInvalid) {
                ToastMsg.showShortMsgByResName("umeng_comm_before_save");
            } else {
                selectProfile();
            }
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if (id == ResFinder.getId("setting_gender")){
            showGenderDialog();
        }else if(id == ResFinder.getId("umeng_comm_gender_textview_male")){
            String maleStr = ResFinder.getString("umeng_comm_male");
            genderEdit.setText(maleStr);
            changeDefaultIcon(CommUser.Gender.MALE);
            closeDialog();
        }else if(id == ResFinder.getId("umeng_comm_gender_textview_femal")){
            String femalStr = ResFinder.getString("umeng_comm_female");
            genderEdit.setText(femalStr);
            closeDialog();
            changeDefaultIcon(CommUser.Gender.FEMALE);

        }
    }
    private void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
    private void showGenderDialog() {
        int style = ResFinder.getStyle("customDialog");
        int layout = ResFinder.getLayout("umeng_comm_gender_select");
        int femalResId = ResFinder.getId("umeng_comm_gender_textview_femal");
        int maleResId = ResFinder.getId("umeng_comm_gender_textview_male");
        mDialog = new Dialog(getActivity(), style);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(layout,
                null, false);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        view.findViewById(femalResId).setOnClickListener(this);
        view.findViewById(maleResId).setOnClickListener(this);
        mDialog.show();
    }
    private void changeDefaultIcon(CommUser.Gender gender) {
        mGender = gender;

    }
    private void selectProfile() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/png;image/jpeg");
        startActivityForResult(pickImageIntent, Constants.PIC_SELECT);
    }
    @Override
    public void showLoading(boolean isShow) {
        if (isShow) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }
    public void judgeIsFirst(){
        Bundle mExtra =  getActivity().getIntent().getExtras();
        if (mExtra != null && mExtra.containsKey(Constants.USER_SETTING)) {
            isFirst = true;
            user = mExtra.getParcelable(Constants.USER);
            isRegisterUserNameInvalid = mExtra.getBoolean(Constants.REGISTER_USERNAME_INVALID);
            loginStyle = mExtra.getInt(Constants.LOGIN_STYLE);
            mPresenter.setFirstSetting(true);
        } else {
            isFirst = false;
            user = CommonUtils.getLoginUser(getActivity());
        }
    }
    private ClipImageDialog.OnClickSaveListener mOnSaveListener = new ClipImageDialog.OnClickSaveListener() {

        @Override
        public void onClickSave(Bitmap bitmap) {
            headicon.setImageBitmap(bitmap);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 防止在选择图片的时候按返回键
        if (data == null) {
            return;
        }
        // 从相册中选择图片
        if (requestCode == Constants.PIC_SELECT) {
            int style = ResFinder.getStyle("umeng_comm_dialog_fullscreen");
            // 显示剪切图片的Dialog
            mClipImageDialog = new ClipImageDialog(getActivity(), data.getData(), style);
            mClipImageDialog.setOnClickSaveListener(mOnSaveListener);
            mClipImageDialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        hideInputMethod();
        super.onDestroy();
    }
    public interface LogoutBtnClick{
        public void onClick();
    }
}
