package com.example.meettingflat.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DeviceUtils;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.GetLinkBean;
import com.example.meettingflat.bean.UserBean;

import ch.ielse.view.SwitchView;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout back;
    private SwitchView link;
    private MAPI mapi;
    private String id;
    private GetLinkBean bean;
    private String address;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        address = getIntent().getStringExtra("meetAddress");
        mapi = new MAPI();
        id = DeviceUtils.getSerialNumber(SettingActivity.this);
        mapi.getLink(this, id, new MAPI.CallLink() {
            @Override
            public void call(GetLinkBean bean) {
                SettingActivity.this.bean = bean;
                if (TextUtils.isEmpty(bean.getData().getLinkage())) {
                    link.setOpened(false);
                } else {
                    link.setOpened(true);
                }
            }
        });
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        link.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {

            }

            @Override
            public void toggleToOff(SwitchView view) {

            }
        });
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (link.isOpened()) {
                    request(false);
                } else if (bean != null || !TextUtils.isEmpty(address)) {
                    request(true);
                }
            }
        });
    }


    public void request(boolean b) {
        mapi.setLink(SettingActivity.this, id, b, address, new MAPI.Call() {
            @Override
            public void call() {
                if(b){
                    link.setOpened(true);
                }else{
                    link.setOpened(false);
                }
            }
        });
    }

    private void initView() {
        back = findViewById(R.id.back);
        link = findViewById(R.id.link);
    }
}
