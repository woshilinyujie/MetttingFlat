package com.example.meettingflat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DateUtils;
import com.example.meettingflat.Utils.DeviceUtils;
import com.example.meettingflat.Utils.GsonUtils;
import com.example.meettingflat.Utils.Instruct;
import com.example.meettingflat.Utils.RbMqUtils;
import com.example.meettingflat.Utils.SPUtil;
import com.example.meettingflat.Utils.SerialPortUtil;
import com.example.meettingflat.Utils.WaitDialogTime;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.ErrBean;
import com.example.meettingflat.bean.MeetingBean;
import com.example.meettingflat.bean.PermissionBean;
import com.example.meettingflat.bean.UserBean;
import com.example.meettingflat.view.LockPasswordDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    private ImageView setting;
    private TextView meetState;
    private Button open;
    private ImageView next;
    private TextView meetAddress;
    private UserBean userBean;
    private List<PermissionBean> permissionList = new ArrayList<>();
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case Instruct.SHOWTOAST:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private ScheduledExecutorService threads;
    private Runnable runnable;
    private RbMqUtils rbmq;
    private String doorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapi = new MAPI();
        initView();
        initSerialPort();
        initData();
        initListener();
    }

    private void initView() {
        meetAddress = findViewById(R.id.meet_address);
        next = findViewById(R.id.next);
        open = findViewById(R.id.open);
        meetState = findViewById(R.id.meet_state);
        meetName = findViewById(R.id.meet_name);
        meetStateRl = findViewById(R.id.meet_state_rl);
        meetTime = findViewById(R.id.meet_time);
        setting = findViewById(R.id.setting);
    }

    private void initListener() {
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                intent.putExtra("meetAddress", mMeetAddress);
                startActivity(intent);
            }
        });
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
                if (passwordDialog == null) {
                    passwordDialog = new LockPasswordDialog(MainActivity.this);
                    passwordDialog.setClickListener(new LockPasswordDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(String password) {
                            if (dialogTime == null) {
                                dialogTime = new WaitDialogTime(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                            }
                            dialogTime.show();
                            String s = Instruct.SENDDOOR + password + "\r\n";
                            serialPort.sendDate(s.getBytes());
                            rbmq.pushMsg("openDoor:"+mMeetAddress+","+doorID);
                        }
                    });
                }
                passwordDialog.show();
            }
        });
        runnable = new Runnable() {
            @Override
            public void run() {
                if (permissionList.size() > 0) {
                    Iterator it = permissionList.iterator();
                    while (it.hasNext()) {
                        PermissionBean next = (PermissionBean) it.next();
                        if (isPermission(next)) {
                            if( !next.isHave()){
                                next.setHave(true);
                                //发送添加指令
                                String s = Instruct.SENDBULECARD + next.getNum() + "\r\n";
                                serialPort.sendDate(s.getBytes());
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //发送删除指令
                            it.remove();
                            String s = Instruct.DELETEBULECARD + next.getNum() + "\r\n";
                            serialPort.sendDate(s.getBytes());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        };
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
                    if (dialogTime != null && dialogTime.isShowing())
                        dialogTime.dismiss();
                    String[] split = data.split("=");
                    switch (split[1]) {
                        case "8"://8表示开门成功
                        case "11"://表示无密码开门成功
                        {
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.obj = "开门成功";
                            handler.sendMessage(message);
                        }
                        break;
                        case "9"://表示关门成功
                            break;
                        case "10"://密码错误
                        {
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.obj = "密码错误";
                            handler.sendMessage(message);
                        }
                        break;
                    }
                }
            }
        });
    }

    private void initData() {
        doorID = DeviceUtils.getSerialNumber(this);
        rbmq = new RbMqUtils();
        setMq();
        threads = Executors.newScheduledThreadPool(1);
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
        //权限重置
        String s = Instruct.DELETEBULECARD + "000000000000" + "\r\n";
        serialPort.sendDate(s.getBytes());
    }

    /**
     * 服务器socket
     */
    public void setMq() {
        //发送端
        rbmq.publishToAMPQ("");
        //接收端
        rbmq.subscribe(doorID);
        rbmq.setUpConnectionFactory();
        rbmq.setRbMsgListener(new RbMqUtils.OnRbMsgListener() {
            @Override
            public void AcceptMsg(String msg) {//服务器返回数据
                Log.e("服务器发给平板---", msg);
                if(msg.contains("openDoor:open")){
                    if (dialogTime == null) {
                        dialogTime = new WaitDialogTime(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                    }
                    dialogTime.show();
                    String s = Instruct.OPENDOOR;
                    serialPort.sendDate(s.getBytes());
                }
            }
        });

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

        mapi.searchALL(this, new MAPI.CallUser() {
            @Override
            public void call(UserBean bean) {
                MainActivity.this.userBean = bean;
            }
        });
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
        long lastTime = 0;
        int flag = -1;
        List<MeetingBean.EventsBean> events = bean.getEvents();
        if (events == null || events.size() == 0) {
            meetState.setText("待启用");
            meetStateRl.setBackgroundResource(R.mipmap.wait);
            meetTime.setVisibility(View.GONE);
            meetName.setText("暂无会议日程");
            //删除或添加权限
            sendOrDeletePermission();
            return;
        }
        meetTime.setVisibility(View.VISIBLE);
        for (int x = 0; x < events.size(); x++) {
            MeetingBean.EventsBean.LocationBean location = events.get(x).getLocation();
            if (location != null && location.getDisplayName().equals(mMeetAddress)&&events.get(x).getStatus().equals("confirmed")) {//是否是该会议室的会议
                //未取消会议
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
                    lastTime = endDate.getTime();
                    flag = x;
                }
            }else if(location != null && location.getDisplayName().equals(mMeetAddress)&&permissionList!=null){
                //取消的会议
                for(int y=0;y<permissionList.size();y++){
                     if(permissionList.get(y).getMeetId().equals(events.get(x).getId())){
                         //取消的会议删除权限
                         permissionList.get(y).setCancel(true);
                         sendOrDeletePermission();
                     }
                }
            }
        }

        if (beginTime == 0) {
            //空闲
            meetState.setText("待启用");
            meetStateRl.setBackgroundResource(R.mipmap.wait);
            meetTime.setVisibility(View.GONE);
            meetName.setText("暂无会议日程");
            //删除或添加权限
            sendOrDeletePermission();
            return;
        }

        if (beginTime > time) {//还没开始
            if ((beginTime - time) > 15 * 60 * 1000) {
                //空闲中
                meetState.setText("空闲中");
                meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_24a37e);
                //通知后板移除
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
        addPermission(events, flag, beginTime, lastTime);
        MeetingBean.EventsBean eventsBean = events.get(flag);
        Date date = dateUtils.transitionTime(eventsBean.getStart().getDateTime());
        Date date1 = dateUtils.transitionTime(eventsBean.getEnd().getDateTime());
        meetName.setText(eventsBean.getSummary());
        meetTime.setText(dateUtils.dateFormat8(date.getTime()) + "-" + dateUtils.dateFormat8(date1.getTime()));

        //删除或添加权限
        sendOrDeletePermission();
    }


    public void addPermission(List<MeetingBean.EventsBean> events, int flag, long beginTime, long lastTime) {
        if (userBean != null && userBean.getData().size() > 0) {
            for (int x = 0; x < events.get(flag).getAttendees().size(); x++) {
                for (int y = 0; y < userBean.getData().size(); y++) {
                    String name = events.get(flag).getAttendees().get(x).getDisplayName();
                    String name1 = userBean.getData().get(y).getParticipants();
                    if (name.equals(name1)) {
                        if (permissionList.size() > 0) {
                            boolean have = false;
                            for (int z = 0; z < permissionList.size(); z++) {
                                if (name.equals(permissionList.get(z).getName())) {
                                    //已经存在更新下时间
                                    String startTime = events.get(flag).getStart().getDateTime();
                                    Date startDate = dateUtils.transitionTime(startTime);
                                    String endTime = events.get(flag).getEnd().getDateTime();
                                    Date endDate = dateUtils.transitionTime(endTime);
                                    permissionList.get(z).setStartTime(startDate.getTime());
                                    permissionList.get(z).setEndTime(endDate.getTime());
                                    permissionList.get(z).setMeetId(events.get(flag).getId());
                                    have = true;
                                }
                            }
                            if (!have) {
                                PermissionBean permissionBean = new PermissionBean();
                                permissionBean.setName(name);
                                permissionBean.setNum(userBean.getData().get(y).getBluetooth());
                                permissionBean.setStartTime(beginTime);
                                permissionBean.setEndTime(lastTime);
                                permissionBean.setMeetId(events.get(flag).getId());
                                permissionList.add(permissionBean);
                            }
                        } else {
                            PermissionBean permissionBean = new PermissionBean();
                            permissionBean.setName(name);
                            permissionBean.setNum(userBean.getData().get(y).getBluetooth());
                            permissionBean.setStartTime(beginTime);
                            permissionBean.setEndTime(lastTime);
                            permissionBean.setMeetId(events.get(flag).getId());
                            permissionList.add(permissionBean);
                        }
                    }
                }
            }
        }
    }

    public void sendOrDeletePermission() {
        threads.execute(runnable);
    }

    public boolean isPermission(PermissionBean bean) {
        return (System.currentTimeMillis() - bean.getEndTime()) < 30 * 60 * 1000&&!bean.isCancel();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
}