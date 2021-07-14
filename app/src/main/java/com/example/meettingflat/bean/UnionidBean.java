package com.example.meettingflat.bean;

import java.util.List;

public class UnionidBean {

    private Integer errcode;
    private String errmsg;
    private ResultBean result;
    private String request_id;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public static class ResultBean {
        private Boolean active;
        private Boolean admin;
        private String avatar;
        private Boolean boss;
        private List<Integer> dept_id_list;
        private List<DeptOrderListBean> dept_order_list;
        private String email;
        private Boolean exclusive_account;
        private Boolean hide_mobile;
        private String job_number;
        private List<LeaderInDeptBean> leader_in_dept;
        private String mobile;
        private String name;
        private Boolean real_authed;
        private String remark;
        private List<RoleListBean> role_list;
        private Boolean senior;
        private String state_code;
        private String telephone;
        private String title;
        private UnionEmpExtBean union_emp_ext;
        private String unionid;
        private String userid;
        private String work_place;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Boolean getAdmin() {
            return admin;
        }

        public void setAdmin(Boolean admin) {
            this.admin = admin;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Boolean getBoss() {
            return boss;
        }

        public void setBoss(Boolean boss) {
            this.boss = boss;
        }

        public List<Integer> getDept_id_list() {
            return dept_id_list;
        }

        public void setDept_id_list(List<Integer> dept_id_list) {
            this.dept_id_list = dept_id_list;
        }

        public List<DeptOrderListBean> getDept_order_list() {
            return dept_order_list;
        }

        public void setDept_order_list(List<DeptOrderListBean> dept_order_list) {
            this.dept_order_list = dept_order_list;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getExclusive_account() {
            return exclusive_account;
        }

        public void setExclusive_account(Boolean exclusive_account) {
            this.exclusive_account = exclusive_account;
        }

        public Boolean getHide_mobile() {
            return hide_mobile;
        }

        public void setHide_mobile(Boolean hide_mobile) {
            this.hide_mobile = hide_mobile;
        }

        public String getJob_number() {
            return job_number;
        }

        public void setJob_number(String job_number) {
            this.job_number = job_number;
        }

        public List<LeaderInDeptBean> getLeader_in_dept() {
            return leader_in_dept;
        }

        public void setLeader_in_dept(List<LeaderInDeptBean> leader_in_dept) {
            this.leader_in_dept = leader_in_dept;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getReal_authed() {
            return real_authed;
        }

        public void setReal_authed(Boolean real_authed) {
            this.real_authed = real_authed;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<RoleListBean> getRole_list() {
            return role_list;
        }

        public void setRole_list(List<RoleListBean> role_list) {
            this.role_list = role_list;
        }

        public Boolean getSenior() {
            return senior;
        }

        public void setSenior(Boolean senior) {
            this.senior = senior;
        }

        public String getState_code() {
            return state_code;
        }

        public void setState_code(String state_code) {
            this.state_code = state_code;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public UnionEmpExtBean getUnion_emp_ext() {
            return union_emp_ext;
        }

        public void setUnion_emp_ext(UnionEmpExtBean union_emp_ext) {
            this.union_emp_ext = union_emp_ext;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getWork_place() {
            return work_place;
        }

        public void setWork_place(String work_place) {
            this.work_place = work_place;
        }

        public static class UnionEmpExtBean {
        }

        public static class DeptOrderListBean {
            private Integer dept_id;
            private Integer order;

            public Integer getDept_id() {
                return dept_id;
            }

            public void setDept_id(Integer dept_id) {
                this.dept_id = dept_id;
            }

            public Integer getOrder() {
                return order;
            }

            public void setOrder(Integer order) {
                this.order = order;
            }
        }

        public static class LeaderInDeptBean {
            private Integer dept_id;
            private Boolean leader;

            public Integer getDept_id() {
                return dept_id;
            }

            public void setDept_id(Integer dept_id) {
                this.dept_id = dept_id;
            }

            public Boolean getLeader() {
                return leader;
            }

            public void setLeader(Boolean leader) {
                this.leader = leader;
            }
        }

        public static class RoleListBean {
            private String group_name;
            private Integer id;
            private String name;

            public String getGroup_name() {
                return group_name;
            }

            public void setGroup_name(String group_name) {
                this.group_name = group_name;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
