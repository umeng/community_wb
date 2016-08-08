package com.umeng.common.ui.configure;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.umeng.comm.core.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by wangfei on 16/6/26.
 */
public  class parseJson {
    public static boolean isfind = true;
    public static int titlewidth = 60;
    public static ArrayList<MainItem> mainItems = new ArrayList<MainItem>();
    public static ArrayList<FindItem> findItems = new ArrayList<FindItem>();
    public static ArrayList<TopicItem> topicItems = new ArrayList<TopicItem>();
    public static void getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        initData(stringBuilder.toString());
       //  stringBuilder.toString();
    }
    public static void initData(String result){
        if (TextUtils.isEmpty(result)){
            return;
        }
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isfind = json.optBoolean("findbutton");
        titlewidth = json.optInt("titlewidth");
        //解析主页
        JSONArray main = json.optJSONArray("main");

        mainItems.clear();
        for (int i = 0;i<main.length();i++){
            try {
                MainItem mainItem = new MainItem();
                JSONObject j = main.optJSONObject(i);
                mainItem.title = j.optString("title");
                mainItem.style = j.optString("style");
                mainItem.isPostBtn = j.optBoolean("postbutton");
                mainItem.isSearch = j.optBoolean("searchbar");
                mainItem.isTitleBar = j.optBoolean("titlebar");
                mainItem.isShowTop = j.optBoolean("showtop");
                mainItem.topicid = j.optString("topicid");
                JSONArray hotarry = j.optJSONArray("hotkind");
                if (hotarry!=null){
                    mainItem.hotIems.clear();
                    for (int k = 0 ; k <hotarry.length();k++){
                        mainItem.hotIems.add(hotarry.optInt(k));
                    }
                }
                if (j.getString("style").equals("topic")){
                    mainItem.topicItems.clear();
                    JSONArray jsonArray = j.getJSONArray("children");
                    for (int m = 0;m<jsonArray.length();m++){
                        MainTopicItem mainTopicItem= new MainTopicItem();
                        JSONObject jj = jsonArray.getJSONObject(m);
                        mainTopicItem.title = jj.optString("title");
                        mainTopicItem.style = jj.optString("style");
                        mainTopicItem.isSearch = jj.optBoolean("searchbar");
                        mainItem.topicItems.add(mainTopicItem);
                    }
                }
                mainItems.add(mainItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //解析发现
        JSONArray find = json.optJSONArray("find");
        findItems.clear();
        for (int i = 0;i<find.length();i++){
            try {
              JSONObject j = find.getJSONObject(i);
                FindItem findItem = new FindItem();
                findItem.title = j.optString("title");
                findItem.list.clear();
                JSONArray jj = j.getJSONArray("list");
                for (int n = 0 ;n<jj.length();n++){
                    FindCell cell = new FindCell();
                    JSONObject jjjson = jj.optJSONObject(n);
                    cell.name = jjjson.optString("name");
                    cell.style = jjjson.optString("style");
                    cell.drawable = jjjson.optString("drawable");
                    findItem.list.add(cell);
                }
                findItems.add(findItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //解析话题
        JSONArray topic = json.optJSONArray("topic");
        topicItems.clear();
        for (int i = 0;i<topic.length();i++){
            TopicItem topicItem = new TopicItem();
            JSONObject j = topic.optJSONObject(i);
            topicItem.title = j.optString("title");
            topicItem.style = j.optString("style");
            topicItem.isPostBtn = j.optBoolean("postbutton");
            topicItem.isSearch = j.optBoolean("searchbar");
            topicItem.isTitleBar = j.optBoolean("titlebar");
            JSONArray hotarry = j.optJSONArray("hotkind");
            if (hotarry!=null){
                topicItem.hotIems.clear();
                for (int k = 0 ; k <hotarry.length();k++){
                    topicItem.hotIems.add(hotarry.optInt(k));
                }
            }
            topicItems.add(topicItem);
        }
    }

}
