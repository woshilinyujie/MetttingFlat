package com.example.meettingflat.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.Instruct;
import com.example.meettingflat.Utils.SPUtil;
import com.example.meettingflat.bean.MainMsgBean;

import org.greenrobot.eventbus.EventBus;

public class DoorSelectActivity extends AppCompatActivity {
    RelativeLayout back;
    Button save;
    ImageView onCheck;
    ImageView twoCheck;

    private int select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_select_layout);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        back=findViewById(R.id.back);
        save=findViewById(R.id.save);
        onCheck=findViewById(R.id.one_check);
        twoCheck=findViewById(R.id.two_check);
    }


    private void initData() {
        select = SPUtil.getInstance(this).getSettingParam("doorSelect", 0);
        switch (select){
            case 0:
                onCheck.setBackgroundResource(R.drawable.device_select_icon);
                break;
            case 1:
                twoCheck.setBackgroundResource(R.drawable.device_select_icon);
                break;
        }
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMsgBean mainMsgBean = new MainMsgBean();
                mainMsgBean.setFlag(select);
                mainMsgBean.setMsg(Instruct.SELECTDOOR);
                EventBus.getDefault().post(mainMsgBean);
                finish();
            }
        });
        onCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheck.setBackgroundResource(R.drawable.device_select_icon);
                twoCheck.setBackgroundResource(R.drawable.device_unselect_icon);
                select=0;
            }
        });
        twoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheck.setBackgroundResource(R.drawable.device_unselect_icon);
                twoCheck.setBackgroundResource(R.drawable.device_select_icon);
                select=1;
            }
        });
    }
}
