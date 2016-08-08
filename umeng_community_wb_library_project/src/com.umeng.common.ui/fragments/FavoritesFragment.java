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

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.utils.ResFinder;

import com.umeng.common.ui.adapters.FeedBaseAdapter;
import com.umeng.common.ui.presenter.impl.FavoritesFeedPresenter;
import com.umeng.common.ui.presenter.impl.FeedListPresenter;
import com.umeng.common.ui.widgets.BaseView;

import java.util.Iterator;

/**
 * 收藏页面
 */
public class FavoritesFragment extends FeedListFragment<FeedListPresenter> {
    protected BaseView mBaseView = null;

    public FavoritesFragment() {
        isShowPostButton(false);
        isSetPaddingToListView(true);
    }

    @Override
    protected FeedListPresenter createPresenters() {
        FavoritesFeedPresenter favoritesFeedPresenter = new FavoritesFeedPresenter(this);
        favoritesFeedPresenter.setIsShowTopFeeds(isShowTopFeed);
        return new FavoritesFeedPresenter(this);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        // 将标题改为收藏列表
//        TextView titleTextView = (TextView) mRootView.findViewById(ResFinder
//                .getId("umeng_comm_title_tv"));
//        titleTextView.setText(ResFinder.getString("umeng_comm_favoriets_list"));
//        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mBaseView = (BaseView) mRootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_feed"));

    }

    public static FavoritesFragment newFavoritesFragment() {
        return new FavoritesFragment();
    }

    @Override
    protected FeedBaseAdapter createListViewAdapter() {
        return command.createFavouriteAdapter(getActivity());
    }
}
