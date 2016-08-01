
package com.umeng.community.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;
import com.umeng.comm.core.nets.Request;
import com.umeng.comm.core.nets.responses.AlbumResponse;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.responses.UsersResponse;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.core.share.Shareable;
import com.umeng.comm.ui.fragments.CommunityMainFragment;
import com.umeng.common.ui.util.CommonLoginStrategy;
import com.umeng.community.example.custom.SimpleLoginImpl;
import com.umeng.community.example.custom.UILImageLoader;

import com.umeng.message.PushAgent;


/**
 * 该类是微社区Demo的主界面Activity类，演示了以Fragment的形式集成友盟微社区。友盟微社区的主页Fragment为
 * {@link CommunityMainFragment},该类继承自v4包中的Fragment。 </p> 该类中还演示了注入自定义登录系统
 * {@link #useCustomLogin()}、配置友盟Social实现的登录系统 {@link #useSocialLogin()}
 * 以及一些常用接口的示例 {@link #someMethodsDemo()}
 */

/**
 *论坛版微博版只能使用一种ui，当import com.umeng.commm.ui.fragments.CommunityMainFragment时是论坛版式
 * 如果使用微博版式则改为import com.umeng.comm.ui.fragments.CommunityMainFragment;
 * 在AndroidManifest中选择对应appid和appsecret，同时如果选择微博版可以删除论坛版对应注册的activity，同理选择论坛版
 * 可以删除微博版对应注册的activity
 */

public class MainActivity extends FragmentActivity {

    CommunitySDK mCommSDK = null;
    String topicId = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ==================== 注意 ===================
        // 开发者如果想将友盟微社区作为ViewPager中的一个页面集成到应用中时，请将您的ViewPager替换为CommunityViewPager，
        // 避免滑动时间冲突导致问题
        // CommunityViewPager viewPager = (CommunityViewPager)
        // findViewById(R.id.viewPager);
        // // 设置ViewPager的Adapter
        // viewPager.setAdapter(new
        // FragmentTabAdapter(getSupportFragmentManager()));
        // ===============================================

        // 2、单纯Fragment使用方式
        /**如果使用android6.0适配，需要加入以下代码，获取对应权限*/
//        String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE};
//        if(Build.VERSION.SDK_INT>=23){
//            requestPermissions(mPermissionList,100);
//        }
        CommunityMainFragment fragment = new CommunityMainFragment();
        fragment.setBackButtonVisibility(View.GONE);
        // 3、将友盟微社区的首页Fragment添加到Activity中
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();


    }

