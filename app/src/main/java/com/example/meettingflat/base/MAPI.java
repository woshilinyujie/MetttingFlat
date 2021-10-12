package com.example.meettingflat.base;


import android.content.Context;
import android.widget.Toast;

import com.example.meettingflat.Utils.GsonUtils;
import com.example.meettingflat.bean.AdBean;
import com.example.meettingflat.bean.BaseBean;
import com.example.meettingflat.bean.GetLinkBean;
import com.example.meettingflat.bean.MeetingBean;
import com.example.meettingflat.bean.ErrBean;
import com.example.meettingflat.bean.MobleJson;
import com.example.meettingflat.bean.TokenBean;
import com.example.meettingflat.bean.UidBean;
import com.example.meettingflat.bean.UnionidBean;
import com.example.meettingflat.bean.UnionidJson;
import com.example.meettingflat.bean.UserBean;
import com.example.meettingflat.bean.UpBean;
import com.example.meettingflat.bean.UpJson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MAPI {
    private String appkey = "dingkgtxgjnxpvb0rjbz";
    private String appsecret = "10OLUP8DfXEKQzUci5-w5uN33ifNiuUJs-naU1FBBFljVOUw24zlUjvSn42pztxD";
    private String phone = "18606889455";
    private String ip = "http://118.31.32.134:10301/api/";
    private String upData = "meetingDoor";//上传们数据
    private String link = "meetingDoorLinkage";
    private String getLink = "meetingDoorIsLinkage";
    private String getAd = "meeting/av";


    /**
     * 获取token
     *
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
                        if (tokenBean != null && tokenBean.getErrcode() != 0) {
                            showT(context, tokenBean.getErrmsg());
                        } else if (tokenBean != null) {
                            call.call(tokenBean.getAccess_token());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if (bean != null)
                            showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 根据手机获取uid
     *
     * @param context
     * @param token
     * @param call
     */
    public void getUid(Context context, String token, CallString call) {
        String url = "https://oapi.dingtalk.com/topapi/v2/user/getbymobile?" + "access_token=" + token;
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
                        if (uidBean != null && uidBean.getErrcode() != 0) {
                            showT(context, uidBean.getErrmsg());
                        } else if (uidBean != null) {
                            getUnionid(context, token, uidBean.getResult().getUserid(), call);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if (bean != null)
                            showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 获取getUnionid
     *
     * @param context
     * @param token
     * @param uid
     * @param call
     */
    public void getUnionid(Context context, String token, String uid, CallString call) {
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
                        } else if (userBean != null) {
                            call.call(userBean.getResult().getUnionid());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if (bean != null)
                            showT(context, bean.getMessage());
                    }
                });
    }


    /**
     * 获取会议信息
     *
     * @param context
     * @param token
     * @param userid
     * @param date
     * @param date1
     * @param call
     */
    public void getMeeting(Context context, String token, String userid, String date, String date1, IMeeting call) {
        String url = "https://api.dingtalk.com/v1.0/calendar/users/" + userid + "/calendars/primary/events";
        OkGo.<String>get(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .headers("x-acs-dingtalk-access-token", token)
                .params("timeMin", date)
                .params("timeMax", date1)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        MeetingBean meetingBean = GsonUtils.GsonToBean(json, MeetingBean.class);
                        if (meetingBean.getCode() != null && meetingBean.getCode().equals("InvalidAuthentication")) {
                            showT(context, meetingBean.getErrmsg());
                            call.error();
                        } else if (meetingBean != null) {
                            call.call(meetingBean);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        String json = response.body();
                        ErrBean bean = GsonUtils.GsonToBean(json, ErrBean.class);
                        if (bean != null && bean.getCode() != null && bean.getCode().equals("InvalidAuthentication")) {
                            call.error();
                        } else if (bean != null) {
                            call.error();
                            showT(context, bean.getMessage());
                        }
                    }
                });
    }

    public void searchALL(Context context, CallUser callUser) {
        String url = ip + "meeting/search";
        OkGo.<String>post(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        UserBean userBean = GsonUtils.GsonToBean(json, UserBean.class);
                        if (userBean.getCode() == 200 && userBean.getMsg().equals("success")) {
                            callUser.call(userBean);
                        } else {
                            showT(context, userBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void up(Context context, String id, String name, boolean isLink, Call call) {
        String url = ip + upData;
        UpJson upJson = new UpJson();
        if (isLink) {
            upJson.setTab("0");
        } else {
            upJson.setTab("1");
        }
        UpJson.MeetingdoorBean meetingdoorBean = new UpJson.MeetingdoorBean();
        meetingdoorBean.setDoor_id(id);
        meetingdoorBean.setLinkage(id);
        meetingdoorBean.setMeeting_name(name);
        upJson.setMeetingdoor(meetingdoorBean);
        String s = GsonUtils.GsonString(upJson);
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
                        UpBean upBean = GsonUtils.GsonToBean(json, UpBean.class);
                        if (upBean.getCode() == 200 && upBean.getMsg().equals("success")) {
                            call.call();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void setLink(Context context, String id, boolean isLink, String name, Call call) {
        String url = ip + link;
        UpJson upJson = new UpJson();
        UpJson.MeetingdoorBean meetingdoorBean = new UpJson.MeetingdoorBean();
        meetingdoorBean.setDoor_id(id);
        meetingdoorBean.setLinkage(id);
        if (isLink) {
            upJson.setTab("1");
        } else {
            upJson.setTab("0");
        }
        meetingdoorBean.setMeeting_name(name);
        upJson.setMeetingdoor(meetingdoorBean);
        String s = GsonUtils.GsonString(upJson);
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
                        BaseBean baseBean = GsonUtils.GsonToBean(json, BaseBean.class);
                        if(baseBean.getCode()==200&&baseBean.getMsg().equals("success")){
                            call.call();                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }


    public void getLink(Context context, String id,  CallLink call) {
        String url = ip + getLink+"/"+id;
        OkGo.<String>get(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        GetLinkBean getLinkBean = GsonUtils.GsonToBean(json, GetLinkBean.class);
                        if(getLinkBean.getCode()==200&&getLinkBean.getMsg().equals("success")&&getLinkBean.getData()!=null){
                            call.call(getLinkBean);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void getAd(Context context,  CallAd call) {
//        String url = ip +getAd;
        String url = "http://120.24.254.13:10301/api/meeting/av";
        OkGo.<String>get(url)
                .tag(context)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        AdBean adBean = GsonUtils.GsonToBean(json, AdBean.class);
                        if(adBean.getCode()==200&&adBean.getMsg().equals("success")) {
                            call.call(adBean);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }
    public void showT(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public interface CallString {
        void call(String token);
    }

    public interface Call {
        void call();
    }

    public interface IMeeting {
        void call(MeetingBean bean);

        void error();
    }

    public interface CallUser {
        void call(UserBean bean);
    }
    public interface CallLink {
        void call(GetLinkBean bean);
    }
    public interface CallAd {
        void call(AdBean bean);
    }
}