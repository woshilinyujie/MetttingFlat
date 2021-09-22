package com.example.meettingflat.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DateUtils;
import com.example.meettingflat.bean.MeetingBean;

import java.util.Date;

public class MeetDetailActivity extends AppCompatActivity {

    private ImageView back;
    private TextView end_time;
    private TextView start_time;
    private TextView state;
    private TextView initiator;
    private TextView num;
    private TextView personnel;
    private TextView name;
    private TextView all_time;
    private DateUtils dateUtils;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_meet_detail);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        back = findViewById(R.id.meet_detail_back);
        end_time = findViewById(R.id.meet_detail_end_time);
        start_time = findViewById(R.id.meet_detail_start_time);
        state = findViewById(R.id.meet_detail_state);
        initiator = findViewById(R.id.meet_detail_initiator);
        num = findViewById(R.id.meet_detail_num);
        personnel = findViewById(R.id.meet_detail_personnel);
        name = findViewById(R.id.meet_detail_name);
        all_time = findViewById(R.id.meet_detail_all_time);
    }


    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initData() {
        dateUtils = DateUtils.getInstance();
        MeetingBean.EventsBean bean = (MeetingBean.EventsBean) getIntent().getSerializableExtra("bean");
        Date date = dateUtils.transitionTime(bean.getStart().getDateTime());
        Date date1 = dateUtils.transitionTime(bean.getEnd().getDateTime());
        start_time.setText("开始时间： "+dateUtils.dateFormat20(date.getTime()));
        end_time.setText("结束时间： "+dateUtils.dateFormat20(date1.getTime()));
        getMeetState(bean, state);
        name.setText(bean.getSummary());
        all_time.setText("会议时常： "+dateUtils.formatDuring(date1.getTime()-date.getTime()));
        initiator.setText("发起人： "+bean.getOrganizer().getDisplayName());
        int size = bean.getAttendees().size();
        num.setText("参会人员： "+size+"人");
        String s="";
        for(int x=0;x<size;x++){
            if(x==size-1){
                s=s+bean.getAttendees().get(x).getDisplayName();
            }else{
                s=s+bean.getAttendees().get(x).getDisplayName()+"、";
            }
        }
        personnel.setText(s);
    }


    public void getMeetState(MeetingBean.EventsBean eventsBean, TextView view) {
        String state = null;
        long currentTimeMillis = System.currentTimeMillis();
        String endTime = eventsBean.getEnd().getDateTime();
        String startTime = eventsBean.getStart().getDateTime();
        Date endDate = dateUtils.transitionTime(endTime);
        Date startDate = dateUtils.transitionTime(startTime);
        if (endDate.getTime() < currentTimeMillis) {
            //排除时间已经超过的会议
            state = "会议已结束~";
            view.setTextColor(Color.parseColor("#999797"));
        } else if (startDate.getTime() > currentTimeMillis) {
            state = "会议暂未开始，请稍等~";
            view.setTextColor(Color.parseColor("#0fb89a"));
        } else {
            view.setTextColor(Color.parseColor("#ee332a"));
            state = "会议正在进行中，请勿打扰~";
        }
        view.setText(state);
    }
}
