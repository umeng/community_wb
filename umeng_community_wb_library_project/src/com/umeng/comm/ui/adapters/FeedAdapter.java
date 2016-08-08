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

package com.umeng.comm.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.listeners.Listeners.OnItemViewClickListener;
import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.ui.adapters.viewholders.FeedItemViewHolder;
import com.umeng.common.ui.adapters.CommonAdapter;
import com.umeng.common.ui.adapters.FeedBaseAdapter;


/**
 * Feed消息流 适配器
 */
public class FeedAdapter extends FeedBaseAdapter< FeedItemViewHolder> {


    public FeedAdapter(Context context) {
        super(context);
    }

    @Override
    protected FeedItemViewHolder createViewHolder() {
        mArrayMap = new ArrayMap<String, FeedItem>();
        return new FeedItemViewHolder(mArrayMap);
    }

    @Override
    protected void setItemData(int position, FeedItemViewHolder holder, View rootView) {
        final FeedItem feedItem = getItem(position);
        if (mShowDistance) {
            holder.setShowDistance();
        }
        if (isShowMedal) {
            holder.setIsShowMedals(true);
        } else {
            holder.setIsShowMedals(false);
        }
        holder.setFeedItem(feedItem);
        // 评论的点击事件
        holder.setOnItemViewClickListener(position, mClickListener);
        holder.setOnUpdateListener(mListener);
        if (mContext instanceof Activity) {
            holder.setShareActivity((Activity) mContext);
        } else {
            Log.e("", "### FeedAdapter中的Context不是Activity类型,无法分享");
        }
    }

} // end of FeedAdapter

