package com.example.meettingflat.bean;

public class GetLinkBean {

    private Integer code;
    private String msg;
    private String note;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private Integer id;
        private String meeting_name;
        private String door_id;
        private String linkage;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMeeting_name() {
            return meeting_name;
        }

        public void setMeeting_name(String meeting_name) {
            this.meeting_name = meeting_name;
        }

        public String getDoor_id() {
            return door_id;
        }

        public void setDoor_id(String door_id) {
            this.door_id = door_id;
        }

        public String getLinkage() {
            return linkage;
        }

        public void setLinkage(String linkage) {
            this.linkage = linkage;
        }
    }
}
