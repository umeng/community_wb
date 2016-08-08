package com.umeng.common.ui.adapters;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.common.ui.adapters.viewholders.ViewParser;

/**
 * Created by wangfei on 16/7/20.
 */
public abstract class FeedBaseAdapter<H extends ViewParser>  extends CommonAdapter<FeedItem, H>{
    protected Listeners.OnItemViewClickListener<FeedItem> mClickListener;
    /**
     * 是否显示距离
     */
    protected boolean mShowDistance = false;
    protected boolean isShowMedal = true;
    protected boolean isDisplayTopic = true;
    protected ArrayMap<String, FeedItem> mArrayMap;
    public FeedBaseAdapter(Context context) {

        super(context);
    }
    public void setShowDistance() {
        mShowDistance = true;
    }
    public void setShowMedals(boolean isShowMedal) {
        this.isShowMedal = isShowMedal;
    }
    @Override
    protected H createViewHolder() {
        return null;
    }
    public void setCommentClickListener(Listeners.OnItemViewClickListener<FeedItem> clickListener) {
        mClickListener = clickListener;
    }
    public FeedItem getItem(int position) {
        return mDataSet.get(position);
    }
    /**
     * 该回调用于更新UI。用于点赞 or评论后更新
     */
    public Listeners.OnResultListener mListener = new Listeners.OnResultListener() {

        @Override
        public void onResult(int status) {
            notifyDataSetChanged();
        }
    };
    public void setIsDisplayTopic(boolean isDisplayTopic){
        this.isDisplayTopic = isDisplayTopic;
    }
}
