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
import com.example.meettingflat.Utils.SPUtil;

public class MeetSelectActivity extends AppCompatActivity {
    private SPUtil instance;
    private int MEETSELECTACTIVITYCODE=100;
    private Button complete;
    private TextView meetName;
    private RelativeLayout back;

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
                    Toast.makeText(MeetSelectActivity.this,"会议门名称不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                instance.setSettingParam("meetAddress",s);
                Intent intent = getIntent();
                intent.putExtra("meetAddress",s);
                setResult(MEETSELECTACTIVITYCODE,intent);
                finish();
            }
        });

    }

    private void initData() {
        instance = SPUtil.getInstance(this);
        String name = instance.getSettingParam("meetAddress", null);
        if(!TextUtils.isEmpty(name)){
            meetName.setText(name);
        }
    }


}
