package com.example.meettingflat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DateUtils;
import com.example.meettingflat.Utils.DeviceUtils;
import com.example.meettingflat.Utils.GsonUtils;
import com.example.meettingflat.Utils.Instruct;
import com.example.meettingflat.Utils.RbMqUtils;
import com.example.meettingflat.Utils.SPUtil;
import com.example.meettingflat.Utils.SerialPortUtil;
import com.example.meettingflat.Utils.VersionUtils;
import com.example.meettingflat.Utils.WaitDialogTime;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.AdBean;
import com.example.meettingflat.bean.DoorBean;
import com.example.meettingflat.bean.ErrBean;
import com.example.meettingflat.bean.GetLinkBean;
import com.example.meettingflat.bean.MainMsgBean;
import com.example.meettingflat.bean.MeetingBean;
import com.example.meettingflat.bean.PermissionBean;
import com.example.meettingflat.bean.SetMsgBean;
import com.example.meettingflat.bean.UpdataJsonBean;
import com.example.meettingflat.bean.UpdateAppBean;
import com.example.meettingflat.bean.UserBean;
import com.example.meettingflat.view.LockPasswordDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity implements PLOnCompletionListener {
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
                long l = System.currentTimeMillis();
                String s = dateUtils.dateFormat15(l);
                systemTime.setText(s);
                quest();
            }
        }
    };
    private SPUtil spUtil;
    private String mMeetAddress;
    private SerialPortUtil serialPort;
    private TextView meetTime;
    private TextView systemTime;
    private LinearLayout meetStateRl;
    private TextView meetName;
    private ImageView setting;
    private TextView meetState1;
    private TextView meetState;
    private Button open;
    private Button doorBell;
    private ImageView next;
    private LinearLayout openLl;
    private TextView meetAddress;
    private UserBean userBean;
    private int normallyOPenFlag;
    private List<PermissionBean> permissionList = new ArrayList<>();
    private ScheduledExecutorService threads;
    private Runnable runnable;
    private RbMqUtils rbmq;
    private String doorID;
    private String currentPassword;
    private LinearLayout meetAddressLl;
    private AVOptions options;
    private Banner banner;
    private PLVideoView plVideoView;
    private ArrayList<AdBean.DataBean> adDataList;
    private int current = 0;
    private AlertDialog mDownloadDialog;
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case Instruct.SHOWTOAST:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case Instruct.DIALOG:
                    if (dialogTime != null) {
                        dialogTime.dismiss();
                    }
                    break;
                case Instruct.PUSHLINK:
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    rbmq.pushMsg("openDoor:" + mMeetAddress + "," + doorID);
                    break;
                case Instruct.REFRESHTIME:
                    long l = System.currentTimeMillis();
                    String s = dateUtils.dateFormat15(l);
                    systemTime.setText(s);
                    break;
                case Instruct.UPDATE:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                        mDownloadDialog = null;
                    }
                    OkGo.getInstance().cancelTag(MainActivity.this);
                    requestPermission();
                    handler.sendEmptyMessageDelayed(Instruct.UPDATE, 24 * 60 * 60 * 1000);
                    break;
                case Instruct.DOORBELL://门铃延迟
