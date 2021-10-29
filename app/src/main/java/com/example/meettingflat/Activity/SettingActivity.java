package com.example.meettingflat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DeviceUtils;
import com.example.meettingflat.Utils.Instruct;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.GetLinkBean;
import com.example.meettingflat.bean.MainMsgBean;
import com.example.meettingflat.bean.SetMsgBean;
import com.example.meettingflat.bean.UserBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ch.ielse.view.SwitchView;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout back;
    private SwitchView link;
    private MAPI mapi;
    private String id;
    private GetLinkBean bean;
    private String address;
    private SwitchView normallyOpen;
    private int normallyOPenFlag;
    private RelativeLayout door_select;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_set);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        EventBus.getDefault().register(this);
        address = getIntent().getStringExtra("meetAddress");
        normallyOPenFlag = getIntent().getIntExtra("normallyOPenFlag",0);
        mapi = new MAPI();
        id = DeviceUtils.getSerialNumber(SettingActivity.this);
        if(normallyOPenFlag==1){
            normallyOpen.setOpened(true);
        }else{
            normallyOpen.setOpened(false);
        }
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


        normallyOpen.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {

            }

            @Override
            public void toggleToOff(SwitchView view) {

            }
        });
        normallyOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMsgBean mainMsgBean = new MainMsgBean();
                if (normallyOpen.isOpened()) {
                    mainMsgBean.setMsg(Instruct.CANCELNORMALLYOPEN);
                } else {
                    mainMsgBean.setMsg(Instruct.NORMALLYOPEN);
                }
                EventBus.getDefault().post(mainMsgBean);
            }
        });
        door_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DoorSelectActivity.class));
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
        door_select = findViewById(R.id.door_select);
        normallyOpen = findViewById(R.id.normally_open);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMsgBean msgBean) {
        switch (msgBean.getMsg()){
            case Instruct.NORMALLYOPEN://常开
            case Instruct.NORMALLYOPEN1:
                normallyOpen.setOpened(true);
                break;
            case Instruct.CANCELNORMALLYOPEN://取消常开
            case Instruct.CANCELNORMALLYOPEN1:
                normallyOpen.setOpened(false);
                break;
        }
    }
}
