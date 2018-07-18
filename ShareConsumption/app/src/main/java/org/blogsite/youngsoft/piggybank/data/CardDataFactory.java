package org.blogsite.youngsoft.piggybank.data;

/**
 * Created by klee on 2018-01-06.
 */

/**
 * 카드 데이터 펙토리
 * 최종 데이터 베이스와 연동 또는 SMS Receiver 또는 Reader와 연동
 */

import java.util.ArrayList;
import java.util.List;

public final class CardDataFactory {
    public static List<CardData> createCardData(){
/*
        final CardEnum shinCheckCard = CardEnum.SHINHAN_CHECKCARD;
        CardProducer shinCheckCardProducer = new CardProducer(shinCheckCard);
        //final CardProducer producer, final CategoryEnum category, final int amount, final long useDate, final boolean approval
        final CardData sd1 = new CardData(shinCheckCardProducer, CategoryEnum.Communication, 100000, 1513151735000L, true);
        final CardData sd2 = new CardData(shinCheckCardProducer, CategoryEnum.Culture, 200000, 1512720476000L, true);
        final CardData sd3 = new CardData(shinCheckCardProducer, CategoryEnum.Dues, 50000, 1512381730000L, true);
        final CardData sd4 = new CardData(shinCheckCardProducer, CategoryEnum.Meal, 500000, 1511565590000L, true);
*/
        final List<CardData> cards = new ArrayList<>();
/*
        cards.add(sd1);
        cards.add(sd2);
        cards.add(sd3);
        cards.add(sd4);
*/
        return null; //cards;
    }
}
