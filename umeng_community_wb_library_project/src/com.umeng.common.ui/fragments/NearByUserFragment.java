package com.umeng.common.ui.fragments;
import android.os.Bundle;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.constants.Constants;
import com.umeng.common.ui.adapters.ActiveUserAdapter;
import com.umeng.common.ui.adapters.FollowedUserAdapter;
import com.umeng.common.ui.adapters.NearByUserAdapter;
import com.umeng.common.ui.presenter.impl.NearbyUserPresenter;


/**
 * Created by pei on 16/3/22.
 */
public class NearByUserFragment extends FollowedUserFragment {
    @Override
    protected NearbyUserPresenter createPresenters() {
        String uid = getArguments().getString(Constants.USER_ID_KEY);
        return new NearbyUserPresenter(this, uid);
    }

    public static NearByUserFragment newNearbyUserFragment( ) {
        NearByUserFragment fragment = new NearByUserFragment();
        Bundle bundle = new Bundle();
        String loginedUId = CommConfig.getConfig().loginedUser.id;
        bundle.putString(Constants.USER_ID_KEY, loginedUId);
        fragment.mUserId = loginedUId;
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public ActiveUserAdapter createAdapter() {
        NearByUserAdapter adapter = new NearByUserAdapter(getActivity());

        return adapter;
    }





}
