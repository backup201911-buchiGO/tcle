package org.blogsite.youngsoft.piggybank.setting;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DonationList implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private HashMap<String, ArrayList<Donations>> donations;

    public DonationList(){
        donations = new HashMap<String, ArrayList<Donations>>();
    }

    public void setDonations(String category, Donations donation){
        if(donations.containsKey(category)){
            ArrayList<Donations> list = donations.get(category);
            list.add(donation);
        }else{
            ArrayList<Donations> list = new ArrayList<Donations>();
            list.add(donation);
            donations.put(category, list);
        }
    }

    public ArrayList<Donations> getDonations(String category){
        return donations.get(category);
    }

    public HashMap<String, ArrayList<Donations>> getDonations(){
        return donations;
    }
}
