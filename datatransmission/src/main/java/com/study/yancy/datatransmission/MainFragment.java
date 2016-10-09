package com.study.yancy.datatransmission;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Jun&Hui on 2016/10/9.
 */
public class MainFragment extends Fragment {

    private Button bt_main;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = View.inflate(getActivity(), R.layout.main_fragemnt, null);
        bt_main = (Button) mainView.findViewById(R.id.bt_main);

        EventBus.getDefault().register(this);
        return mainView;
    }

    @Subscribe
    public void onEvent(String data) {
        bt_main.setText(data);
    }
    public void setData(String string) {
        bt_main.setText(string);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
