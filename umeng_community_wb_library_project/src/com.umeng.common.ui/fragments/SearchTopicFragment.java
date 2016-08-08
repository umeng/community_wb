package com.umeng.common.ui.fragments;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.activities.SearchTopicBaseActivity;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.listener.TopicToTopicDetail;
import com.umeng.common.ui.presenter.impl.TopicBasePresenter;
import com.umeng.common.ui.presenter.impl.SearchTopicPresenter;

/**
 * Created by wangfei on 16/7/21.
 */
public class SearchTopicFragment extends TopicFragment{
    protected EditText mSearchEditText;
    protected ProgressDialog mProgressDialog;
    protected View mBackButton;
    @Override
    protected void initWidgets() {
        mBackButton = mViewFinder.findViewById(ResFinder.getId("umeng_comm_back"));
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("加载中...");
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                getActivity().finish();
            }
        });


        mSearchEditText = mViewFinder.findViewById(ResFinder.getId("umeng_comm_topic_edittext"));
        findViewById(ResFinder.getId("umeng_comm_topic_search")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchTopicPresenter) mPresenter).executeSearch(mSearchEditText.getText()
                        .toString().trim());
            }
        });
        super.initWidgets();
        mSearchEditText.requestFocus();
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    ((SearchTopicPresenter) mPresenter).executeSearch(textView.getText()
                            .toString().trim());
                }
                return false;
            }
        });

        showInputMethod();
        setAdapterGotoDetail();
        isShowSearchBar(false);
    }
    private void hideSoftKeyboard() {
        if (getActivity()!=null&&!getActivity().isFinishing()) {
            ((SearchTopicBaseActivity) getActivity()).hideInputMethod(mSearchEditText);
        }
    }

    @Override
    protected TopicBasePresenter createPresenters() {
        return new SearchTopicPresenter(this);
    }
    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_search_topic");
    }
    public void executeSearch() {
        String keyword = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastMsg.showShortMsgByResName("umeng_comm_topic_search_no_keyword");
            return;
        }
        ((SearchTopicPresenter) mPresenter).executeSearch(mSearchEditText.getText()
                .toString().trim());
    }
    private void showInputMethod() {
        ((SearchTopicBaseActivity) getActivity()).showInputMethod(mSearchEditText);
    }

    protected void setAdapterGotoDetail() {
        ((RecommendTopicAdapter) mAdapter).setTtt(new TopicToTopicDetail() {
            @Override
            public void gotoTopicDetail(Topic topic) {
                command.navigateToTopicDetail(topic);
            }
        });
    }
}
