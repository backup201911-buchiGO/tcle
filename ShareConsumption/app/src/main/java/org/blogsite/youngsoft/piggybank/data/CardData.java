package org.blogsite.youngsoft.piggybank.data;

import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;

/**
 * Created by klee on 2018-01-06.
 */

/**
 * 카드 데이터 구성 클래스
 */
public class CardData implements CardChargable {
    private final CardProducer producer;
    private final boolean approval;
    private final int amount;
    private final long timestamp;
    private final CategoryEnum category;
    private String name;
    private String body;

    public CardData(final CardProducer producer, final CategoryEnum category, final int amount, final long timestamp, final boolean approval, final String body){
        this.producer = producer;
        this.name = producer.getCardName().getName();
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
        this.approval = approval;
        this.body = body;
    }

    public CardProducer getProducer() {
        return producer;
    }

    public CategoryEnum getCategory(){
        return category;
    }

    /**
     * 카드 이름
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 카드 이름 설정
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * 사용 금액
     * @return
     */
    public int getAmount() {
        return amount;
    }

    /**
     * 카드문자 수신 일자 : long type
     * @return
     */
    public long getTimestamp(){
        return timestamp;
    }

    /**
     * 카드 승인/취소
     * @return
     */
    public boolean isApproval(){
        return approval;
    }

    @Override
    public String toString() {
        return producer.getName() + " " + name;
    }

    public String getBody(){
        return body;
    }
}
