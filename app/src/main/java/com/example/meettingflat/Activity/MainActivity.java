package com.example.meettingflat.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DateUtils;
import com.example.meettingflat.Utils.GsonUtils;
import com.example.meettingflat.Utils.Instruct;
import com.example.meettingflat.Utils.SPUtil;
import com.example.meettingflat.Utils.SerialPortUtil;
import com.example.meettingflat.Utils.WaitDialogTime;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.ErrBean;
import com.example.meettingflat.bean.MeetingBean;
import com.example.meettingflat.view.LockPasswordDialog;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int MEETSELECTACTIVITYCODE = 100;
    private MAPI mapi;
    private DateUtils dateUtils;
    private String token;
    private String uid;
    private LockPasswordDialog passwordDialog;
    private WaitDialogTime dialogTime;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                quest();
            }
        }
    };
    private SPUtil spUtil;
    private String mMeetAddress;
    private SerialPortUtil serialPort;
    private TextView meetTime;
    private RelativeLayout meetStateRl;
    private TextView meetName;
    private TextView meetState;
    private Button open;
    private ImageView next;
    private TextView meetAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapi = new MAPI();
        initView();
        initData();
        initSerialPort();
        initListener();
    }

    private void initView() {
        meetAddress= findViewById(R.id.meet_address);
        next = findViewById(R.id.next);
        open = findViewById(R.id.open);
        meetState = findViewById(R.id.meet_state);
        meetName = findViewById(R.id.meet_name);
        meetStateRl = findViewById(R.id.meet_state_rl);
        meetTime = findViewById(R.id.meet_time);
    }

    private void initListener() {
        meetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mMeetAddress)) {
                    Intent intent = new Intent(MainActivity.this, CurrentMeetingActivity.class);
                    intent.putExtra("meetAddress", mMeetAddress);
                    startActivity(intent);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, MeetSelectActivity.class), MEETSELECTACTIVITYCODE);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordDialog==null){
                    passwordDialog=new LockPasswordDialog(MainActivity.this);
                    passwordDialog.setClickListener(new LockPasswordDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(String password) {
                            if(dialogTime==null){
                                dialogTime=new WaitDialogTime(MainActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                            }
                            dialogTime.show();
                            String s=Instruct.SENDDOOR+password+"\r\n" ;
                            serialPort.sendDate(s.getBytes());
                        }
                    });
                }
                passwordDialog.show();
            }
        });
    }

    /**
     * 串口socket
     */
    private void initSerialPort() {
        serialPort = SerialPortUtil.getInstance();
        serialPort.readCode(new SerialPortUtil.DataListener() {
            @Override
            public void getData(String data) {
                if (data.contains(Instruct.DOOR)) {
                    if(dialogTime!=null&&dialogTime.isShowing())
                        dialogTime.show();
                    String[] split = data.split("=");
                    switch (split[1]) {
                        case "1"://表示故障1
                            Toast.makeText(MainActivity.this, "故障1", Toast.LENGTH_SHORT).show();
                            break;
                        case "2"://表示故障2
                            Toast.makeText(MainActivity.this, "故障2", Toast.LENGTH_SHORT).show();
                            break;
                        case "C"://表示故障3
                            Toast.makeText(MainActivity.this, "故障3", Toast.LENGTH_SHORT).show();
                            break;
                        case "D"://表示故障4
                            Toast.makeText(MainActivity.this, "故障4", Toast.LENGTH_SHORT).show();
                            break;
                        case "A"://表示报警1
                            Toast.makeText(MainActivity.this, "报警1", Toast.LENGTH_SHORT).show();
                            break;
                        case "B"://表示报警2
                            Toast.makeText(MainActivity.this, "报警2", Toast.LENGTH_SHORT).show();
                            break;
                        case "E"://表示假锁状态
                            Toast.makeText(MainActivity.this, "假锁", Toast.LENGTH_SHORT).show();
                            break;
                        case "8"://8表示开门成功
                            break;
                        case "9"://表示关门成功
                            break;
                    }
                }
            }
        });
    }

    private void initData() {
        spUtil = SPUtil.getInstance(this);
        mMeetAddress = spUtil.getSettingParam("meetAddress", null);
        if (TextUtils.isEmpty(mMeetAddress)) {
            meetAddress.setText("去选择会议室");
        } else {
            meetAddress.setText(mMeetAddress);

        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
        dateUtils = DateUtils.getInstance();

    }


    /**
     * 网络请求
     */
    private void quest() {
        if (TextUtils.isEmpty(mMeetAddress)) {
            return;
        }
        long time = System.currentTimeMillis();
        String startTime = dateUtils.dateFormatStart(time);
        String endTime = dateUtils.dateFormatEnd(time);
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(uid)) {
            mapi.getToken(MainActivity.this, new MAPI.CallString() {
                @Override
                public void call(String token) {
                    MainActivity.this.token = token;
                    mapi.getUid(MainActivity.this, token, new MAPI.CallString() {
                        @Override
                        public void call(String uid) {
                            mapi.getMeeting(MainActivity.this, token, uid, startTime, endTime, new MAPI.IMeeting() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void call(MeetingBean bean) {
                                    MainActivity.this.uid = uid;
                                    setData(bean, System.currentTimeMillis());
                                }

                                @Override
                                public void error() {
                                    MainActivity.this.token = null;
                                    MainActivity.this.uid = null;
                                }
                            });
                        }
                    });
                }
            });
        } else {
            mapi.getMeeting(MainActivity.this, token, uid, startTime, endTime, new MAPI.IMeeting() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void call(MeetingBean bean) {
                    setData(bean, System.currentTimeMillis());
                }

                @Override
                public void error() {
                    MainActivity.this.token = null;
                    MainActivity.this.uid = null;
                }
            });
        }
    }


    /**
     * 数据处理
     *
     * @param bean
     * @param time
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(MeetingBean bean, long time) {
        long beginTime = 0;
        int flag = -1;
        List<MeetingBean.EventsBean> events = bean.getEvents();
        if (events == null || events.size() == 0) {
            meetState.setText("待启用");
            meetStateRl.setBackgroundResource(R.drawable.shape_radius_8_1a252f);
            meetTime.setVisibility(View.GONE);
            meetName.setText("暂无会议日程");
            return;
        }
        meetTime.setVisibility(View.VISIBLE);
        for (int x = 0; x < events.size(); x++) {
            if (events.get(x).getLocation().getDisplayName().equals(meetAddress)) {//是否是该会议室的会议
                String endTime = events.get(x).getEnd().getDateTime();
                Date endDate = dateUtils.transitionTime(endTime);
                String startTime = events.get(x).getStart().getDateTime();
                Date startDate = dateUtils.transitionTime(startTime);
                if (endDate.getTime() < time) {
                    //排除时间已经超过的会议
                    continue;
                }
                //选出一个时间最早的
                if (beginTime == 0 || startDate.getTime() < beginTime) {
                    beginTime = startDate.getTime();
                    flag = x;
                }
            }
        }

        if (beginTime == 0) {
            //空闲
            meetState.setText("待启用");
            meetStateRl.setBackgroundResource(R.drawable.shape_radius_8_1a252f);
            meetTime.setVisibility(View.GONE);
            meetName.setText("暂无会议日程");
            return;
        }

        if (beginTime > time) {//还没开始
            if ((beginTime - time) > 15 * 60 * 1000) {
                //空闲中
                meetState.setText("空闲中");
                meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_24a37e);
            } else {
                //准备中
                meetState.setText("即将开会");
                meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_c88525);
            }
        } else {
            //会议中
            meetState.setText("会议中");
            meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_ad3329);
        }
        MeetingBean.EventsBean eventsBean = events.get(flag);
        Date date = dateUtils.transitionTime(eventsBean.getStart().getDateTime());
        Date date1 = dateUtils.transitionTime(eventsBean.getEnd().getDateTime());

        meetName.setText(eventsBean.getSummary());
        meetTime.setText(dateUtils.dateFormat8(date.getTime()) + "-" + dateUtils.dateFormat8(date1.getTime()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        quest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEETSELECTACTIVITYCODE && data != null) {
            String name = data.getStringExtra("meetAddress");
            mMeetAddress = name;
            meetAddress.setText(name);
        }
    }
}