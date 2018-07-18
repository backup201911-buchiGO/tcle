package org.blogsite.youngsoft.piggybank.setting;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class DonationInfoList implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    ArrayList<DonationInfo> donationInfoList;

    public DonationInfoList(){
        donationInfoList = new ArrayList<DonationInfo>();
    }

    public void setDonationInfo(DonationInfo info){
        donationInfoList.add(info);
    }

    public ArrayList<DonationInfo> getDonationInfoList(){
        return donationInfoList;
    }

    public int getDonationInfoCount(){
        return donationInfoList.size();
    }

}
