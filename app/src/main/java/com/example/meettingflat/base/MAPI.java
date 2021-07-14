package com.example.meettingflat.base;


import android.content.Context;
import android.widget.Toast;

import com.example.meettingflat.Utils.GsonUtils;
import com.example.meettingflat.bean.MeetingBean;
import com.example.meettingflat.bean.ErrBean;
import com.example.meettingflat.bean.MobleJson;
import com.example.meettingflat.bean.TokenBean;
import com.example.meettingflat.bean.UidBean;
import com.example.meettingflat.bean.UnionidBean;
import com.example.meettingflat.bean.UnionidJson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MAPI {
    private String appkey = "dingkgtxgjnxpvb0rjbz";
    private String appsecret = "10OLUP8DfXEKQzUci5-w5uN33ifNiuUJs-naU1FBBFljVOUw24zlUjvSn42pztxD";
    private String phone="18606889455";


    /**
     * 获取token
     * @param context
     * @param call
     */
    public void getToken(Context context, CallString call) {
        String url = "https://oapi.dingtalk.com/gettoken?appkey=" + appkey + "&appsecret=" + appsecret;
        OkGo.<String>get(url)                            // 请求方式和请求url
                .tag(context)                       // 请求的 tag, 主要用于取消对应的请求
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        TokenBean tokenBean = GsonUtils.GsonToBean(json, TokenBean.class);
                        if (tokenBean!=null&&tokenBean.getErrcode() != 0) {
                            showT(context, tokenBean.getErrmsg());
                        } else if(tokenBean!=null){
                            call.call(tokenBean.getAccess_token());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if(bean!=null)
                        showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 根据手机获取uid
     * @param context
     * @param token
     * @param call
     */
    public void getUid(Context context,String token,CallString call) {
        String url ="https://oapi.dingtalk.com/topapi/v2/user/getbymobile?"+"access_token="+token;
        MobleJson mobleJson = new MobleJson();
        mobleJson.setMobile(phone);
        String s = GsonUtils.GsonString(mobleJson);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, s);
        OkGo.<String>post(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .headers("Content-Type", "application/json")
                .upRequestBody(requestBody)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        UidBean uidBean = GsonUtils.GsonToBean(json, UidBean.class);
                        if (uidBean!=null&&uidBean.getErrcode() != 0) {
                            showT(context, uidBean.getErrmsg());
                        } else if(uidBean!=null){
                            getUnionid(context,token,uidBean.getResult().getUserid(),call);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if(bean!=null)
                        showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 获取getUnionid
     * @param context
     * @param token
     * @param uid
     * @param call
     */
    public void getUnionid(Context context, String token,String uid,CallString call) {
        String url = "https://oapi.dingtalk.com/topapi/v2/user/get?access_token=" + token;
        UnionidJson unionidJson = new UnionidJson();
        unionidJson.setLanguage("zh_CN");
        unionidJson.setUserid(uid);
        String s = GsonUtils.GsonString(unionidJson);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, s);
        OkGo.<String>post(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .upRequestBody(requestBody)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        UnionidBean userBean = GsonUtils.GsonToBean(json, UnionidBean.class);
                        if (userBean.getErrcode() != 0) {
                            showT(context, userBean.getErrmsg());
                        } else if(userBean!=null){
                           call.call(userBean.getResult().getUnionid());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if(bean!=null)
                        showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 获取会议信息
     * @param context
     * @param token
     * @param userid
     * @param date
     * @param date1
     * @param call
     */
    public void getMeeting(Context context, String token, String userid,String date,String  date1,IMeeting call) {
        String url = "https://api.dingtalk.com/v1.0/calendar/users/"+userid+"/calendars/primary/events";
        OkGo.<String>get(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .headers("x-acs-dingtalk-access-token", token)
                .params("timeMin",date)
                .params("timeMax",date1)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        MeetingBean meetingBean = GsonUtils.GsonToBean(json, MeetingBean.class);
                        if (meetingBean.getCode()!=null&&meetingBean.getCode().equals("InvalidAuthentication")){
                            showT(context, meetingBean.getErrmsg());
                            call.error();
                        } else if(meetingBean!=null){
                            call.call(meetingBean);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if(bean!=null&&bean.getCode()!=null&&bean.getCode().equals("InvalidAuthentication")){
                            call.error();
                        }else if(bean!=null){
                            call.error();
                            showT(context, bean.getMessage());
                        }
                    }
                });
    }

    public void showT(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public interface CallString {
        void call(String token);
    }

    public interface IMeeting {
        void call(MeetingBean bean);
        void error();
    }
}