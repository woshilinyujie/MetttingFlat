package com.example.meettingflat.bean;

public class UpJson {

    private MeetingdoorBean meetingdoor;
    private String tab;

    public MeetingdoorBean getMeetingdoor() {
        return meetingdoor;
    }

    public void setMeetingdoor(MeetingdoorBean meetingdoor) {
        this.meetingdoor = meetingdoor;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public static class MeetingdoorBean {
        private String meeting_name;
        private String door_id;
        private String linkage;

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
