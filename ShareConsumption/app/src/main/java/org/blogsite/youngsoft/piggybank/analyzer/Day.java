package org.blogsite.youngsoft.piggybank.analyzer;

import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 분석된 데이터를 일자별로 저장하기 위한 클래스
 *
 * Created by klee on 2018-01-29.
 */
public class Day implements Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private final List<Data> dataList;
    private final List<Card> cardList;
    private int currentDay;

    public Day() {
        currentDay = -1;
        dataList = new ArrayList<Data>();

        /**
         * 일자별 카드 데이터 초기화(null로 설정)
         */
        cardList = new ArrayList<Card>();
        for (int i = 0; i < 32; i++) {
            cardList.add(i, null);
        }
    }

    /**
     * 분석 데이터 추가
     *
     * @param data
     */
    public void addData(Data data) {
        dataList.add(data);
        int day = data.getDay();
        Card card = new Card(data.getCard());
        card.addCategory(data.getCategory(), data.getAmount());
        addCard(day, card);
    }

    /**
     * 일자별 카드 데이터 추가
     *
     * @param day
     * @param card
     */
    private void addCard(int day, Card card) {
        Card old = cardList.get(day);
        if (old == null) {
            cardList.add(day, card);
        } else {
            Category category = card.getCategories();
            for (int i = 0; i < Category.MAX_CATEGORY + 1; i++) {
                CategoryEnum cat = CategoryEnum.getByValue(i);
                old.addCategory(cat, category.getCategotyValue(cat));
            }
        }
    }

    /**
     * 모든 데이터 목록
     *
     * @return
     */
    public List<Data> getDataList() {
        return dataList;
    }

    /**
     * 모든 카드 목록
     *
     * @return
     */
    public List<Card> getCardList() {
        return cardList;
    }

    public Card getCard(int day) {
        return cardList.get(day);
    }

    public Category getCategories(int day) {
        Card card = cardList.get(day);
        if (card != null) {
            return card.getCategories();
        } else {
            return null;
        }
    }
}
