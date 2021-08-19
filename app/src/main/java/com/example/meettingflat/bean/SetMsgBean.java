package com.example.meettingflat.bean;

public class SetMsgBean {
    private String msg;
    private int flag;
    public SetMsgBean(String msg){
        this.msg=msg;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
