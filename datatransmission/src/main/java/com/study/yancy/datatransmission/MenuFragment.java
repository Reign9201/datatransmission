package com.study.yancy.datatransmission;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧的菜单Fragment
 * Created by Jun&Hui on 2016/10/9.
 */
public class MenuFragment extends Fragment {
    List<String> mDatas = new ArrayList<>();
    private OnDataTransmissionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View menuView = View.inflate(getActivity(), R.layout.menu_fragemnt, null);


        for(int i = 1; i <= 5; i++) {
            mDatas.add("这是第"+ i + "条数据");
        }

        final ListView lv = (ListView) menuView.findViewById(R.id.lv_menu);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mDatas.size();
            }

            @Override
            public Object getItem(int position) {
                return mDatas.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_fragemnt_item, parent, false);
                TextView tv = (TextView) contentView.findViewById(R.id.tv_menu_item);
                tv.setText(mDatas.get(position));

                /**
                 * 需求：点击对应的条目，将条目的内容发送到MainFragment中的Button上，更改Button名称（即进行一个数据传递）
                 */

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /**
                         * 方法三：使用第三方开源框架EventBus
                         */
                        EventBus.getDefault().post(mDatas.get(position));
                    }
                });

                return contentView;
            }
        });

        return menuView;
    }

    //接口回调的方法
    public interface OnDataTransmissionListener {
        public void dataTransmission(String data);
    }

    public void setOnDataTransmissionListener(OnDataTransmissionListener mListener) {
        this.mListener = mListener;
    }
}
