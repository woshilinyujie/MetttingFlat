package com.example.meettingflat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DeviceUtils;
import com.example.meettingflat.Utils.SPUtil;
import com.example.meettingflat.Utils.WaitDialogTime;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.UserBean;

public class MeetSelectActivity extends AppCompatActivity {
    private SPUtil instance;
    private int MEETSELECTACTIVITYCODE=100;
    private Button complete;
    private TextView meetName;
    private RelativeLayout back;
    private MAPI mapi;
    private String id;
    private String name;
    private WaitDialogTime waitDialogTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_meet);
        iniView();
        initListener();
        initData();
    }

    private void iniView() {
        complete = findViewById(R.id.complete);
        meetName = findViewById(R.id.meet_name);
        back = findViewById(R.id.back);
    }
    private void initData() {
        mapi = new MAPI();
        id = DeviceUtils.getSerialNumber(MeetSelectActivity.this);
        instance = SPUtil.getInstance(this);
        name = instance.getSettingParam("meetAddress", null);
        if(!TextUtils.isEmpty(name)){
            meetName.setText(name);
        }
    }
    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = meetName.getText().toString();
                if(TextUtils.isEmpty(s)){
                    Toast.makeText(MeetSelectActivity.this,"会议室名称不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(s.length()>8){
                    Toast.makeText(MeetSelectActivity.this,"会议室名称不能超出8个字",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(waitDialogTime==null)
                waitDialogTime = new WaitDialogTime(MeetSelectActivity.this, R.style.m_dialog);
                waitDialogTime.show();
                //先删除后绑定
                mapi.up(MeetSelectActivity.this, id, name, false, new MAPI.Call() {
                    @Override
                    public void call() {
                        mapi.up(MeetSelectActivity.this, id, s, true, new MAPI.Call() {
                            @Override
                            public void call() {
                                waitDialogTime.dismiss();
                                instance.setSettingParam("meetAddress",s);
                                Intent intent = getIntent();
                                intent.putExtra("meetAddress",s);
                                setResult(MEETSELECTACTIVITYCODE,intent);
                                finish();
                            }
                        });
                    }
                });
            }
        });

    }



}
