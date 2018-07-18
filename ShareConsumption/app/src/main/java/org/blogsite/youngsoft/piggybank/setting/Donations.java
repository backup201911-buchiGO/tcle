package org.blogsite.youngsoft.piggybank.setting;

import android.content.Context;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.DonationEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;

public class Donations implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private String category = SmsUtils.getResource(R.string.catagory_etc);
    private String name = "";
    private String address = "";
    private String home = "";
    private String tel = "";
    private String bankname = "";
    private String account = "";
    private int resId = -1;
    private String resourceName = "";
    private String packName;
    private transient Context context;

    public Donations(Context context){
        packName = context.getApplicationContext().getPackageName();
        this.context = context;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getResId() {
        if(resourceName!=null && !"".equals(resourceName)){
            int id = convertResource();
            if(id!=0){
                return id;
            }else{
                return resId;
            }
        }else {
            return resId;
        }
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getPackName() {
        return packName;
    }

    private int convertResource(){
        return SmsUtils.convertResource(context, resourceName, packName);
    }
}
