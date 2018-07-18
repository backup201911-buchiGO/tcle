package org.blogsite.youngsoft.piggybank.adapter;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by klee on 2018-02-02.
 */

public class CardListData {
    public int resId;
    public String cardName;
    public String amountValue;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<CardListData> ALPHA_COMPARATOR = new Comparator<CardListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(CardListData mListDate_1, CardListData mListDate_2) {
            return sCollator.compare(mListDate_1.cardName, mListDate_2.cardName);
        }
    };
}
