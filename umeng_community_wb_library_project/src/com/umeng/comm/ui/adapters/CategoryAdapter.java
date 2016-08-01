package com.umeng.comm.ui.adapters;

import android.content.Context;
import android.view.View;

import com.umeng.comm.core.beans.Category;
import com.umeng.comm.ui.adapters.viewholders.CategoryViewHolder;
import com.umeng.common.ui.adapters.CommonAdapter;


/**
 * Created by wangfei on 15/11/24.
 */
public class CategoryAdapter extends CommonAdapter<Category,CategoryViewHolder> {
    public CategoryAdapter(Context context) {
        super(context);

    }
    @Override
    protected CategoryViewHolder createViewHolder() {
        return new CategoryViewHolder();
    }

    @Override
    protected void setItemData(int position, CategoryViewHolder viewHolder, View rootView) {
        final Category category = getItem(position);
        viewHolder.setFeedItem(category);
    }
}
