package org.blogsite.youngsoft.piggybank.data;

import java.util.Comparator;

/**
 * Created by klee on 2018-01-06.
 */

/**
 * 정렬 가능한 필드를 위한 클래스
 */
public final class CardComparators {
    private CardComparators(){
        //no instance
    }

    public static Comparator<CardData>  getCardProducerComparator(){
            return new CardProducerComparator();
    }

    public static Comparator<CardData>  getCardNameComparator(){
        return new CardNameComparator();
    }

    public static Comparator<CardData>  getCardAmountComparator(){
        return new CardAmountComparator();
    }

    public static Comparator<CardData>  getCardUseDateComparator(){
        return new CardUseDateComparator();
    }

    /**
     * 카드 Procedure 비교 연선
     */
    private static class CardProducerComparator implements Comparator<CardData> {
        @Override
        public int compare(final CardData card1, final CardData card2) {
            return card1.getProducer().getName().compareTo(card2.getProducer().getName());
        }
    }

    /**
     * 카드 이름 비교 연산
     */
    private static class CardNameComparator implements Comparator<CardData> {
        @Override
        public int compare(final CardData card1, final CardData card2) {
            return card1.getName().compareTo(card2.getName());
        }
    }

    /**
     * 카드 사용 금액 비교 연산
     */
    private static class CardAmountComparator implements Comparator<CardData> {
        @Override
        public int compare(final CardData card1, final CardData card2) {
            if (card1.getAmount() < card2.getAmount()) return -1;
            if (card1.getAmount() > card2.getAmount()) return 1;
            return 0;
        }
    }

    /**
     * long type 카드 사용 일자 비교 연산
     */
    private static class CardUseDateComparator implements Comparator<CardData> {
        @Override
        public int compare(final CardData card1, final CardData card2) {
            if (card1.getTimestamp() < card2.getTimestamp()) return -1;
            if (card1.getTimestamp() > card2.getTimestamp()) return 1;
            return 0;
        }
    }
}
