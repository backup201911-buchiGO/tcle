package org.blogsite.youngsoft.piggybank.setting;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;

public class DonationHistory implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private String donationName;
    private int amount = -1;
    private int year = -1;
    private int month = -1;
    private int day = -1;
    private String account = "";
    private String bankname = "";
    private String home = "";
    private String tel = "";
    private String address = "";
    private String category = "";

    public String getDonationName() {
        return donationName;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
