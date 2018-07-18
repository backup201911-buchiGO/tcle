package org.blogsite.youngsoft.piggybank.adapter;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by klee on 2018-02-02.
 */

public class DonationInfoListData {
    public int cardResId;
    public String cardName;
    public int categorydResId;
    public String categoryName;
    public int threshold;
    public int percent;
    public boolean overall;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<DonationInfoListData> ALPHA_COMPARATOR = new Comparator<DonationInfoListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(DonationInfoListData mListDate_1, DonationInfoListData mListDate_2) {
            return sCollator.compare(mListDate_1.cardName, mListDate_2.cardName);
        }
    };
}
