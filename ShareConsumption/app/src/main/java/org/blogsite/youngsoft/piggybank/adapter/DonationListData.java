package org.blogsite.youngsoft.piggybank.adapter;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by klee on 2018-02-02.
 */

public class DonationListData {
    public int resId;
    public String cardName;
    public String categoryName;
    public String amountValue;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<DonationListData> ALPHA_COMPARATOR = new Comparator<DonationListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(DonationListData mListDate_1, DonationListData mListDate_2) {
            return sCollator.compare(mListDate_1.cardName, mListDate_2.cardName);
        }
    };
}
