package com.umeng.common.ui.configure;

import java.util.ArrayList;

/**
 * Created by wangfei on 16/7/25.
 */
public class MainItem {
    public String title;
    public String style;
    public String topicid;
    public boolean isSearch;
    public boolean isTitleBar;
    public boolean isPostBtn;
    public boolean isShowTop;
    public ArrayList<MainTopicItem> topicItems = new ArrayList<MainTopicItem>();
    public ArrayList<Integer> hotIems = new ArrayList<Integer>();

}
