package com.example.meettingflat.Activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meettingflat.R;
import com.example.meettingflat.Utils.DateUtils;
import com.example.meettingflat.base.MAPI;
import com.example.meettingflat.bean.MeetingBean;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurrentMeetingActivity extends AppCompatActivity {
    private DateUtils dateUtils;
    private MyAdapter mAdapter;
    private MAPI mapi;
    private String token;
    private String uid;
    private String mMeetingAddress ;//选择的会议室
    private List<MeetingBean.EventsBean> list=new ArrayList<>();
    private TextView meetAddress;
    private RelativeLayout back;
    private SmartRefreshLayout refreshView;
    private RecyclerView recyclerView;
    private LinearLayout message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_cuurent_meet);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        meetAddress = findViewById(R.id.meet_address);
        back = findViewById(R.id.back);
        refreshView = findViewById(R.id.refresh_view);
        recyclerView = findViewById(R.id.recyclerView);
        message = findViewById(R.id.message);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                list.clear();
                quest();
            }
        });
    }

    private void initData() {
        mapi = new MAPI();
        dateUtils = DateUtils.getInstance();
        mMeetingAddress=getIntent().getStringExtra("meetAddress");
        meetAddress.setText(mMeetingAddress);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter();
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        refreshView.autoRefresh(100);
    }




    /**
     * 网络请求
     */
    private void quest() {
        long time = System.currentTimeMillis();
        String startTime = dateUtils.dateFormatStart(time);
        String endTime = dateUtils.dateFormatEnd(time);
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(uid)) {
            mapi.getToken(CurrentMeetingActivity.this, new MAPI.CallString() {
                @Override
                public void call(String token) {
                    CurrentMeetingActivity.this.token = token;
                    mapi.getUid(CurrentMeetingActivity.this, token, new MAPI.CallString() {
                        @Override
                        public void call(String uid) {
                            mapi.getMeeting(CurrentMeetingActivity.this, token, uid, startTime, endTime, new MAPI.IMeeting() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void call(MeetingBean bean) {
                                    refreshView.finishRefresh();
                                    CurrentMeetingActivity.this.uid = uid;
                                    //赛选当前会议室
                                    if(bean.getEvents()!=null&&bean.getEvents().size()>0){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        message.setVisibility(View.GONE);
                                        for(int x=0;x<bean.getEvents().size();x++){
                                            MeetingBean.EventsBean.LocationBean location = bean.getEvents().get(x).getLocation();
                                            if (location!=null&&location.getDisplayName().equals(mMeetingAddress)&&bean.getEvents().get(x).getStatus().equals("confirmed")) {
                                                list.add(bean.getEvents().get(x));
                                            }
                                        }
                                        if(list.size()>0){
                                            recyclerView.setVisibility(View.VISIBLE);
                                            message.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
                                        }else{
                                            message.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.GONE);
                                        }
                                    }else{
                                        message.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);

                                    }
                                }

                                @Override
                                public void error() {
                                    refreshView.finishRefresh();
                                    CurrentMeetingActivity.this.token = null;
                                    CurrentMeetingActivity.this.uid = null;
                                }
                            });
                        }
                    });
                }
            });
        } else {
            mapi.getMeeting(CurrentMeetingActivity.this, token, uid, startTime, endTime, new MAPI.IMeeting() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void call(MeetingBean bean) {
                    refreshView.finishRefresh();
                    if(bean.getEvents()!=null&&bean.getEvents().size()>0){
                        for(int x=0;x<bean.getEvents().size();x++){
                            if (bean.getEvents().get(x).getLocation().getDisplayName().equals(mMeetingAddress)&&bean.getEvents().get(x).getStatus().equals("confirmed")) {
                                list.add(bean.getEvents().get(x));
                            }
                        }

                        if(list.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            message.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }else{
                            message.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }else{
                        message.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void error() {
                    refreshView.finishRefresh();
                    CurrentMeetingActivity.this.token = null;
                    CurrentMeetingActivity.this.uid = null;
                }
            });
        }
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meet_layout, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(CurrentMeetingActivity.MyAdapter.MyHolder holder, int position) {
            MeetingBean.EventsBean eventsBean = list.get(position);
            holder.name.setText(eventsBean.getSummary());
            Date date = dateUtils.transitionTime(eventsBean.getStart().getDateTime());
            Date date1 = dateUtils.transitionTime(eventsBean.getEnd().getDateTime());
            holder.time.setText(dateUtils.dateFormat8(date.getTime()) + "~" + dateUtils.dateFormat8(date1.getTime()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView time;

            public MyHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                time = itemView.findViewById(R.id.time);
            }
        }
    }

}
