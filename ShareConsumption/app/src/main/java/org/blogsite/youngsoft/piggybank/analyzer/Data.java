package org.blogsite.youngsoft.piggybank.analyzer;

import com.google.gson.Gson;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.TimeUtils;

import java.io.Serializable;

/**
 * 분석된 카드 메시지 정보를 저장하기 위한 클래스
 *
 * Created by klee on 2018-01-30.
 */
public class Data implements Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private boolean approval;
    private boolean accept;
    private long timestamp;
    private int second;
    private int minute;
    private int hour;
    private int day;
    private int month;
    private int year;
    private int amount;
    private CategoryEnum category;
    private CardEnum card;
    private String body;

    public Data() {
        approval = false;
        accept = false;
        timestamp = -1L;
        second = -1;
        minute = -1;
        hour = -1;
        day = -1;
        month = -1;
        year = -1;
        amount = -1;
        category = CategoryEnum.Unclassified;
        card = CardEnum.UNKNOWN_CARD;
        body = null;
    }

    /**
     * 결재 승인/취소
     *
     * @return
     */
    public boolean isApproval() {
        return approval;
    }

    /**
     * 결재 승인/취소
     *
     * @param approval
     */
    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    /**
     * 분석에 성공했는지 여부
     *
     * @return
     */
    public boolean isAccept() {
        return accept;
    }

    /**
     * 메지시를 받은 시각
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 메지시를 받은 시각
     *
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        TimeUtils time = new TimeUtils(timestamp);
        second = time.getSeconds();
        minute = time.getMinutes();
        hour = time.getHour();
        day = time.getDay();
        month = time.getMonth();
        year = time.getYear();
    }

    /**
     * 분석에 성공 했는지 여부
     *
     * @param accept
     */
    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    /**
     * 메시지를 받은 초
     *
     * @return
     */
    public int getSecond() {
        return second;
    }

    /**
     * 메시지를 받은 분
     *
     * @return
     */
    public int getMinute() {
        return minute;
    }

    /**
     * 메시지를 받은 시
     *
     * @return
     */
    public int getHour() {
        return hour;
    }

    /**
     * 메시지를 받은 일
     *
     * @return
     */
    public int getDay() {
        return day;
    }

    /**
     * 메시지를 받은 월
     *
     * @return
     */
    public int getMonth() {
        return month;
    }

    /**
     * 메시지를 받은 연도
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     * 카드 사용 금액
     *
     * @return
     */
    public int getAmount() {
        return amount;
    }

    /**
     * 카드 사용 금액
     *
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * 카드 사용 내역 분류
     *
     * @return
     */
    public CategoryEnum getCategory() {
        return category;
    }

    /**
     * 카드 사용 내역 분류
     *
     * @param category
     */
    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    /**
     * 카드 이름
     *
     * @return
     */
    public CardEnum getCard() {
        return card;
    }

    /**
     * 카드 이름
     *
     * @param card
     */
    public void setCard(CardEnum card) {
        this.card = card;
    }

    /**
     * 메시지 본문
     *
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * 메시지 본문
     *
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String s = gson.toJson(this);

//        Data d = gson.fromJson(s, this.getClass());
//        System.out.println(d.getCard().getName());
        return s;
    }
}
