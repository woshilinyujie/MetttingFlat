package com.example.meettingflat.bean;

import java.util.List;

public class UserBean {

    private Integer code;
    private String msg;
    private String note;
    private List<DataBean> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private Integer id;
        private String participants;
        private String dingding;
        private String tel;
        private String bluetooth;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getParticipants() {
            return participants;
        }

        public void setParticipants(String participants) {
            this.participants = participants;
        }

        public String getDingding() {
            return dingding;
        }

        public void setDingding(String dingding) {
            this.dingding = dingding;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getBluetooth() {
            return bluetooth;
        }

        public void setBluetooth(String bluetooth) {
            this.bluetooth = bluetooth;
        }
    }
}