//                    mediaplayer.stop();
//                    try {
//                        mediaplayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    doorBell.setEnabled(true);
                    break;
            }
        }
    };
    private MAPI.CallAd callAdListener;
    private int version;
    private PrintWriter printWriter;
    private ProgressBar mProgress;
    private MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        meetState1 = findViewById(R.id.meet_state1);
        meetName = findViewById(R.id.meet_name);
        meetStateRl = findViewById(R.id.meet_state_rl);
        meetTime = findViewById(R.id.meet_time);
        systemTime = findViewById(R.id.system_time);
        setting = findViewById(R.id.setting);
        meetAddressLl = findViewById(R.id.meet_address_ll);
        banner = findViewById(R.id.banner);
        plVideoView = findViewById(R.id.video);
        doorBell = findViewById(R.id.door_bell);
        openLl = findViewById(R.id.open_ll);
    }

    private void initListener() {
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("meetAddress", mMeetAddress);
                intent.putExtra("normallyOPen", normallyOPenFlag);
                startActivity(intent);
            }
        });
        meetAddressLl.setOnClickListener(new View.OnClickListener() {
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

        doorBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorBell.setEnabled(false);
//                mediaplayer.setLooping(true);//设置为循环播放
//                mediaplayer.start();
                handler.sendEmptyMessageDelayed(Instruct.DOORBELL,3000);
                serialPort.sendDate((Instruct.GETID + "\r\n").getBytes());//先去拿id
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
                            //先使用固定密码开门
                            currentPassword = password;
                            if (dialogTime == null) {
                                dialogTime = new WaitDialogTime(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                            }
                            dialogTime.show();
                            String s = Instruct.SENDDOOR + password + "\r\n";
                            serialPort.sendDate(s.getBytes());
                        }
                    });
                }
                passwordDialog.show();
            }
        });

        banner.setAdapter(new BannerImageAdapter<AdBean.DataBean>(adDataList) {
            @Override
            public void onBindView(BannerImageHolder holder, AdBean.DataBean data, int position, int size) {
                String msg = data.getMsg();
                //图片加载自己实现
                Glide.with(holder.itemView)
                        .load(msg)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        }).addBannerLifecycleObserver(this).setIndicator(new CircleIndicator(this));


        runnable = new Runnable() {
            @Override
            public void run() {
                if (permissionList.size() > 0) {
                    Iterator it = permissionList.iterator();
                    while (it.hasNext()) {
                        PermissionBean next = (PermissionBean) it.next();
                        if (isPermission(next)) {
                            if (!next.isHave()) {
                                next.setHave(true);
                                //发送添加指令
                                String s = Instruct.SENDBULECARD + next.getNum() + "\r\n";
                                Log.e("发送添加指令：", s);
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
                            Log.e("发送删除指令：", s);
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
                    String[] split = data.split("=");
                    switch (split[1]) {
                        case "8"://表示密码开门成功
                        {
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.obj = "开门成功";
                            handler.sendMessage(message);
                            handler.sendEmptyMessage(Instruct.PUSHLINK);
                        }
                        break;
                        case "11"://表示无密码开门成功
                        {
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            Message message = handler.obtainMessage();
                            message.what = Instruct.PUSHLINK;
                            message.obj = "开门成功";
                            handler.sendMessage(message);
                        }
                        case "12"://服务器通知开门成功
                        {
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            Message message = handler.obtainMessage();
                            message.what = Instruct.SHOWTOAST;
                            message.obj = "开门成功";
                            handler.sendMessage(message);
                        }
                        break;
                        case "9"://表示关门成功
                            break;
                        case "10"://密码错误
                        {
                            Log.e("串口返回10", "permissionListSize" + permissionList.size());
                            Message message = handler.obtainMessage();
                            message.what = Instruct.SHOWTOAST;
                            if (permissionList.size() == 0) {
                                if (dialogTime != null)
                                    dialogTime.dismiss();
                                message.obj = "无法开门，请检查开门权限";
                                handler.sendMessage(message);
                                return;
                            }
                            boolean passwordOpen = false;
                            boolean timeOver = false;
                            for (int x = 0; x < permissionList.size(); x++) {
                                Log.e("串口返回10", "permissionList:" + permissionList.toString());
                                if (permissionList.get(x).getPassWord() != null && permissionList.get(x).isHave()) {
                                    timeOver = true;
                                    String mPassword = permissionList.get(x).getPassWord().replaceAll(" ", "");
                                    int length = mPassword.length();
                                    String substring = mPassword.substring(length - 6, length);
                                    if (substring.equals(currentPassword)) {
                                        passwordOpen = true;
                                        break;
                                    }
                                }
                            }

                            if (passwordOpen) {
                                String s = Instruct.OPENDOOR + "\r\n";
                                serialPort.sendDate(s.getBytes());
                            } else {
                                if (dialogTime != null)
                                    dialogTime.dismiss();
                                if (!timeOver) {
                                    message.obj = "当前时间无法开门";
                                } else {
                                    message.obj = "密码错误";
                                }
                                handler.sendMessage(message);
                            }

                        }
                        break;
                        case "13"://常开
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            EventBus.getDefault().post(new SetMsgBean(Instruct.NORMALLYOPEN));
                            //通知 服务器联动
                            rbmq.pushMsg("normallyOpen:" + mMeetAddress + "," + doorID);
                            normallyOPenFlag = 1;
                            break;
                        case "14"://服务器常开
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            EventBus.getDefault().post(new SetMsgBean(Instruct.NORMALLYOPEN1));
                            normallyOPenFlag = 1;
                            break;
                        case "15"://取消常开
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            EventBus.getDefault().post(new SetMsgBean(Instruct.CANCELNORMALLYOPEN));
                            //通知 服务器联动
                            rbmq.pushMsg("cancelNormallyOpen:" + mMeetAddress + "," + doorID);
                            normallyOPenFlag = 2;
                            break;
                        case "16"://服务器取消常开
                            if (dialogTime != null)
                                dialogTime.dismiss();
                            EventBus.getDefault().post(new SetMsgBean(Instruct.CANCELNORMALLYOPEN1));
                            normallyOPenFlag = 2;
                            break;
                    }
                } else if (data.contains("AT+DEFAULT=")) {
                    String[] s = data.split("=");
                    String[] split = s[1].split(",");
                    switch (Integer.parseInt(split[0])) {
                        case 7://已经常开
                            if (normallyOPenFlag != 2) {
                                normallyOPenFlag = 2;
                            }
                            break;
                        case 8://没有开启常开
                            if (normallyOPenFlag != 1) {
                                normallyOPenFlag = 1;
                            }
                            break;
                    }
                }
            }
        });
    }

    private void initData() {
        mediaplayer = MediaPlayer.create(this, R.raw.alarm);
        int select = SPUtil.getInstance(this).getSettingParam("doorSelect", 0);
        if(select==0){
            openLl.setVisibility(View.VISIBLE);
        }else{
            openLl.setVisibility(View.GONE);
        }
        handler.sendEmptyMessage(Instruct.REFRESHTIME);
        //获取硬件数据
        serialPort.sendDate((Instruct.DATA + "\r\n").getBytes());
        EventBus.getDefault().register(this);
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
        requestPermission();
        handler.sendEmptyMessageDelayed(Instruct.UPDATE, 24 * 60 * 60 * 1000);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
        dateUtils = DateUtils.getInstance();
        //重置
        String s = Instruct.DELETEBULECARD + "000000000000" + "\r\n";
        serialPort.sendDate(s.getBytes());
        callAdListener = new MAPI.CallAd() {
            @Override
            public void call(AdBean bean) {
                adDataList = bean.getData();
                if (adDataList != null && adDataList.size() > 0) {
                    if (adDataList.get(0).getType().equals("mp4")) {
                        banner.setVisibility(View.GONE);
                        banner.stop();
                        plVideoView.setVisibility(View.VISIBLE);
                        openVideoFromUri(bean.getData().get(0).getMsg());
                    } else {
                        //轮播图
                        plVideoView.setVisibility(View.GONE);
                        plVideoView.pause();
                        plVideoView.stopPlayback();
                        banner.setVisibility(View.VISIBLE);
                        banner.setDatas(adDataList);
                        banner.start();
                    }
                }
            }
        };
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
                if (msg.contains("openDoor:open")) {
                    if (dialogTime == null) {
                        dialogTime = new WaitDialogTime(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                    }
                    dialogTime.show();
                    String s = Instruct.OPENDOOR1 + "\r\n";
                    serialPort.sendDate(s.getBytes());
                } else if (msg.contains("normallyOpen:open")) {
                    String s = Instruct.NORMALLYOPEN1 + "\r\n";
                    serialPort.sendDate(s.getBytes());
                } else if (msg.contains("normallyOpen:cancel")) {
                    String s = Instruct.CANCELNORMALLYOPEN1 + "\r\n";
                    serialPort.sendDate(s.getBytes());
                } else if (msg.contains("refresh")) {
                    mapi.getAd(MainActivity.this, callAdListener);
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
            meetState.setVisibility(View.GONE);
            meetState1.setVisibility(View.GONE);
            meetStateRl.setBackgroundResource(R.mipmap.wait);
            meetTime.setVisibility(View.GONE);
            if (meetAddress.getText().toString().equals("请设置会议室名称")) {
                meetName.setText("待启用~");
            } else {
                meetName.setText("今日暂无会议哦~");
            }
            //删除或添加
            sendOrDeletePermission();
            return;
        }
        meetTime.setVisibility(View.VISIBLE);
        for (int x = 0; x < events.size(); x++) {
            MeetingBean.EventsBean.LocationBean location = events.get(x).getLocation();

            if (location != null&&location.getDisplayName()!=null && location.getDisplayName().equals(mMeetAddress) && events.get(x).getStatus().equals("confirmed")) {//是否是该会议室的会议
                //未取消会议
                String endTime = events.get(x).getEnd().getDateTime();
                String startTime = events.get(x).getStart().getDateTime();
                if (endTime == null || startTime == null) {
                    return;
                }
                Date endDate = dateUtils.transitionTime(endTime);
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
            } else if (location != null&&location.getDisplayName()!=null && location.getDisplayName().equals(mMeetAddress) && permissionList != null) {
                //取消的会议
                for (int y = 0; y < permissionList.size(); y++) {
                    if (permissionList.get(y).getMeetId().equals(events.get(x).getId())) {
                        //取消的会议删除
                        permissionList.get(y).setCancel(true);
                        sendOrDeletePermission();
                    }
                }
            }
        }

        if (beginTime == 0) {
            //空闲
            meetState.setVisibility(View.GONE);
            meetState1.setVisibility(View.GONE);
            meetStateRl.setBackgroundResource(R.mipmap.wait);
            meetTime.setVisibility(View.GONE);
            if (meetAddress.getText().toString().equals("请设置会议室名称")) {
                meetName.setText("待启用~");
            } else {
                meetName.setText("今日暂无会议哦~");
            }
            //删除或添加
            sendOrDeletePermission();
            return;
        }


        meetState.setVisibility(View.VISIBLE);
        meetState1.setVisibility(View.VISIBLE);
        meetStateRl.setBackgroundResource(R.drawable.shape_radius_8_1a252f);
        if (beginTime > time) {//还没开始
//            if ((beginTime - time) > 15 * 60 * 1000) {
            //空闲中
            meetState.setText("空闲");
            meetState.setBackgroundResource(R.drawable.half_circle);
            meetState1.setTextColor(Color.parseColor("#0fb89a"));
            meetState1.setText("暂无会议，会议室空闲中~");
            //通知后板移除
//            } else {
//                //准备中
//                meetState.setText("即将开会");
//                meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_c88525);
//            }
        } else {
            //会议中
            meetState1.setTextColor(Color.parseColor("#ea332a"));
            meetState1.setText("会议正在进行中，请勿打扰~");
            meetState.setText("会议中");
            meetState.setBackgroundResource(R.drawable.half_circle_e34235);
//            meetStateRl.setBackgroundResource(R.drawable.shape_radius_4_ad3329);
        }
        addPermission(events, flag, beginTime, lastTime);
        MeetingBean.EventsBean eventsBean = events.get(flag);
        Date date = dateUtils.transitionTime(eventsBean.getStart().getDateTime());
        Date date1 = dateUtils.transitionTime(eventsBean.getEnd().getDateTime());
        meetName.setText(eventsBean.getSummary());
        meetTime.setText(dateUtils.dateFormat8(date.getTime()) + "-" + dateUtils.dateFormat8(date1.getTime()));

        //删除或添加
        sendOrDeletePermission();
    }


    public void addPermission(List<MeetingBean.EventsBean> events, int flag, long beginTime, long lastTime) {
        Log.e("添加", "打印events：" + events.toString());
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
                                    //已经存在更新下时间和密码
                                    String startTime = events.get(flag).getStart().getDateTime();
                                    Date startDate = dateUtils.transitionTime(startTime);
                                    String endTime = events.get(flag).getEnd().getDateTime();
                                    Date endDate = dateUtils.transitionTime(endTime);
                                    permissionList.get(z).setStartTime(startDate.getTime());
                                    permissionList.get(z).setEndTime(endDate.getTime());
                                    permissionList.get(z).setMeetId(events.get(flag).getId());
                                    if (events.get(flag).getOnlineMeetingInfo() != null)
                                        permissionList.get(z).setPassWord(events.get(flag).getOnlineMeetingInfo().getExtraInfo().getRoomCode());
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
                                if (events.get(flag).getOnlineMeetingInfo() != null)
                                    permissionBean.setPassWord(events.get(flag).getOnlineMeetingInfo().getExtraInfo().getRoomCode());
                                permissionList.add(permissionBean);
                            }
                        } else {
                            PermissionBean permissionBean = new PermissionBean();
                            permissionBean.setName(name);
                            permissionBean.setNum(userBean.getData().get(y).getBluetooth());
                            permissionBean.setStartTime(beginTime);
                            permissionBean.setEndTime(lastTime);
                            permissionBean.setMeetId(events.get(flag).getId());
                            if (events.get(flag).getOnlineMeetingInfo() != null)
                                permissionBean.setPassWord(events.get(flag).getOnlineMeetingInfo().getExtraInfo().getRoomCode());
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
        if (bean.isCancel()) {
            return false;
        }
        if (System.currentTimeMillis() < bean.getEndTime() && System.currentTimeMillis() > bean.getStartTime()) {
            return true;
        }
        long finishTime = System.currentTimeMillis() - bean.getEndTime();
        long startTime = System.currentTimeMillis() - bean.getStartTime();
        if ((finishTime > 0 && finishTime < 30 * 60 * 1000) || (startTime < 0 && startTime > -15 * 60 * 1000)) {
            return true;
        }

        return false;
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


    //---------------------eventBus----------------
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainMsgBean msgBean) {
        switch (msgBean.getMsg()) {
            case Instruct.NORMALLYOPEN://常开
                serialPort.sendDate((Instruct.NORMALLYOPEN + "\r\n").getBytes());
                break;
            case Instruct.CANCELNORMALLYOPEN://取消常开
                serialPort.sendDate((Instruct.CANCELNORMALLYOPEN + "\r\n").getBytes());
                break;
            case Instruct.SELECTDOOR://子门母门  切换
                if (msgBean.getFlag() == 0) {//母门
                    openLl.setVisibility(View.VISIBLE);
                    SPUtil.getInstance(this).setSettingParam("doorSelect",0);
                } else {//子门
                    openLl.setVisibility(View.GONE);
                    SPUtil.getInstance(this).setSettingParam("doorSelect",1);
                }
                break;
        }
    }

    private void openVideoFromUri(String url) {
        if (options == null) {
            options = new AVOptions();
            plVideoView.setOnCompletionListener(this);
        }
        options.setString(AVOptions.KEY_CACHE_DIR, getFilesDir() + "/" + url);
        plVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        plVideoView.setAVOptions(options);
        plVideoView.setVideoPath(url);
        plVideoView.start();

    }

    //----------------------播放器回调----------------------------
    @Override
    public void onCompletion() {
        if (adDataList.size() > 1) {
            current++;
            current = current % adDataList.size();
            openVideoFromUri(adDataList.get(current).getMsg());
        } else {
            //重新播放
            plVideoView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        quest();
        if (!plVideoView.isPlaying()) {
            mapi.getAd(MainActivity.this, callAdListener);
        }
        if (plVideoView.getVisibility() == View.VISIBLE) {
            plVideoView.start();
        }
        if (banner.getVisibility() == View.VISIBLE) {
            banner.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (plVideoView.getVisibility() == View.VISIBLE) {
            plVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (banner.getVisibility() == View.VISIBLE) {
            banner.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaplayer.stop();
        mediaplayer.release();
        handler.removeMessages(Instruct.UPDATE);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        EventBus.getDefault().unregister(this);
        plVideoView.stopPlayback();
        banner.destroy();
    }

    //------------------------------------下载-------------------------------------


    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        checkUpdate();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        checkUpdate();
                    }
                })
                .start();
    }

    protected void checkUpdate() {
        version = VersionUtils.getVersionCode(this);
        requestAppUpdate(version, new DataRequestListener<UpdateAppBean>() {
            @Override
            public void success(UpdateAppBean data) {
                downloadApp(data.getPUS().getBody().getUrl());
            }

            @Override
            public void fail(String msg) {

            }
        });
    }

    private void requestAppUpdate(int version, final DataRequestListener<UpdateAppBean> listener) {
        UpdataJsonBean updataJsonBean = new UpdataJsonBean();
        UpdataJsonBean.PUSBean pusBean = new UpdataJsonBean.PUSBean();
        UpdataJsonBean.PUSBean.BodyBean bodyBean = new UpdataJsonBean.PUSBean.BodyBean();
        UpdataJsonBean.PUSBean.HeaderBean headerBean = new UpdataJsonBean.PUSBean.HeaderBean();

        bodyBean.setToken("");
        bodyBean.setVendor_name("general");
        bodyBean.setPlatform("android");

        bodyBean.setEndpoint_type("WL025S1-M");

        bodyBean.setCurrent_version(version + "");

        headerBean.setApi_version("1.0");
        headerBean.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        headerBean.setSeq_id("1");

        pusBean.setBody(bodyBean);
        pusBean.setHeader(headerBean);
        updataJsonBean.setPUS(pusBean);

        String s = GsonUtils.GsonString(updataJsonBean);
        String path = "";
        path = "https://pus.wonlycloud.com:10400";
        OkGo.<String>post(path).upJson(s).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body();
                Gson gson = new Gson();
                try {
                    UpdateAppBean updateAppBean = gson.fromJson(s, UpdateAppBean.class);
                    if (Integer.parseInt(updateAppBean.getPUS().getBody().getNew_version()) > version) {
                        listener.success(updateAppBean);
                    }
                } catch (Exception e) {
                    Log.e("升级接口报错", e.toString());
                }
            }

            @Override
            public void onError(Response<String> response) {
                listener.fail("服务器连接失败");
            }
        });
    }


    //下载apk文件并跳转(第二次请求，get)
    private void downloadApp(String apk_url) {
        OkGo.<File>get(apk_url).tag(this).execute(new FileCallback() {
            @Override
            public void onError(Response<File> response) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
            }

            @Override
            public void onSuccess(Response<File> response) {
                if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
                String filePath = response.body().getAbsolutePath();
                boolean b = installApp(filePath);
            }

            @Override
            public void downloadProgress(Progress progress) {
                if (mDownloadDialog == null) {
                    // 构造软件下载对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("正在更新");
                    // 给下载对话框增加进度条
                    final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View v = inflater.inflate(R.layout.item_progress, null);
                    mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
                    builder.setView(v);
                    mDownloadDialog = builder.create();
                    mDownloadDialog.show();
                }
                mProgress.setProgress((int) (progress.fraction * 100));
            }
        });
    }

    public boolean installApp(String apkPath) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "install", "-r", "-i", "com.wl.wlflatproject", apkPath).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            Log.e("静默安装报错", e.toString());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.e("result", "" + errorMsg.toString());
        return successMsg.toString().equalsIgnoreCase("success");
    }


    public interface DataRequestListener<T> {
        //请求成功
        void success(T data);

        //请求失败
        void fail(String msg);
    }

}