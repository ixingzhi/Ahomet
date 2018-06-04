package com.shichuang.ahomet.entify;

/**
 * Created by Administrator on 2018/1/13.
 */

public class User {
    private int id;
    private String uuid;
    private String phone_num;
    private String nickname;
    private String password;
    private String available_amount;
    private String head_pic;
    private String popularity_value;
    private String register_date;
    private String full_name;
    private String gender;
    private String province_id;
    private String city_id;
    private String area_id;
    private String token;
    private String user_score;
    private String available_point;
    private String state;
    private int is_member;  // 1是会员，其他都不是
    private String member_end_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvailable_amount() {
        return available_amount;
    }

    public void setAvailable_amount(String available_amount) {
        this.available_amount = available_amount;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getPopularity_value() {
        return popularity_value;
    }

    public void setPopularity_value(String popularity_value) {
        this.popularity_value = popularity_value;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_score() {
        return user_score;
    }

    public void setUser_score(String user_score) {
        this.user_score = user_score;
    }

    public String getAvailable_point() {
        return available_point;
    }

    public void setAvailable_point(String available_point) {
        this.available_point = available_point;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getIs_member() {
        return is_member;
    }

    public void setIs_member(int is_member) {
        this.is_member = is_member;
    }

    public String getMember_end_time() {
        return member_end_time;
    }

    public void setMember_end_time(String member_end_time) {
        this.member_end_time = member_end_time;
    }
}