//    /**
//     * 初始化分享相关的平台
//     *
//     * @param activity
//     */
//    private void initPlatforms(Activity activity) {
//        // 添加QQ
//        UMQQSsoHandler qqHandler = new UMQQSsoHandler(activity, "1104606393",
//                "X4BAsJAVKtkDQ1zQ");
//        qqHandler.addToSocialSDK();
//        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1104606393",
//                "X4BAsJAVKtkDQ1zQ");
//        qZoneSsoHandler.addToSocialSDK();
//        // 添加微信平台
//        UMWXHandler wechatHandler = new UMWXHandler(activity, "wx96110a1e3af63a39",
//                "c60e3d3ff109a5d17013df272df99199");
//        wechatHandler.addToSocialSDK();
//        // 添加微信朋友圈平台
//        UMWXHandler circleHandler = new UMWXHandler(activity, "wx96110a1e3af63a39",
//                "c60e3d3ff109a5d17013df272df99199");
//        circleHandler.setToCircle(true);
//        circleHandler.addToSocialSDK();
//
//        UMShareServiceFactory.getSocialService().getConfig()
//                .setPlatforms(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
//        UMShareServiceFactory.getSocialService().getConfig()
//                .setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
//    }
//
//    /**
//     * 自定义自己的登录系统
//     */
//    protected void useSocialLogin() {
//
//        // 用户自定义的登录
//        UMAuthService mLogin = UMLoginServiceFactory.getLoginService("umeng_login_impl");
//        String appId = "1104606393";
//        String appKey = "X4BAsJAVKtkDQ1zQ";
//        // SSO 设置
//        // mLogin.getConfig().setSsoHandler(new SinaSsoHandler());
//        new UMQQSsoHandler(this, appId, appKey).addToSocialSDK();
//
//        String wxappId = "wx96110a1e3af63a39";
//        String wxappSecret = "c60e3d3ff109a5d17013df272df99199";
//        new UMWXHandler(getApplicationContext(), wxappId,
//                wxappSecret).addToSocialSDK();
//
//        // 将登录实现注入到sdk中,key为umeng_login
//        LoginSDKManager.getInstance().addAndUse(mLogin);
//
//    }

    protected void useCustomLogin() {
        // 管理器
        LoginSDKManager.getInstance().addAndUse(new SimpleLoginImpl());
    }

    /**
     * 自定义自己的ImageLoader
     */
    protected void useMyImageLoader() {
        //
        final String imageLoadKey = UILImageLoader.class.getSimpleName();
        // 使用第三方ImageLoader库,添加到sdk manager中, 并且使用useThis来使用该加载器.
        ImageLoaderManager manager = ImageLoaderManager.getInstance();
        manager.addImpl(imageLoadKey, new UILImageLoader(this));
        manager.useThis(imageLoadKey);
    }

    /**
     * 一些常用的接口以及获取推荐的数据接口
     */
    void someMethodsDemo() {
        // 主动登录
        mCommSDK.login(getApplicationContext(), new LoginListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int stCode, CommUser userInfo) {

            }
        });

        // 获取登录SDK Manager
        LoginSDKManager manager = LoginSDKManager.getInstance();
        Loginable currentLoginable = manager.getCurrentSDK();
        // 是否登录
        //currentLoginable.isLogined(getApplicationContext());

        // 未登录下获取话题
        mCommSDK.fetchTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(TopicResponse response) {
                for (Topic item : response.result) {
                    Log.e("", "### topic id : " + item.id + ", name = " + item.name);
                    topicId = item.id;
                }

            }
        });

        // 未登录情况下获取某个话题下的feed
        mCommSDK.fetchTopicFeed(topicId, new
                FetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        Log.e("", "### 未登录下获取到某个topic下的feed : " + response.result.size());
                        for (FeedItem item : response.result) {
                            Log.e("", "### topic feed id : " + item.id + ", name = " +
                                    item.text);
                        }

                    }

                    @Override
                    public void onStart() {
                    }
                });

        // 推荐的feed
        mCommSDK.fetchRecommendedFeeds(new FetchListener<FeedsResponse>() {

            @Override
            public void onComplete(FeedsResponse response) {
                Log.e("", "### 推荐feed  code : " + response.errCode + ", msg = " + response.errMsg);
                for (FeedItem item : response.result) {
                    Log.e("", "### 推荐feed id : " + item.id + ", name = " + item.text);
                }
            }

            @Override
            public void onStart() {

            }
        });

        // 获取推荐的话题
        mCommSDK.fetchRecommendedTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onComplete(TopicResponse response) {
                Log.e("", "### 推荐的话题 : ");
                for (Topic item : response.result) {
                    Log.e("", "### 话题 : " + item.name);
                }
            }

            @Override
            public void onStart() {

            }
        });

        // 获取某个话题活跃的用户
        mCommSDK.fetchActiveUsers("541fe6f40bbbaf4f41f7aa3f", new FetchListener<UsersResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(UsersResponse response) {
                Log.e("", "### 某个话题的活跃用户 : ");
                for (CommUser user : response.result) {
                    Log.e("", "### 活跃用户 : " + user.name);
                }
            }
        });

        // 获取某用户的相册,也就是发布feed上传的所有图片
        mCommSDK.fetchAlbums(CommConfig.getConfig().loginedUser.id,
                new FetchListener<AlbumResponse>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(AlbumResponse response) {
                        Log.e("", "### response size : " + response.result.size());
                    }
                });

        // 搜索周边的feed
        mCommSDK.searchFeedNearby(116.3758540000f, 39.9856970000f,
                new FetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        Log.e("", "### 周边的feed : " + response.result.size());
                    }

                    @Override
                    public void onStart() {

                    }

                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareSDKManager.getInstance().getCurrentSDK().onActivityResult(this,requestCode,resultCode,data);
    }
}
