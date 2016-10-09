package com.study.yancy.datatransmission;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MenuFragment menuFragment = new MenuFragment();
        final MainFragment mainFragment  = new MainFragment();

        //将上面的两个Fragment添加进来
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_menu, menuFragment, "menuFragment").commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, mainFragment, "mainFragment").commit();

        menuFragment.setOnDataTransmissionListener(new MenuFragment.OnDataTransmissionListener() {

            @Override
            public void dataTransmission(String data) {
                mainFragment.setData(data);
            }
        });
    }
}
