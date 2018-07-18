package org.blogsite.youngsoft.piggybank.setting;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;

public class DonationInfo implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private String card = null;
    private String category = null;
    private int threshold = 50000;
    private int percent = 50;
    private boolean thresholdOverall = false;
    private int amount = 0;


    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isThresholdOverall() {
        return thresholdOverall;
    }

    public void setThresholdOverall(boolean thresholdOverall) {
        this.thresholdOverall = thresholdOverall;
    }

    public int getThresholdOverall(){
        return thresholdOverall==true ? 1 : 0;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
