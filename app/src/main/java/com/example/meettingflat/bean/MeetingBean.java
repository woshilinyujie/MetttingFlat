package com.example.meettingflat.bean;

import java.util.List;

public class MeetingBean {
    private String code;
    private String errmsg;
    private List<EventsBean> events;
    private String requestId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<EventsBean> getEvents() {
        return events;
    }

    public void setEvents(List<EventsBean> events) {
        this.events = events;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public static class EventsBean {
        private List<AttendeesBean> attendees;
        private String createTime;
        private String description;
        private EndBean end;
        private String id;
        private Boolean isAllDay;
        private LocationBean location;
        private OrganizerBean organizer;
        private StartBean start;
        private String status;
        private String summary;
        private String updateTime;

        public List<AttendeesBean> getAttendees() {
            return attendees;
        }

        public void setAttendees(List<AttendeesBean> attendees) {
            this.attendees = attendees;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public EndBean getEnd() {
            return end;
        }

        public void setEnd(EndBean end) {
            this.end = end;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getIsAllDay() {
            return isAllDay;
        }

        public void setIsAllDay(Boolean isAllDay) {
            this.isAllDay = isAllDay;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public OrganizerBean getOrganizer() {
            return organizer;
        }

        public void setOrganizer(OrganizerBean organizer) {
            this.organizer = organizer;
        }

        public StartBean getStart() {
            return start;
        }

        public void setStart(StartBean start) {
            this.start = start;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public static class EndBean {
            private String dateTime;
            private String timeZone;

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public String getTimeZone() {
                return timeZone;
            }

            public void setTimeZone(String timeZone) {
                this.timeZone = timeZone;
            }
        }

        public static class LocationBean {
            private String displayName;

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }
        }

        public static class OrganizerBean {
            private String displayName;
            private String id;
            private Boolean self;

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Boolean getSelf() {
                return self;
            }

            public void setSelf(Boolean self) {
                this.self = self;
            }
        }

        public static class StartBean {
            private String dateTime;
            private String timeZone;

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public String getTimeZone() {
                return timeZone;
            }

            public void setTimeZone(String timeZone) {
                this.timeZone = timeZone;
            }
        }

        public static class AttendeesBean {
            private String displayName;
            private String id;
            private String responseStatus;
            private Boolean self;

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getResponseStatus() {
                return responseStatus;
            }

            public void setResponseStatus(String responseStatus) {
                this.responseStatus = responseStatus;
            }

            public Boolean getSelf() {
                return self;
            }

            public void setSelf(Boolean self) {
                this.self = self;
            }
        }
    }
}
